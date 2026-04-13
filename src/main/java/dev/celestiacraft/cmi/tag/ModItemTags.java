package dev.celestiacraft.cmi.tag;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
	public static final TagKey<Item> MECHANISMS;
	public static final TagKey<Item> INCOMPLETE_MECHANISMS;
	public static final TagKey<Item> MECHANISM_FLASH_DRIVES;
	public static final TagKey<Item> WRENCHES;

	static {
		MECHANISMS = TagsBuilder.item("mechanisms").create();
		INCOMPLETE_MECHANISMS = TagsBuilder.item("incomplete_mechanisms").create();
		MECHANISM_FLASH_DRIVES = TagsBuilder.item("mechanism_flash_drives").cmi();
		WRENCHES = TagsBuilder.item("wrenches").forge();
	}

	public static TagKey<Item> mechanism(String name) {
		String path = String.format("mechanisms/%s", name);
		return TagsBuilder.item(path).create();
	}
}