package dev.celestiacraft.cmi.compat.thermal;

import cofh.lib.common.fluid.FluidIngredient;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.stream.Stream;

public class ThermalFluidIngredient extends FluidIngredient {
	private ThermalFluidIngredient() {
		super(Stream.of());
	}

	@RemapForJS("ofFluid")
	public static FluidIngredient of(Fluid fluid, int amount) {
		return of(new FluidStack(fluid, amount));
	}

	@RemapForJS("ofStack")
	public static FluidIngredient of(FluidStack stack) {
		return of(stack);
	}

	@RemapForJS("ofStacks")
	public static FluidIngredient of(FluidStack... stack) {
		return of(stack);
	}

	@RemapForJS("ofTagId")
	public static FluidIngredient of(ResourceLocation tag, int amount) {
		return of(FluidTags.create(tag), amount);
	}
}