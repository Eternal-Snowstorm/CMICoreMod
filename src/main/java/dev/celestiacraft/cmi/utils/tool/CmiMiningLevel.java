package dev.celestiacraft.cmi.utils.tool;

import net.minecraft.resources.ResourceLocation;

public enum CmiMiningLevel {
	WOODEN("forge:needs_wooden_tool"),
	STONE("minecraft:needs_stone_tool"),
	IRON("minecraft:needs_iron_tool"),
	GOLD("forge:needs_gold_tool"),
	DIAMOND("minecraft:needs_diamond_tool"),
	NETHER("forge:needs_netherite_tool");

	private final ResourceLocation tag;

	CmiMiningLevel(String location) {
		tag = ResourceLocation.parse(location);
	}

	public ResourceLocation tag() {
		return tag;
	}

	public static CmiMiningLevel from(String key) {
		try {
			return CmiMiningLevel.valueOf(key.toUpperCase());
		} catch (Exception exception) {
			throw new IllegalArgumentException("Unknown ToolType: " + key);
		}
	}
}