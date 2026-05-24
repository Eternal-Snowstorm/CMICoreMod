package dev.celestiacraft.cmi.mixin;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.buuz135.functionalstorage.item.UpgradeItem;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import dev.celestiacraft.cmi.compat.storage.CmiDrawerUpgradeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiPredicate;

@Mixin(value = ControllableDrawerTile.class, remap = false)
public abstract class ControllableDrawerTileMixin {

	@Shadow
	private InventoryComponent<?> storageUpgrades;

	@Shadow
	private InventoryComponent<?> utilityUpgrades;

	@Shadow
	private boolean needsUpgradeCache;

	@Shadow
	private boolean hasDowngrade;

	@Shadow
	private boolean isCreative;

	@Shadow
	private boolean isVoid;

	@Shadow
	private int mult;

	@Shadow
	public abstract int getUtilitySlotAmount();

	@Shadow
	public abstract double getStorageDiv();

	@Inject(method = "<init>(Lcom/hrznstudio/titanium/block/BasicTileBlock;Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("RETURN"))
	private void cmi$wrapUpgradeFilters(BasicTileBlock<?> base, BlockEntityType<?> entityType, BlockPos pos, BlockState state, CallbackInfo ci) {
		cmi$wrapInsertPredicate(storageUpgrades);
		cmi$wrapInsertPredicate(utilityUpgrades);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void cmi$wrapInsertPredicate(InventoryComponent component) {
		if (component == null) {
			return;
		}
		BiPredicate<ItemStack, Integer> original = component.getInsertPredicate();
		BiPredicate<ItemStack, Integer> wrapped = (stack, slotIndex) -> {
			if (original != null && !original.test(stack, slotIndex)) {
				return false;
			}
			return CmiDrawerUpgradeHelper.canAcceptUpgradeInComponent(component, stack, slotIndex);
		};
		component.setInputFilter(wrapped);
	}

	@Inject(method = "onSlotActivated", at = @At("HEAD"), cancellable = true)
	private void cmi$sameUpgradeOnly(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ, int slot, CallbackInfoReturnable<InteractionResult> cir) {
		if (!CmiDrawerUpgradeHelper.isSameUpgradeOnly()) {
			return;
		}

		ItemStack stack = playerIn.getItemInHand(hand);
		if (stack.isEmpty() || !(stack.getItem() instanceof UpgradeItem upgradeItem)) {
			return;
		}

		InventoryComponent<?> targetComponent;
		if (upgradeItem instanceof StorageUpgradeItem || upgradeItem.equals(FunctionalStorage.CREATIVE_UPGRADE.get())) {
			targetComponent = storageUpgrades;
		} else {
			targetComponent = utilityUpgrades;
		}

		if (targetComponent != null && CmiDrawerUpgradeHelper.hasDifferentUpgrade(targetComponent, stack)) {
			cir.setReturnValue(InteractionResult.PASS);
		}
	}

	@Inject(method = "maybeCacheUpgrades", at = @At("HEAD"), cancellable = true)
	private void cmi$additiveUpgrade(CallbackInfo ci) {
		if (!CmiDrawerUpgradeHelper.isAdditiveUpgrade()) {
			return;
		}
		if (!needsUpgradeCache) {
			ci.cancel();
			return;
		}

		isCreative = false;
		hasDowngrade = false;
		mult = 1;
		for (int i = 0; i < storageUpgrades.getSlots(); i++) {
			Item upgrade = storageUpgrades.getStackInSlot(i).getItem();
			if (upgrade.equals(FunctionalStorage.STORAGE_UPGRADES.get(StorageUpgradeItem.StorageTier.IRON).get())) {
				hasDowngrade = true;
			}
			if (upgrade.equals(FunctionalStorage.CREATIVE_UPGRADE.get())) {
				isCreative = true;
			}
			if (upgrade instanceof StorageUpgradeItem storageUpgradeItem) {
				double calculated = storageUpgradeItem.getStorageMultiplier() / getStorageDiv();
				if (mult == 1) mult = 0;
				mult += (int) calculated;
			}
		}
		isVoid = false;
		if (getUtilitySlotAmount() > 0) {
			for (int i = 0; i < utilityUpgrades.getSlots(); i++) {
				if (utilityUpgrades.getStackInSlot(i).getItem().equals(FunctionalStorage.VOID_UPGRADE.get())) {
					isVoid = true;
				}
			}
		}
		needsUpgradeCache = false;
		ci.cancel();
	}
}
