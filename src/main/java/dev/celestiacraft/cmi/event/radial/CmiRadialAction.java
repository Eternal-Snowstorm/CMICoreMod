package dev.celestiacraft.cmi.event.radial;

import cc.sighs.auratip.api.action.Actions;
import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.utils.ModResources;
import mekanism.common.registries.MekanismItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CmiRadialAction {
	public static final ResourceLocation WRENCH = Cmi.loadResource("wrench");
	public static final ResourceLocation NETTOOL = Cmi.loadResource("network_tool");
	public static final ResourceLocation CONFIGURATOR = Cmi.loadResource("configurator");

	public static void register() {
		pickupWrench();
		pickupNetworkTool();
		pickupConfigurator();
	}

	private static void pickupWrench() {
		Actions.register(WRENCH, (params) -> {
			pickup(AllItems.WRENCH.asStack());
		});
	}

	private static void pickupNetworkTool() {
		Actions.register(NETTOOL, (params) -> {
			pickup(ModResources.NETWORK_TOOL.getItemStack());
		});
	}

	private static void pickupConfigurator() {
		Actions.register(CONFIGURATOR, (params) -> {
			pickup(MekanismItems.CONFIGURATOR.getItemStack());
		});
	}

	private static void pickup(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;

		if (player == null) {
			return;
		}

		if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
			return;
		}

		Inventory inventory = player.getInventory();

		int slot = findItemIgnoringNBT(inventory, stack);
		if (slot == -1) {
			player.displayClientMessage(
					Component.translatable("message.cmi.notFindItem"),
					false
			);
			return;
		}

		ItemStack stackInSlot = inventory.getItem(slot);
		player.setItemInHand(InteractionHand.MAIN_HAND, stackInSlot);
		inventory.setItem(slot, ItemStack.EMPTY);
	}

	/**
	 * 忽略NBT查找物品
	 *
	 * @param inventory
	 * @param item
	 * @return
	 */
	private static int findItemIgnoringNBT(Inventory inventory, ItemStack item) {
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);

			if (!stack.isEmpty() && ItemStack.isSameItem(stack, item)) {
				return i;
			}
		}
		return -1;
	}
}