package dev.celestiacraft.cmi.compat.kubejs.modify.fluid;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import net.minecraft.resources.ResourceLocation;

public interface IFluidStackJSUtils {
	String kjs$tagType = null;

	static FluidStackJS tag(String key, String tag, long amount) {
		UnboundFluidStackJS fs = new UnboundFluidStackJS(ResourceLocation.tryParse(tag), key);
		fs.setAmount(amount);
		return fs;
	}

	default boolean kjs$isTag() {
		return false;
	}
}