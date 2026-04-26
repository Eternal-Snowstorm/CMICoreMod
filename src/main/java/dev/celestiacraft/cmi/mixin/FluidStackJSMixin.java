package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.compat.kubejs.utils.fluid.FluidStackJSUtils;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidStackJS.class)
public class FluidStackJSMixin implements FluidStackJSUtils {
}