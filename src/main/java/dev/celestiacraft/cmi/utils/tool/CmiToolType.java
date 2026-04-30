package dev.celestiacraft.cmi.utils.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum CmiToolType {
	SWORD("forge:mineable/sword"),
	PICKAXE("minecraft:mineable/pickaxe"),
	AXE("minecraft:mineable/axe"),
	SHOVEL("minecraft:mineable/shovel"),
	HOE("minecraft:mineable/hoe");

	private final TagKey<Block> tag;

	CmiToolType(String location) {
		tag = BlockTags.create(ResourceLocation.parse(location));
	}

	public TagKey<Block> tag() {
		return tag;
	}

	public static CmiToolType from(String key) {
		try {
			return CmiToolType.valueOf(key.toUpperCase());
		} catch (Exception exception) {
			throw new IllegalArgumentException("Unknown ToolType: " + key);
		}
	}
}