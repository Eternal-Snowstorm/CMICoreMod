package dev.celestiacraft.cmi.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.packs.repository.PackCompatibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackCompatibility.class)
public class PackCompatibilityMixin {
	@ModifyReturnValue(method = "isCompatible()Z",at = @At("RETURN"))
	private boolean alwaysCompatible(boolean ori) {
		return true;
	}
}