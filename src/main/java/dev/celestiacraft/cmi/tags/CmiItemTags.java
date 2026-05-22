package dev.celestiacraft.cmi.tags;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CmiItemTags {
	public static final TagKey<Item>
			MECHANISMS,
			INCOMPLETE_MECHANISMS,
			MECHANISM_FLASH_DRIVES,
			WRENCHES,
			BURNER,
			COGWHEEL,
			LARGE_COGWHEEL,
			WORKBENCHES,
			NO_STACK_UPGRADE,
			NO_STORAGE_STACK;

	static {
		MECHANISMS = TagsBuilder.item("mechanisms").create();
		INCOMPLETE_MECHANISMS = TagsBuilder.item("incomplete_mechanisms").create();
		MECHANISM_FLASH_DRIVES = TagsBuilder.item("mechanism_flash_drives").cmi();
		WRENCHES = TagsBuilder.item("wrenches").forge();
		BURNER = TagsBuilder.item("burner").namespace("steampowered");
		COGWHEEL = TagsBuilder.item("cogwheel").create();
		LARGE_COGWHEEL = TagsBuilder.item("large_cogwheel").create();
		WORKBENCHES = TagsBuilder.item("workbenches").forge();
		NO_STACK_UPGRADE = TagsBuilder.item("no_stack_upgrade").cmi();
		NO_STORAGE_STACK = TagsBuilder.item("no_storage_stack").cmi();
	}

	public static TagKey<Item> mechanism(String name) {
		String path = String.format("mechanisms/%s", name);
		return TagsBuilder.item(path).create();
	}
}