package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.mbd2.api.recipe.ingredient.FluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.stream.Stream;

public class MBDFluidIngredient extends FluidIngredient {
	private MBDFluidIngredient() {
		super(Stream.empty(), 0, null);
	}

	public static FluidIngredient of(FluidStack fluid, long amount) {
		return of(fluid, amount);
	}

	public static FluidIngredient of(FluidStack fluid, long amount, CompoundTag nbt) {
		return of(fluid, amount, nbt);
	}

	public static FluidIngredient ofTag(TagKey<Fluid> tag, long amount) {
		return of(tag, amount);
	}

	public static FluidIngredient ofTag(TagKey<Fluid> tag, long amount, CompoundTag nbt) {
		return of(tag, amount, nbt);
	}

	public static FluidIngredient ofTagId(ResourceLocation tag, long amount) {
		return of(FluidTags.create(tag), amount);
	}

	public static FluidIngredient ofTagId(ResourceLocation tag, long amount, CompoundTag nbt) {
		return of(FluidTags.create(tag), amount, nbt);
	}
}