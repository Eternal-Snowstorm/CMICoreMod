package dev.celestiacraft.cmi.utils;

import dev.celestiacraft.cmi.utils.metal.CmiMetalRegistry;
import dev.latvian.mods.kubejs.util.ConsoleJS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CmiGlobal {
	public static void registerMetal(String id, int meltingPoint, String namespace, String byProduct) {
		CmiMetalRegistry.register(id, meltingPoint, namespace, byProduct);
	}

	/**
	 * 整合包最终版本号
	 */
	public static String modPackMainVersion;
	/**
	 * 整合包状态
	 */
	public static String modPackState = "Beta";

	/**
	 * 是否为热修复版本
	 */
	public static boolean isHotfix = false;

	/**
	 * Modpack数字版本号
	 */
	public static String modpackNumberVersion = "2.5.0";

	/**
	 * 定义版本号
	 */
	private void setModPackMainVersion() {
		if (isHotfix) {
			modPackMainVersion = String.format("§0CMI %s-%s-hf", modPackState, modpackNumberVersion);
			ConsoleJS.STARTUP.info(modPackMainVersion);
		}

		modPackMainVersion = String.format("§0CMI %s-%s", modPackState, modpackNumberVersion);
		ConsoleJS.STARTUP.info(modPackMainVersion);
	}

	/**
	 * 扳手Tag
	 */
	public static final String WRENCH_PICKUP = "create:wrench_pickup";

	/**
	 * 挖掘工具类型
	 */
	public static final Map<String, String> TOOL_TYPE = Map.of(
			"sword", "forge:mineable/sword",
			"pickaxe", "minecraft:mineable/pickaxe",
			"axe", "minecraft:mineable/axe",
			"shovel", "minecraft:mineable/shovel",
			"hoe", "minecraft:mineable/hoe"
	);

	/**
	 * 挖掘等级
	 */
	public static final Map<String, String> MINING_LEVEL = Map.of(
			"wooden", "minecraft:needs_wooden_tool",
			"stone", "minecraft:needs_stone_tool",
			"iron", "minecraft:needs_iron_tool",
			"gold", "forge:needs_gold_tool",
			"diamond", "minecraft:needs_diamond_tool",
			"nether", "forge:needs_netherite_tool"
	);

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

	/**
	 * 金属材料ID List
	 */
	public static final List<String> METAL_GROUP = new ArrayList<>();
}