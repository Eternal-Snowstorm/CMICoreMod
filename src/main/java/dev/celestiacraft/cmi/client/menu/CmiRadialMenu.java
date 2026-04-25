package dev.celestiacraft.cmi.client.menu;

import cc.sighs.auratip.api.action.Actions;
import cc.sighs.auratip.api.radiamenu.RadialMenuBuilder;
import cc.sighs.auratip.api.radiamenu.RadialMenuRegistry;
import cc.sighs.auratip.data.RadialMenuData;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.event.radial.CmiRadialAction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class CmiRadialMenu {
	public static ResourceLocation MENU = Cmi.loadResource("radial");

	public static void register() {
		RadialMenuData menu = new RadialMenuBuilder(MENU)
				.radii(55, 100)
				.animationSpeed(1.0F)
				.ringColors(List.of("#1A366699", "#D93A6EA5"))
				.slot(
						"Open Quests",
						ResourceLocation.parse("ftbquests:textures/item/book.png"),
						Actions.runCommand("ftbquests open_book"),
						Component.translatable("radial.cmi.open_quest"),
						"#77FFFFFF"
				)
				.slot(
						"Claim Chunk",
						ResourceLocation.parse("ftblibrary:textures/icons/map.png"),
						Actions.runCommand("ftbchunks claim"),
						Component.translatable("radial.cmi.claim_chunk"),
						"#77FFFFFF"
				)
				.slot(
						"Pickup A Wrench",
						ResourceLocation.parse("ae2:textures/item/certus_quartz_wrench.png"),
						Actions.script(
								CmiRadialAction.WRENCH_PICKUP,
								Map.of("wrench_pickup", "wrench")
						),
						Component.translatable("radial.cmi.wrench_pickup"),
						"#77FFFFFF"
				)
				.build();

		RadialMenuRegistry.setMenus(Cmi.MODID, List.of(menu));
	}
}