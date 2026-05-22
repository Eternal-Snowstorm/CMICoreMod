package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.compat.storage.CmiStackUpgradeHelper;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryHandler.class, remap = false)
public abstract class InventoryHandlerMixin {
	@Inject(method = "getBaseStackLimit", at = @At("HEAD"), cancellable = true, remap = false)
	private void cmi$forceStackOneBase(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (CmiStackUpgradeHelper.isNoStorageStack(stack)) {
			cir.setReturnValue(1);
		}
	}

	@Inject(method = "getStackLimit", at = @At("HEAD"), cancellable = true, remap = false)
	private void cmi$forceStackOneSlot(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (CmiStackUpgradeHelper.isNoStorageStack(stack)) {
			cir.setReturnValue(1);
		}
	}
}
