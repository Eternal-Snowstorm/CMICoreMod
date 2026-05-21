package dev.celestiacraft.cmi.compat.storage;

import dev.celestiacraft.cmi.tags.CmiItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CmiStackUpgradeHelper {
	private CmiStackUpgradeHelper() {
	}

	public static boolean isNoStackUpgrade(ItemStack stack) {
		return !stack.isEmpty() && stack.is(CmiItemTags.NO_STACK_UPGRADE);
	}

	public static boolean isNoStackUpgrade(Item item) {
		return hasTag(item, CmiItemTags.NO_STACK_UPGRADE);
	}

	public static boolean isNoStorageStack(ItemStack stack) {
		return !stack.isEmpty() && stack.is(CmiItemTags.NO_STORAGE_STACK);
	}

	public static boolean isNoStorageStack(Item item) {
		return hasTag(item, CmiItemTags.NO_STORAGE_STACK);
	}

	private static boolean hasTag(Item item, TagKey<Item> tag) {
		return item != null && BuiltInRegistries.ITEM.wrapAsHolder(item).is(tag);
	}
}
