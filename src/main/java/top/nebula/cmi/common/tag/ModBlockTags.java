package top.nebula.cmi.common.tag;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import top.nebula.cmi.Cmi;

public class ModBlockTags {
	public static TagKey<Block> createTag(String namespace, String name) {
		return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
	}

	public static TagKey<Block> createTag(String name) {
		return BlockTags.create(Cmi.loadResource(name));
	}

	public static TagKey<Block> GRILL_SOURCES;

	static {
		GRILL_SOURCES = createTag("grill_sources");
	}
}