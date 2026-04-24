package dev.celestiacraft.cmi.compat.kubejs.utils.fluid;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import net.minecraft.resources.ResourceLocation;

public interface FluidWrapperUtils {
	static FluidStackJS tag(String key, ResourceLocation tag, int amount) {
		return FluidStackJSUtils.tag(key, tag, amount);
	}

	static FluidStackJS tag(String key, ResourceLocation tag) {
		return FluidStackJSUtils.tag(key, tag, 1000);
	}
}