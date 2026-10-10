package dev.celestiacraft.cmi.mixin.kubejs;

import dev.celestiacraft.cmi.compat.kubejs.modify.fluid.IFluidWrapperUtils;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidWrapper.class)
public class FluidWrapperMixin implements IFluidWrapperUtils {
}