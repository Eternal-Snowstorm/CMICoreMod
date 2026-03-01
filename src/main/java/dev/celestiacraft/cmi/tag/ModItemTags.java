package dev.celestiacraft.cmi.tag;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
	public static final TagKey<Item> MECHANISMS;
	public static final TagKey<Item> INCOMPLETE_MECHANISMS;

	static {
		MECHANISMS = TagsBuilder.item("mechanisms", "create");
		INCOMPLETE_MECHANISMS = TagsBuilder.item("incomplete_mechanisms", "create");
	}

	public static TagKey<Item> mechanism(String name) {
		String path = String.format("mechanisms/%s", name);
		return TagsBuilder.item(path, "create");
	}
}