package dev.celestiacraft.cmi.tags;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
	public static final TagKey<Block> GRILL_SOURCES;
	public static final TagKey<Block> FREEZING_CATALYST;
	public static final TagKey<Block> COKE_OVEN_STRUCTURE;
	public static final TagKey<Block> COGWHEEL;
	public static final TagKey<Block> LARGE_COGWHEEL;

	static {
		GRILL_SOURCES = TagsBuilder.block("grill_sources").cmi();
		FREEZING_CATALYST = TagsBuilder.block("freezing_catalyst").cmi();
		COKE_OVEN_STRUCTURE = TagsBuilder.block("coke_oven_structure").cmi();
		COGWHEEL = TagsBuilder.block("cogwheel").create();
		LARGE_COGWHEEL = TagsBuilder.block("large_cogwheel").create();
	}
}