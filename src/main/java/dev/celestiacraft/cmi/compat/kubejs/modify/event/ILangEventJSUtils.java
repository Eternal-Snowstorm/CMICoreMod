package dev.celestiacraft.cmi.compat.kubejs.modify.event;

import dev.latvian.mods.kubejs.client.LangEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

public interface ILangEventJSUtils {
	default void renameFluid(Fluid fluid, String name) {
		if (fluid == null || fluid == Fluids.EMPTY) {
			return;
		}

		ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);

		if (id == null) {
			return;
		}

		String description = fluid.getFluidType().getDescriptionId();

		if (description == null || description.isEmpty()) {
			return;
		}

		((LangEventJS) this).add(id.getNamespace(), description, name);
	}
}