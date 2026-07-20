package dev.celestiacraft.cmi.network.c2s;

import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorConsoleConstructionPacket;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorMaterialsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ConstructSpaceElevatorPacket {
	private final BlockPos anchorPos;

	public ConstructSpaceElevatorPacket(BlockPos anchorPos) {
		this.anchorPos = anchorPos.immutable();
	}

	public static void encode(ConstructSpaceElevatorPacket msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.anchorPos);
	}

	public static ConstructSpaceElevatorPacket decode(FriendlyByteBuf buf) {
		return new ConstructSpaceElevatorPacket(buf.readBlockPos());
	}

	public static void handle(ConstructSpaceElevatorPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player == null) {
				return;
			}
			if (!SpaceElevatorConstructionHandler.isWrench(player.getMainHandItem())) {
				return;
			}

			SpaceElevatorConstructionHandler.ConstructResult result = SpaceElevatorConstructionHandler.tryConstruct(player, msg.anchorPos);
			player.displayClientMessage(Component.translatable(result.translationKey()), false);
			if (result == SpaceElevatorConstructionHandler.ConstructResult.SUCCESS) {
				LevelChunk chunk = player.serverLevel().getChunkAt(msg.anchorPos);
				CmiNetwork.CHANNEL.send(
						PacketDistributor.TRACKING_CHUNK.with(() -> chunk),
						new SyncSpaceElevatorConsoleConstructionPacket(msg.anchorPos)
				);
			}
			SpaceElevatorConstructionRecipe recipe = SpaceElevatorConstructionHandler.getRecipe(player.serverLevel());
			int ingredientCount = recipe == null ? 0 : recipe.ingredients().size();
			int fluidIngredientCount = recipe == null ? 0 : recipe.fluidIngredients().size();
			int[] counts = SpaceElevatorConstructionHandler.getStoredCounts(player.serverLevel(), msg.anchorPos, ingredientCount);
			int[] fluidAmounts = SpaceElevatorConstructionHandler.getStoredFluidAmounts(player.serverLevel(), msg.anchorPos, fluidIngredientCount);
			boolean orbitalCounterpartPresent = SpaceElevatorConstructionHandler.hasOrbitalCounterpart(player.serverLevel(), msg.anchorPos);
			CmiNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncSpaceElevatorMaterialsPacket(msg.anchorPos, counts, fluidAmounts, orbitalCounterpartPresent));
		});
		ctx.setPacketHandled(true);
	}
}
