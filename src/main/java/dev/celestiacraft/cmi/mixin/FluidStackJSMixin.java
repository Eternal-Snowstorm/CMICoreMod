package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.compat.kubejs.utils.fluid.IFluidStackJSUtils;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidStackJS.class)
public class FluidStackJSMixin implements IFluidStackJSUtils {
}