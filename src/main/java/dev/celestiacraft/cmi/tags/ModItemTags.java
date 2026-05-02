package dev.celestiacraft.cmi.tags;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
	public static final TagKey<Item> MECHANISMS;
	public static final TagKey<Item> INCOMPLETE_MECHANISMS;
	public static final TagKey<Item> MECHANISM_FLASH_DRIVES;
	public static final TagKey<Item> WRENCHES;
	public static final TagKey<Item> BURNER;
	public static final TagKey<Item> COGWHEEL;
	public static final TagKey<Item> LARGE_COGWHEEL;

	static {
		MECHANISMS = TagsBuilder.item("mechanisms").create();
		INCOMPLETE_MECHANISMS = TagsBuilder.item("incomplete_mechanisms").create();
		MECHANISM_FLASH_DRIVES = TagsBuilder.item("mechanism_flash_drives").cmi();
		WRENCHES = TagsBuilder.item("wrenches").forge();
		BURNER = TagsBuilder.item("burner").namespace("steampowered");
		COGWHEEL = TagsBuilder.item("cogwheel").create();
		LARGE_COGWHEEL = TagsBuilder.item("large_cogwheel").create();
	}

	public static TagKey<Item> mechanism(String name) {
		String path = String.format("mechanisms/%s", name);
		return TagsBuilder.item(path).create();
	}
}