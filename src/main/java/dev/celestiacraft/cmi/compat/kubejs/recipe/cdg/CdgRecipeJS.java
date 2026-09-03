package dev.celestiacraft.cmi.compat.kubejs.recipe.cdg;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.BlockTagIngredient;
import dev.latvian.mods.kubejs.create.CreateInputFluid;
import dev.latvian.mods.kubejs.create.platform.FluidIngredientHelper;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.MapJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class CdgRecipeJS extends RecipeJS {
	@Override
	public InputFluid readInputFluid(Object from) {
		return normalizeFluid(from);
	}

	@Override
	public JsonElement writeInputFluid(InputFluid value) {
		return serializeFluid(value);
	}

	private InputFluid normalizeFluid(Object from) {
		if (from == null) {
			return CreateInputFluid.EMPTY;
		}

		if (from instanceof CreateInputFluid f) {
			return f;
		}

		if (from instanceof FluidIngredient ingredient) {
			return new CreateInputFluid(ingredient);
		}

		if (from instanceof FluidStackJS fluid) {
			return new CreateInputFluid(FluidIngredientHelper.toFluidIngredient(fluid));
		}

		if (from instanceof FluidStack fluid) {
			return new CreateInputFluid(FluidIngredient.fromFluidStack(fluid));
		}

		JsonObject json = MapJS.json(from);
		return json != null
				? new CreateInputFluid(FluidIngredient.deserialize(json))
				: CreateInputFluid.EMPTY;
	}

	private JsonElement serializeFluid(InputFluid value) {
		if (value instanceof CreateInputFluid fluid) {
			return fluid.ingredient().serialize();
		}

		if (value instanceof FluidIngredient ingredient) {
			return ingredient.serialize();
		}

		if (value instanceof FluidStackJS fluid) {
			return FluidIngredientHelper
					.toFluidIngredient(fluid)
					.serialize();
		}

		return FluidIngredient.EMPTY.serialize();
	}

	public boolean inputItemHasPriority(Object from) {
		if (isDirectItem(from)) {
			return true;
		}

		InputItem input = readInputItem(from);
		return isValidItemInput(input);
	}

	public boolean inputFluidHasPriority(Object from) {
		return from instanceof InputFluid
				|| FluidIngredient.isFluidIngredient(MapJS.json(from));
	}

	private boolean isDirectItem(Object from) {
		return from instanceof InputItem
				|| from instanceof Ingredient
				|| from instanceof ItemStack;
	}

	private boolean isValidItemInput(InputItem input) {
		Ingredient ingredient = input.ingredient;

		if (ingredient instanceof BlockTagIngredient tag) {
			return !((TagContext) TagContext.INSTANCE.getValue())
					.isEmpty(tag.getTag());
		}

		return !input.isEmpty();
	}

	@Override
	public OutputItem readOutputItem(Object from) {
		if (from instanceof ProcessingOutput output) {
			return OutputItem.of(
					output.getStack(),
					output.getChance()
			);
		}

		OutputItem base = super.readOutputItem(from);
		return applyChance(from, base);
	}

	private OutputItem applyChance(Object from, OutputItem item) {
		if (from instanceof JsonObject object && object.has("chance")) {
			return item.withChance(object.get("chance").getAsDouble());
		}
		return item;
	}

	public RecipeJS heated() {
		return setHeat(HeatCondition.HEATED);
	}

	public RecipeJS superheated() {
		return setHeat(HeatCondition.SUPERHEATED);
	}

	private RecipeJS setHeat(HeatCondition condition) {
		return setValue(CdgRecipesSchema.HEAT_REQUIREMENT, condition.serialize());
	}
}