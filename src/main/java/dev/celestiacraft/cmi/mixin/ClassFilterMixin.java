package dev.celestiacraft.cmi.mixin;

import dev.latvian.mods.kubejs.util.ClassFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClassFilter.class, remap = false)
public abstract class ClassFilterMixin {
	@Inject(method = "deny(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
	public void cmi$cancelDenyString(String string, CallbackInfo info) {
		info.cancel();
	}

	@Inject(method = "deny(Ljava/lang/Class;)V", at = @At("HEAD"), cancellable = true)
	public void cmi$cancelDenyClass(Class<?> c, CallbackInfo info) {
		info.cancel();
	}
}