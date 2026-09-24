package dev.celestiacraft.cmi.network.c2s;

import dev.celestiacraft.cmi.common.feature.cargogrid.CargoGridRules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RotateCarriedItemPacket {
	public RotateCarriedItemPacket() {}

	public static void encode(RotateCarriedItemPacket msg, FriendlyByteBuf buf) {
	}

	public static RotateCarriedItemPacket decode(FriendlyByteBuf buf) {
		return new RotateCarriedItemPacket();
	}

	public static void handle(RotateCarriedItemPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player == null) return;
			AbstractContainerMenu menu = player.containerMenu;
			if (menu == null) return;
			ItemStack carried = menu.getCarried();
			if (carried.isEmpty()) return;
			CargoGridRules.toggleRotated(carried);
			menu.setCarried(carried);
			menu.broadcastChanges();
		});
		ctx.setPacketHandled(true);
	}
}
