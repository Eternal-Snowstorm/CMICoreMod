package dev.celestiacraft.cmi.tags;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CmiItemTags {
	public static final TagKey<Item>
			INCOMPLETE_ITEMS,
			MECHANISMS,
			INCOMPLETE_MECHANISMS,
			MECHANISM_FLASH_DRIVES,
			WRENCHES,
			COGWHEEL,
			LARGE_COGWHEEL,
			WORKBENCHES,
			COILS,
			COINS;

	static {
		INCOMPLETE_ITEMS = TagsBuilder.item("incomplete_items").cmi();
		MECHANISMS = TagsBuilder.item("mechanisms").create();
		INCOMPLETE_MECHANISMS = TagsBuilder.item("incomplete_mechanisms").create();
		MECHANISM_FLASH_DRIVES = TagsBuilder.item("mechanism_flash_drives").cmi();
		WRENCHES = TagsBuilder.item("wrenches").forge();
		COGWHEEL = TagsBuilder.item("cogwheel").create();
		LARGE_COGWHEEL = TagsBuilder.item("large_cogwheel").create();
		WORKBENCHES = TagsBuilder.item("workbenches").forge();
		COILS = TagsBuilder.item("coils").cmi();

		// 线圈发射器的弹药标签: 任何带 forge:coins 的物品都能被打出去
		COINS = TagsBuilder.item("coins").forge();
	}

	public static TagKey<Item> mechanism(String name) {
		String path = String.format("mechanisms/%s", name);
		return TagsBuilder.item(path).create();
	}
}