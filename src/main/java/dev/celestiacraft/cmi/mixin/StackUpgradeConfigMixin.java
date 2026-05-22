package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.compat.storage.CmiStackUpgradeHelper;
import net.minecraft.world.item.Item;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stack.StackUpgradeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StackUpgradeConfig.class, remap = false)
public class StackUpgradeConfigMixin {
	@Inject(method = "canStackItem", at = @At("HEAD"), cancellable = true)
	private void cmi$noStackUpgradeOverride(Item item, CallbackInfoReturnable<Boolean> cir) {
		if (CmiStackUpgradeHelper.isNoStackUpgrade(item)) {
			cir.setReturnValue(false);
		}
	}
}
