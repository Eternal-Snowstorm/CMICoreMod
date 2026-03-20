package dev.celestiacraft.cmi.network;

import dev.celestiacraft.cmi.common.recipe.space_elevator_base.SpaceElevatorBaseRecipe;
import dev.celestiacraft.cmi.compat.adastra.AdAstraSpaceElevatorCompat;
import dev.celestiacraft.cmi.compat.adastra.AdAstraSpaceElevatorStationCompat;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorBaseHandler;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorLinkHandler;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.compat.argonauts.ArgonautsIntegration;
import earth.terrarium.adastra.common.handlers.SpaceStationHandler;
import earth.terrarium.adastra.common.handlers.base.SpaceStation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class BuildEarthSpaceElevatorBasePacket {
	private final ResourceKey<Level> dimension;
	private final ChunkPos spaceStationPos;

	public BuildEarthSpaceElevatorBasePacket(ResourceKey<Level> dimension, ChunkPos spaceStationPos) {
		this.dimension = dimension;
		this.spaceStationPos = spaceStationPos;
	}

	public static void encode(BuildEarthSpaceElevatorBasePacket msg, FriendlyByteBuf buf) {
		buf.writeResourceKey(msg.dimension);
		buf.writeChunkPos(msg.spaceStationPos);
	}

	public static BuildEarthSpaceElevatorBasePacket decode(FriendlyByteBuf buf) {
		return new BuildEarthSpaceElevatorBasePacket(buf.readResourceKey(net.minecraft.core.registries.Registries.DIMENSION), buf.readChunkPos());
	}

	public static void handle(BuildEarthSpaceElevatorBasePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player == null) {
				return;
			}

			if (!Planet.EARTH_ORBIT.equals(msg.dimension)) {
				return;
			}

			ServerLevel orbitLevel = player.server.getLevel(msg.dimension);
			if (orbitLevel == null) {
				return;
			}

			if (!Level.OVERWORLD.equals(player.serverLevel().dimension())) {
				notifyPlayer(player, "text.cmi.space_elevator_base.need_earth_launch");
				return;
			}

			if (!isAllowed(player, orbitLevel, msg.spaceStationPos)) {
				return;
			}

			if (SpaceElevatorBaseHandler.hasBuiltStructure(orbitLevel, msg.spaceStationPos)) {
				CmiNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncSpaceElevatorBaseStatePacket(msg.dimension, msg.spaceStationPos, true));
				notifyPlayer(player, "text.cmi.space_elevator_base.already_built");
				return;
			}

			SpaceElevatorBaseRecipe recipe = SpaceElevatorBaseRecipe.getRecipe(orbitLevel, msg.dimension);
			if (recipe == null) {
				return;
			}

			if (!AdAstraSpaceElevatorCompat.hasValidEarthLaunchOrigin(player)) {
				notifyPlayer(player, "text.cmi.space_elevator_base.need_earth_launch");
				return;
			}

			if (!SpaceElevatorBaseRecipe.hasIngredients(player, recipe)) {
				notifyPlayer(player, "text.cmi.space_elevator_base.not_enough_materials");
				return;
			}

			BlockPos groundBasePos = AdAstraSpaceElevatorCompat.getLastEarthLaunchOrigin(player);
			if (!AdAstraSpaceElevatorCompat.buildEarthBaseFromLastLaunch(player, orbitLevel)) {
				notifyPlayer(player, "text.cmi.space_elevator_base.invalid_launch_position");
				return;
			}

			SpaceElevatorBaseRecipe.consumeIngredients(player, recipe);
			SpaceElevatorBaseHandler.markBaseBuilt(orbitLevel, msg.spaceStationPos);
			AdAstraSpaceElevatorStationCompat.decorateEarthOrbitStation(orbitLevel, msg.spaceStationPos);
			if (groundBasePos != null) {
				SpaceElevatorLinkHandler.setGroundBase(orbitLevel, msg.spaceStationPos, Level.OVERWORLD, groundBasePos);
			}
			CmiNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncSpaceElevatorBaseStatePacket(msg.dimension, msg.spaceStationPos, true));
			notifyPlayer(player, "text.cmi.space_elevator_base.success");
		});
		ctx.setPacketHandled(true);
	}

	private static void notifyPlayer(ServerPlayer player, String translationKey) {
		Component message = Component.translatable(translationKey);
		player.displayClientMessage(message, false);
	}

	private static boolean isAllowed(ServerPlayer player, ServerLevel level, ChunkPos targetPos) {
		Set<SpaceStation> stations = new HashSet<>(SpaceStationHandler.getOwnedSpaceStations(player, level));

		if (!ArgonautsIntegration.argonautsLoaded()) {
			return stations.stream().anyMatch(station -> station.position().equals(targetPos));
		}

		for (var member : ArgonautsIntegration.getClientPartyMembers(player.getUUID())) {
			stations.addAll(SpaceStationHandler.getOwnedSpaceStations(member.getId(), level));
		}
		for (var member : ArgonautsIntegration.getClientGuildMembers(player.getUUID())) {
			stations.addAll(SpaceStationHandler.getOwnedSpaceStations(member.getId(), level));
		}

		return stations.stream().anyMatch(station -> station.position().equals(targetPos));
	}
}
