package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.compat.kubejs.utils.fluid.FluidWrapperUtils;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidWrapper.class)
public class FluidWrapperMixin implements FluidWrapperUtils {
}