package dev.celestiacraft.cmi.network.c2s;

import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.s2c.SyncSpaceElevatorMaterialsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class RequestSpaceElevatorMaterialsPacket {
	private final BlockPos anchorPos;

	public RequestSpaceElevatorMaterialsPacket(BlockPos anchorPos) {
		this.anchorPos = anchorPos.immutable();
	}

	public static void encode(RequestSpaceElevatorMaterialsPacket msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.anchorPos);
	}

	public static RequestSpaceElevatorMaterialsPacket decode(FriendlyByteBuf buf) {
		return new RequestSpaceElevatorMaterialsPacket(buf.readBlockPos());
	}

	public static void handle(RequestSpaceElevatorMaterialsPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player == null || !SpaceElevatorConstructionHandler.isAnchorBlock(player.serverLevel(), msg.anchorPos)) {
				return;
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
