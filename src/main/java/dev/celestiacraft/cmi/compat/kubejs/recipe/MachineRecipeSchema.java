package dev.celestiacraft.cmi.compat.kubejs.recipe;

import dev.celestiacraft.libs.common.recipe.machine.MachineRecipe;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public interface MachineRecipeSchema {
	RecipeSchema SCHEMA = new RecipeSchema(MachineRecipeJS.class, MachineRecipeJS::new);

	class MachineRecipeJS extends RecipeJS {
		private final List<MachineRecipe.ItemInput> inputItems = new ArrayList<>();
		private final List<MachineRecipe.ItemOutput> outputItems = new ArrayList<>();
		private final List<MachineRecipe.FluidInput> inputFluids = new ArrayList<>();
		private final List<MachineRecipe.FluidOutput> outputFluids = new ArrayList<>();
		private int inputEnergy;
		private int outputEnergy;
		private int duration = 20;

		public MachineRecipeJS duration(int duration) {
			this.duration = Math.max(1, duration);
			save();
			return this;
		}

		public MachineRecipeJS inputItem(Object... items) {
			for (Object item : items) {
				InputItem input = InputItem.of(item);
				if (!input.isEmpty()) {
					inputItems.add(new MachineRecipe.ItemInput(input.ingredient, input.count));
				}
			}
			save();
			return this;
		}

		public MachineRecipeJS outputItem(Object... items) {
			for (Object item : items) {
				OutputItem output = OutputItem.of(item);
				if (!output.isEmpty()) {
					outputItems.add(new MachineRecipe.ItemOutput(output.item, output.hasChance() ? output.getChance() : 1D));
				}
			}
			save();
			return this;
		}

		public MachineRecipeJS inputFluid(Object... fluids) {
			for (Object fluid : fluids) {
				FluidStackJS stack = FluidStackJS.of(fluid);
				if (!stack.kjs$isEmpty()) {
					inputFluids.add(new MachineRecipe.FluidInput(toForgeStack(stack)));
				}
			}
			save();
			return this;
		}

		public MachineRecipeJS outputFluid(Object... fluids) {
			for (Object fluid : fluids) {
				FluidStackJS stack = FluidStackJS.of(fluid);
				if (!stack.kjs$isEmpty()) {
					outputFluids.add(new MachineRecipe.FluidOutput(toForgeStack(stack), stack.hasChance() ? stack.getChance() : 1D));
				}
			}
			save();
			return this;
		}

		public MachineRecipeJS inputEnergy(int energy) {
			inputEnergy = Math.max(0, energy);
			save();
			return this;
		}

		public MachineRecipeJS outputEnergy(int energy) {
			outputEnergy = Math.max(0, energy);
			save();
			return this;
		}

		public MachineRecipeJS inputItems(Object... items) {
			return inputItem(items);
		}

		public MachineRecipeJS outputItems(Object... items) {
			return outputItem(items);
		}

		public MachineRecipeJS inputFluids(Object... fluids) {
			return inputFluid(fluids);
		}

		public MachineRecipeJS outputFluids(Object... fluids) {
			return outputFluid(fluids);
		}

		@Override
		public void deserialize(boolean merge) {
			super.deserialize(merge);
			MachineRecipe recipe = MachineRecipe.fromJson(getOrCreateId(), getType(), json);
			inputItems.clear();
			outputItems.clear();
			inputFluids.clear();
			outputFluids.clear();
			inputItems.addAll(recipe.getInputItems());
			outputItems.addAll(recipe.getOutputItems());
			inputFluids.addAll(recipe.getInputFluids());
			outputFluids.addAll(recipe.getOutputFluids());
			inputEnergy = recipe.getInputEnergy();
			outputEnergy = recipe.getOutputEnergy();
			duration = recipe.getDuration();
		}

		@Override
		public void serialize() {
			json = buildRecipe().toJson();
		}

		private MachineRecipe buildRecipe() {
			return new MachineRecipe(
					getOrCreateId(),
					getType(),
					inputItems,
					outputItems,
					inputFluids,
					outputFluids,
					inputEnergy,
					outputEnergy,
					duration
			);
		}

		private static FluidStack toForgeStack(FluidStackJS stack) {
			FluidStack forgeStack = new FluidStack(stack.getFluid(), (int) stack.getAmount());
			if (stack.getNbt() != null) {
				forgeStack.setTag(stack.getNbt().copy());
			}
			return forgeStack;
		}
	}
}
