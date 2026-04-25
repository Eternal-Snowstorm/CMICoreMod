package dev.celestiacraft.cmi.event.radial;

import cc.sighs.auratip.api.action.Actions;
import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.utils.ModResources;
import earth.terrarium.adastra.AdAstra;
import mekanism.common.registries.MekanismItems;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static dev.celestiacraft.cmi.utils.ModResources.loadResource;

public class CmiRadialAction {
	public static final ResourceLocation WRENCH = Cmi.loadResource("wrench");
	public static final ResourceLocation NETTOOL = Cmi.loadResource("network_tool");
	public static final ResourceLocation CONFIGURATOR = Cmi.loadResource("configurator");

	public static void register() {
		Actions();
	}

	public static void Actions() {
		Actions.register(WRENCH, (params) -> {
			Pickup(AllItems.WRENCH.asStack());
		});

		Actions.register(NETTOOL, (params) -> {
			Pickup(loadResource(ResourceLocation.fromNamespaceAndPath("ae2", "network_tool")).getItemStack());
		});

		Actions.register(CONFIGURATOR, (params) -> {
			Pickup(ModResources.loadResource("mekanism:configurator").getItemStack());
		});
	}

	private static void Pickup(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) {
			return;
		}

		Inventory inventory = minecraft.player.getInventory();

		int slot = inventory.findSlotMatchingItem(stack);
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
	}
}