package dev.celestiacraft.cmi.api.register.multiblock.machine;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public final class FluidFilters {
	private static final FluidFilter ANY = (stack) -> true;

	private FluidFilters() {
	}

	public static FluidFilter any() {
		return ANY;
	}

	public static FluidFilter only(Fluid fluid) {
		return (stack) -> !stack.isEmpty() && stack.getFluid() == fluid;
	}

	public static FluidFilter tag(TagKey<Fluid> tag) {
		return (stack) -> !stack.isEmpty() && ForgeRegistries.FLUIDS.tags().getTag(tag).contains(stack.getFluid());
	}
}
