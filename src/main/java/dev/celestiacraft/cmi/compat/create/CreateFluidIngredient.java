package dev.celestiacraft.cmi.compat.create;

import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public abstract class CreateFluidIngredient extends FluidIngredient {
	@RemapForJS("ofFluid")
	public static FluidIngredient of(Fluid fluid, int amount) {
		return fromFluid(fluid, amount);
	}

	@RemapForJS("ofStack")
	public static FluidIngredient of(FluidStack fluid) {
		return fromFluidStack(fluid);
	}

	@RemapForJS("ofTag")
	public static FluidIngredient of(TagKey<Fluid> tag, int amount) {
		return fromTag(tag, amount);
	}

	@RemapForJS("ofTagId")
	public static FluidIngredient of(ResourceLocation tag, int amount) {
		return fromTag(FluidTags.create(tag), amount);
	}
}