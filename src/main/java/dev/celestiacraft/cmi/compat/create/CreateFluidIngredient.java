package dev.celestiacraft.cmi.compat.create;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public abstract class CreateFluidIngredient extends FluidIngredient {
	public static FluidIngredient ofTag(TagKey<Fluid> tag, int amount) {
		return fromTag(tag, amount);
	}

	public static FluidIngredient ofTagId(ResourceLocation tag, int amount) {
		return fromTag(FluidTags.create(tag), amount);
	}
}