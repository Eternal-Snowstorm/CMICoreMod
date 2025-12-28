package top.nebula.cmi.common.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import top.nebula.cmi.Cmi;

public class ModItemTags {
	public static TagKey<Item> createTag(String namespace, String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
	}

	public static TagKey<Item> createTag(String name) {
		return ItemTags.create(Cmi.loadResource(name));
	}
}