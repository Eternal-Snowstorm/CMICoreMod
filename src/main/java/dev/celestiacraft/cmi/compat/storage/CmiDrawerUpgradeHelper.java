package dev.celestiacraft.cmi.compat.storage;

import com.buuz135.functionalstorage.item.UpgradeItem;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import dev.celestiacraft.cmi.config.common.DrawerUpgradeConfig;
import dev.celestiacraft.cmi.config.main.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

public final class CmiDrawerUpgradeHelper {
	private CmiDrawerUpgradeHelper() {
	}

	public static boolean isSameUpgradeOnly() {
		return isEnabled(DrawerUpgradeConfig.SAME_UPGRADE_ONLY_ENABLED);
	}

	public static boolean isAdditiveUpgrade() {
		return isEnabled(DrawerUpgradeConfig.ADDITIVE_UPGRADE_ENABLED);
	}

	public static boolean canAcceptUpgradeInComponent(InventoryComponent<?> component, ItemStack incoming, int incomingSlot) {
		if (!isSameUpgradeOnly() || component == null || incoming.isEmpty() || !(incoming.getItem() instanceof UpgradeItem)) {
			return true;
		}

		Item incomingItem = incoming.getItem();
		for (int i = 0; i < component.getSlots(); i++) {
			if (i == incomingSlot) {
				continue;
			}
			ItemStack existing = component.getStackInSlot(i);
			if (!existing.isEmpty() && !existing.getItem().equals(incomingItem)) {
				return false;
			}
		}
		return true;
	}

	public static boolean hasDifferentUpgrade(InventoryComponent<?> component, ItemStack incoming) {
		if (component == null || incoming.isEmpty() || !(incoming.getItem() instanceof UpgradeItem)) {
			return false;
		}

		Item incomingItem = incoming.getItem();
		for (int i = 0; i < component.getSlots(); i++) {
			ItemStack existing = component.getStackInSlot(i);
			if (!existing.isEmpty() && !existing.getItem().equals(incomingItem)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEnabled(ForgeConfigSpec.BooleanValue value) {
		return value != null && CommonConfig.SPEC.isLoaded() && value.get();
	}
}
