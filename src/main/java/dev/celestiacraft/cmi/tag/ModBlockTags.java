package dev.celestiacraft.cmi.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.libs.tags.TagsBuilder;

public class ModBlockTags {
	public static final TagKey<Block> GRILL_SOURCES;

	static {
		GRILL_SOURCES = TagsBuilder.block("grill_sources")
				.namespace(Cmi.MODID);
	}
}