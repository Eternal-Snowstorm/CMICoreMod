package dev.celestiacraft.cmi.compat.kubejs.utils.fluid;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;

public interface FluidWrapperUtils {
	static FluidStackJS tag(String key, String tag, int amount) {
		return FluidStackJSUtils.tag(key, tag, amount);
	}

	static FluidStackJS tag(String key, String tag) {
		return FluidStackJSUtils.tag(key, tag, 1000);
	}
}