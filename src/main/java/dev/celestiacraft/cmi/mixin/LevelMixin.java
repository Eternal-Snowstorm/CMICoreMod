package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.common.block.wind_vane.WindVaneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
	@Inject(
			method = "isRainingAt",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cmi$isRainingAt(BlockPos pos, CallbackInfoReturnable<Boolean> returnable) {
		Level self = (Level) (Object) this;
		if (WindVaneManager.isSealed(self, pos)) {
			returnable.setReturnValue(false);
		}
	}
}