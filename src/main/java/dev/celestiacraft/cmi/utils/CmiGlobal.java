package dev.celestiacraft.cmi.utils;

import com.simibubi.create.Create;
import dev.celestiacraft.cmi.utils.metal.CmiMetalRegistry;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmiGlobal {
	public static void registerMetal(String id, int meltingPoint, String namespace, String byProduct) {
		CmiMetalRegistry.register(id, meltingPoint, namespace, byProduct);
	}

	/**
	 * 整合包最终版本号
	 */
	@Setter
	public static String modPackMainVersion;

	/**
	 * 整合包状态
	 */
	@Getter
	@Setter
	public static String modPackState = "Beta";

	/**
	 * 是否为热修复版本
	 */
	@Getter
	@Setter
	public static boolean isHotfix = false;

	/**
	 * Modpack数字版本号
	 */

	@Getter
	@Setter
	public static String modpackNumberVersion = "2.5.0";

	/**
	 * 定义版本号
	 */
	private static void setModPackMainVersion() {
		if (isHotfix) {
			modPackMainVersion = String.format("§0CMI %s-%s-hf", modPackState, modpackNumberVersion);
			ConsoleJS.STARTUP.info(String.format("CMI Version: %s-%s-hf", modPackState, modpackNumberVersion));
		}

		modPackMainVersion = String.format("§0CMI %s-%s", modPackState, modpackNumberVersion);
		ConsoleJS.STARTUP.info(String.format("CMI Version: %s-%s", modPackState, modpackNumberVersion));
	}

	public static String getModPackMainVersion() {
		if (modPackMainVersion == null) {
			setModPackMainVersion();
		}
		return modPackMainVersion;
	}

	/**
	 * 扳手Tag
	 */
	public static final ResourceLocation WRENCH_PICKUP = Create.asResource("wrench_pickup");

	public static final Map<FluidStackJS, Integer> FUEL_TEMPERATURES = new HashMap<>();

	public static void setFuel(FluidStackJS id, int temperature) {
		FUEL_TEMPERATURES.put(id, temperature);
	}

	public static int getFuelTemp(FluidStackJS id) {
		try {
			return FUEL_TEMPERATURES.getOrDefault(id, 0);
		} catch (Exception exception) {
			throw new IllegalArgumentException("Unknown Fuel: " + id);
		}
	}

	public static final List<String> DYE_COLOR_GROUP = List.of(
			"black",
			"blue",
			"brown",
			"cyan",
			"gray",
			"green",
			"light_blue",
			"light_gray",
			"lime",
			"magenta",
			"orange",
			"pink",
			"purple",
			"red",
			"white",
			"yellow"
	);

	/**
	 * 构件ID List
	 */
	public static List<String> mechanismGroup = List.of(
			"wooden",
			"stone",
			"iron",
			"nature",
			"pig_iron",
			"potion",
			"colorful",
			"enchanted",
			"nether",
			"sculk",
			"ender",
			"copper",
			"andesite",
			"bronze",
			"precision",
			"redstone",
			"railway",
			"light_engineering",
			"heavy_engineering",
			"coil",
			"smart",
			"cobalt",
			"photosensitive",
			"thermal",
			"reinforced",
			"gold",
			"basic_mekanism",
			"advanced_mekanism",
			"elite_mekanism",
			"ultimate_mekanism",
			"computing",
			"air_tight",
			"tier_1_aviation",
			"tier_2_aviation",
			"tier_3_aviation",
			"tier_4_aviation",
			"nuclear",
			"antimatter",
			"creative"
	);
}