package dev.celestiacraft.cmi.tags;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CmiBlockTags {
	public static final TagKey<Block>
			GRILL_SOURCES,
			FREEZING_CATALYST,
			COKE_OVEN_STRUCTURE,
			COGWHEEL,
			LARGE_COGWHEEL;

	static {
		GRILL_SOURCES = TagsBuilder.block("grill_sources").cmi();
		FREEZING_CATALYST = TagsBuilder.block("freezing_catalyst").cmi();
		COKE_OVEN_STRUCTURE = TagsBuilder.block("coke_oven_structure").cmi();
		COGWHEEL = TagsBuilder.block("cogwheel").create();
		LARGE_COGWHEEL = TagsBuilder.block("large_cogwheel").create();
	}
}