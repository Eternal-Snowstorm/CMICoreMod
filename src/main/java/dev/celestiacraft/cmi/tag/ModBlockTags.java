package dev.celestiacraft.cmi.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import dev.celestiacraft.cmi.Cmi;
import top.nebula.libs.tags.TagsBuilder;

public class ModBlockTags {
	public static final TagKey<Block> GRILL_SOURCES;
	public static final TagKey<Block> SPOUT_ATTACHMENTS;

	static {
		GRILL_SOURCES = TagsBuilder.block("grill_sources")
				.custom(Cmi.MODID)
				.build();

		SPOUT_ATTACHMENTS = TagsBuilder.block("spout_attachments")
				.custom(Cmi.MODID)
				.build();
	}
}