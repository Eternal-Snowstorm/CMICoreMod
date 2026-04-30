package dev.celestiacraft.cmi.utils.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum CmiMiningLevel {
	WOODEN("forge:needs_wooden_tool"),
	STONE("minecraft:needs_stone_tool"),
	IRON("minecraft:needs_iron_tool"),
	GOLD("forge:needs_gold_tool"),
	DIAMOND("minecraft:needs_diamond_tool"),
	NETHER("forge:needs_netherite_tool");

	private final TagKey<Block> tag;

	CmiMiningLevel(String location) {
		tag = BlockTags.create(ResourceLocation.parse(location));
	}

	public TagKey<Block> tag() {
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