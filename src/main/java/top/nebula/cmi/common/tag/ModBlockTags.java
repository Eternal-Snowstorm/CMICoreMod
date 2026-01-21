package top.nebula.cmi.common.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import top.nebula.cmi.Cmi;
import top.nebula.utils.tags.TagsBuilder;

public class ModBlockTags {
	public static TagKey<Block> GRILL_SOURCES;

	static {
		GRILL_SOURCES = TagsBuilder.block("grill_sources")
				.custom(Cmi.MODID)
				.build();
	}
}