package dev.celestiacraft.cmi.event.radial;

import cc.sighs.auratip.api.action.Actions;
import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CmiRadialAction {
	public static final ResourceLocation WRENCH_PICKUP = Cmi.loadResource("wrench_pickup");

	public static void register() {
		wrenchPickup();
	}

	public static void wrenchPickup() {
		Actions.register(WRENCH_PICKUP, (params) -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.player == null) {
				return;
			}

			Inventory inventory = minecraft.player.getInventory();

			int slot = inventory.findSlotMatchingItem(AllItems.WRENCH.asStack());
			if (slot == -1) {
				return;
			}

			ItemStack main = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!main.isEmpty()) {
				return;
			}

			ItemStack wrench = inventory.getItem(slot);

			minecraft.player.setItemInHand(InteractionHand.MAIN_HAND, wrench.copy());
			inventory.removeItem(slot, wrench.getCount());
		});
	}
}