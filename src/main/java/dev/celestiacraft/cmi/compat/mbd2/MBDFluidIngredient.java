package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.mbd2.api.recipe.ingredient.FluidIngredient;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.stream.Stream;

public class MBDFluidIngredient extends FluidIngredient {
	private MBDFluidIngredient() {
		super(Stream.empty(), 0, null);
	}

	@RemapForJS("ofFluid")
	public static FluidIngredient ofFluid(Fluid fluid, long amount) {
		return of(fluid, amount, null);
	}

	@RemapForJS("ofFluid")
	public static FluidIngredient of(Fluid fluid, long amount, CompoundTag nbt) {
		return FluidIngredient.of(Stream.of(fluid), amount, nbt);
	}

	@RemapForJS("ofTag")
	public static FluidIngredient of(TagKey<Fluid> tag, long amount) {
		return FluidIngredient.of(tag, amount);
	}

	@RemapForJS("ofTag")
	public static FluidIngredient of(TagKey<Fluid> tag, long amount, CompoundTag nbt) {
		return FluidIngredient.of(tag, amount, nbt);
	}

	@RemapForJS("ofTagId")
	public static FluidIngredient of(ResourceLocation tag, long amount) {
		return FluidIngredient.of(FluidTags.create(tag), amount);
	}

	@RemapForJS("ofTagId")
	public static FluidIngredient of(ResourceLocation tag, long amount, CompoundTag nbt) {
		return FluidIngredient.of(FluidTags.create(tag), amount, nbt);
	}
}