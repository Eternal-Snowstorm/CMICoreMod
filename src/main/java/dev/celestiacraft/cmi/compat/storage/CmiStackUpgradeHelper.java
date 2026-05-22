package dev.celestiacraft.cmi.compat.storage;

import dev.celestiacraft.cmi.config.common.StackStorageConfig;
import dev.celestiacraft.cmi.config.main.CommonConfig;
import dev.celestiacraft.cmi.tags.CmiItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

public final class CmiStackUpgradeHelper {
	private CmiStackUpgradeHelper() {
	}

	public static boolean isNoStackUpgrade(ItemStack stack) {
		return isEnabled(StackStorageConfig.NO_STACK_UPGRADE_ENABLED) && !stack.isEmpty() && stack.is(CmiItemTags.NO_STACK_UPGRADE);
	}

	public static boolean isNoStackUpgrade(Item item) {
		return isEnabled(StackStorageConfig.NO_STACK_UPGRADE_ENABLED) && hasTag(item, CmiItemTags.NO_STACK_UPGRADE);
	}

	public static boolean isNoStorageStack(ItemStack stack) {
		return isEnabled(StackStorageConfig.NO_STORAGE_STACK_ENABLED) && !stack.isEmpty() && stack.is(CmiItemTags.NO_STORAGE_STACK);
	}

	public static boolean isNoStorageStack(Item item) {
		return isEnabled(StackStorageConfig.NO_STORAGE_STACK_ENABLED) && hasTag(item, CmiItemTags.NO_STORAGE_STACK);
	}

	private static boolean isEnabled(ForgeConfigSpec.BooleanValue value) {
		return value == null || !CommonConfig.SPEC.isLoaded() || value.get();
	}

	private static boolean hasTag(Item item, TagKey<Item> tag) {
		return item != null && BuiltInRegistries.ITEM.wrapAsHolder(item).is(tag);
	}
}
