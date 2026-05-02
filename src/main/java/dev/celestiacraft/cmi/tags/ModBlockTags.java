package dev.celestiacraft.cmi.tags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.libs.tags.TagsBuilder;

public class ModBlockTags {
	public static final TagKey<Block> GRILL_SOURCES;
	public static final TagKey<Block> FREEZING_CATALYST;
	public static final TagKey<Block> COKE_OVEN_STRUCTURE;

	static {
		GRILL_SOURCES = TagsBuilder.block("grill_sources", Cmi.MODID);
		FREEZING_CATALYST = TagsBuilder.block("freezing_catalyst", Cmi.MODID);
		COKE_OVEN_STRUCTURE = TagsBuilder.block("coke_oven_structure", Cmi.MODID);
	}
}