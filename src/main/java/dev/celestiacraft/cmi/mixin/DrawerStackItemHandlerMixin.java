package dev.celestiacraft.cmi.mixin;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.inventory.BigInventoryHandler;
import com.buuz135.functionalstorage.inventory.item.DrawerStackItemHandler;
import dev.celestiacraft.cmi.compat.storage.CmiStackUpgradeHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = DrawerStackItemHandler.class, remap = false)
public abstract class DrawerStackItemHandlerMixin {
	@Shadow(remap = false)
	private FunctionalStorage.DrawerType type;

	@Shadow(remap = false)
	private boolean downgrade;

	@Shadow(remap = false)
	public abstract List<BigInventoryHandler.BigStack> getStoredStacks();

	@Inject(method = "getSlotLimit", at = @At("HEAD"), cancellable = true, remap = false)
	private void cmi$noStackUpgrade(int slot, CallbackInfoReturnable<Integer> cir) {
		if (slot >= type.getSlots()) {
			return;
		}

		ItemStack stored = getStoredStacks().get(slot).getStack();
		if (CmiStackUpgradeHelper.isNoStorageStack(stored)) {
			cir.setReturnValue(1);
			return;
		}
		if (!CmiStackUpgradeHelper.isNoStackUpgrade(stored)) {
			return;
		}

		int slotAmount = downgrade ? 64 : type.getSlotAmount();
		cir.setReturnValue((int) Math.min(Integer.MAX_VALUE, slotAmount));
	}
}
