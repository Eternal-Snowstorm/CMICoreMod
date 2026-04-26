package dev.celestiacraft.cmi.client.menu;

import cc.sighs.auratip.api.action.Actions;
import cc.sighs.auratip.api.radiamenu.RadialMenuBuilder;
import cc.sighs.auratip.api.radiamenu.RadialMenuRegistry;
import cc.sighs.auratip.data.RadialMenuData;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.event.radial.CmiRadialAction;
import mekanism.common.Mekanism;
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
						"Pickup A Wrench",
						ResourceLocation.parse("cmi:textures/gui/wrench.png"),
						Actions.script(
								CmiRadialAction.WRENCH,
								Map.of()
						),
						Component.translatable("radial.cmi.wrench"),
						"#77FFFFFF"
				)
				.slot(
						"Pickup A Network Tool",
						ResourceLocation.parse("cmi:textures/gui/network_tool.png"),
						Actions.script(
								CmiRadialAction.NETTOOL,
								Map.of()
						),
						Component.translatable("radial.cmi.net_tool"),
						"#77FFFFFF"
				)
				.slot(
						"Pickup A Configurator",
						Mekanism.rl("textures/item/configurator.png"),
						Actions.script(
								CmiRadialAction.CONFIGURATOR,
								Map.of()
						),
						Component.translatable("radial.cmi.configurator"),
						"#77FFFFFF"
				)
				.build();

		RadialMenuRegistry.setMenus(Cmi.MODID, List.of(menu));
	}
}