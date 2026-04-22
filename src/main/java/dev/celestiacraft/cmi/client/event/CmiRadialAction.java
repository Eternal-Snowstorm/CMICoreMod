package dev.celestiacraft.cmi.client.event;

import cc.sighs.auratip.api.action.Actions;
import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CmiRadialAction {
	public static final ResourceLocation WRENCH_PICKUP = Cmi.loadResource("wrench_pickup");

	public static void register() {
		wrenchPickup();
	}

	public static void wrenchPickup() {
		Actions.register(WRENCH_PICKUP, (params) -> {
			Minecraft instance = Minecraft.getInstance();
			if (instance.player == null) {
				return;
			}
			Inventory inventory = instance.player.getInventory();
			if (inventory == null) {
				return;
			}
			if (inventory.contains(AllItems.WRENCH.asStack())) {
				int wrenchItemSlot = inventory.findSlotMatchingItem(AllItems.WRENCH.asStack());
				ItemStack mainHandItem = instance.player.getItemInHand(InteractionHand.MAIN_HAND);
				if (Items.AIR.equals(mainHandItem)) {
					inventory.removeItem(wrenchItemSlot, 1);
					inventory.setPickedItem(AllItems.WRENCH.asStack());
				}
			}
		});
	}
}
