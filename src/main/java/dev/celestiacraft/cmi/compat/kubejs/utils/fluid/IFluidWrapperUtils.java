package dev.celestiacraft.cmi.compat.kubejs.utils.fluid;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;

public interface IFluidWrapperUtils {
	static FluidStackJS tag(String key, String tag, int amount) {
		return IFluidStackJSUtils.tag(key, tag, amount);
	}

	static FluidStackJS tag(String key, String tag) {
		return IFluidStackJSUtils.tag(key, tag, 1000);
	}
}