package dev.celestiacraft.cmi.common.recipe.space_elevator_construction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiRecipeSerializer;
import dev.celestiacraft.cmi.common.register.CmiRecipeType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

public class SpaceElevatorConstructionRecipe implements Recipe<SimpleContainer> {
	private final ResourceLocation id;
	private final ResourceKey<Level> dimension;
	private final List<IngredientEntry> ingredients;
	private final List<FluidIngredientEntry> fluidIngredients;

	public SpaceElevatorConstructionRecipe(ResourceLocation id, ResourceKey<Level> dimension, List<IngredientEntry> ingredients, List<FluidIngredientEntry> fluidIngredients) {
		this.id = id;
		this.dimension = dimension;
		this.ingredients = ingredients;
		this.fluidIngredients = fluidIngredients;
	}

	@Override
	public boolean matches(@NotNull SimpleContainer container, @NotNull Level level) {
		for (IngredientEntry entry : ingredients) {
			int count = 0;
			for (int i = 0; i < container.getContainerSize(); i++) {
				ItemStack stack = container.getItem(i);
				if (entry.ingredient().test(stack)) {
					count += stack.getCount();
				}
			}
			if (count < entry.count()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return CmiRecipeSerializer.SPACE_ELEVATOR_CONSTRUCTION.get();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return CmiRecipeType.SPACE_ELEVATOR_CONSTRUCTION.get();
	}

	public ResourceKey<Level> dimension() {
		return dimension;
	}

	public List<IngredientEntry> ingredients() {
		return ingredients;
	}

	public List<FluidIngredientEntry> fluidIngredients() {
		return fluidIngredients;
	}

	@Nullable
	public static SpaceElevatorConstructionRecipe getRecipe(Level level, ResourceKey<Level> dimension) {
		return level.getRecipeManager()
				.getAllRecipesFor(CmiRecipeType.SPACE_ELEVATOR_CONSTRUCTION.get())
				.stream()
				.filter((recipe) -> {
					return recipe.dimension().equals(dimension);
				})
				.findFirst()
				.orElse(null);
	}

	public static List<DisplayIngredient> getDisplayIngredients(SpaceElevatorConstructionRecipe recipe, IntUnaryOperator ownedProvider) {
		List<DisplayIngredient> result = new ArrayList<>();
		for (int ingredientIndex = 0; ingredientIndex < recipe.ingredients().size(); ingredientIndex++) {
			IngredientEntry entry = recipe.ingredients().get(ingredientIndex);
			ItemStack displayStack = ItemStack.EMPTY;
			ItemStack[] matchingStacks = entry.ingredient().getItems();
			if (matchingStacks.length > 0) {
				displayStack = matchingStacks[0].copyWithCount(1);
			}
			result.add(new DisplayIngredient(displayStack, entry.count(), Math.max(0, ownedProvider.applyAsInt(ingredientIndex))));
		}
		return result;
	}

	public static List<DisplayFluidIngredient> getDisplayFluidIngredients(SpaceElevatorConstructionRecipe recipe, IntUnaryOperator ownedProvider) {
		List<DisplayFluidIngredient> result = new ArrayList<>();
		for (int i = 0; i < recipe.fluidIngredients().size(); i++) {
			FluidIngredientEntry entry = recipe.fluidIngredients().get(i);
			result.add(new DisplayFluidIngredient(entry.displayStack(), entry.amount(), Math.max(0, ownedProvider.applyAsInt(i))));
		}
		return result;
	}

	public static float getCompletionRatio(SpaceElevatorConstructionRecipe recipe, IntUnaryOperator ownedProvider, boolean bypassRequirements) {
		if (bypassRequirements) {
			return 1.0F;
		}

		int totalRequired = 0;
		int totalOwned = 0;
		for (DisplayIngredient ingredient : getDisplayIngredients(recipe, ownedProvider)) {
			totalRequired += ingredient.required();
			totalOwned += Math.min(ingredient.owned(), ingredient.required());
		}
		if (totalRequired <= 0) {
			return 1.0F;
		}
		return Math.min(1.0F, totalOwned / (float) totalRequired);
	}

	public record IngredientEntry(Ingredient ingredient, int count) {
	}

	public record DisplayIngredient(ItemStack stack, int required, int owned) {
		public boolean complete(boolean bypassRequirements) {
			return bypassRequirements || owned >= required;
		}
	}

	public record DisplayFluidIngredient(net.minecraftforge.fluids.FluidStack stack, int required, int owned) {
		public boolean complete(boolean bypassRequirements) {
			return bypassRequirements || owned >= required;
		}
	}

	public static class Type implements RecipeType<SpaceElevatorConstructionRecipe> {
		public static final Type INSTANCE = new Type();

		private Type() {
		}
	}

	public static class Serializer implements RecipeSerializer<SpaceElevatorConstructionRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = Cmi.loadResource("space_elevator_construction");

		@Override
		public @NotNull SpaceElevatorConstructionRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
			String dimensionId = json.get("dimension").getAsString();
			ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimensionId));

			JsonArray ingredientsJson = json.getAsJsonArray("ingredients");
			if (ingredientsJson == null) {
				throw new JsonParseException("Missing ingredients for recipe " + id);
			}

			List<IngredientEntry> ingredients = new ArrayList<>();
			for (int i = 0; i < ingredientsJson.size(); i++) {
				ingredients.add(parseIngredientEntry(ingredientsJson.get(i), id));
			}

			List<FluidIngredientEntry> fluidIngredients = new ArrayList<>();
			if (json.has("fluid_ingredients")) {
				JsonArray fluidJson = json.getAsJsonArray("fluid_ingredients");
				for (int i = 0; i < fluidJson.size(); i++) {
					JsonElement element = fluidJson.get(i);
					if (!element.isJsonObject()) {
						throw new JsonParseException("Invalid fluid ingredient entry for recipe " + id);
					}
					fluidIngredients.add(FluidIngredientEntry.fromJson(element.getAsJsonObject()));
				}
			}
			return new SpaceElevatorConstructionRecipe(id, dimension, ingredients, fluidIngredients);
		}

		private static IngredientEntry parseIngredientEntry(JsonElement ingredientElement, ResourceLocation recipeId) {
			if (!ingredientElement.isJsonObject()) {
				throw new JsonParseException("Invalid ingredient entry for recipe " + recipeId);
			}

			JsonObject ingredientJson = ingredientElement.getAsJsonObject();
			if (!ingredientJson.has("item") && !ingredientJson.has("tag")) {
				throw new JsonParseException("Ingredient entry must define item or tag for recipe " + recipeId);
			}

			int count = ingredientJson.has("count") ? ingredientJson.get("count").getAsInt() : 1;
			JsonObject standardIngredientJson = ingredientJson.deepCopy();
			standardIngredientJson.remove("count");
			Ingredient ingredient = Ingredient.fromJson(standardIngredientJson);
			return new IngredientEntry(ingredient, count);
		}

		@Override
		public @Nullable SpaceElevatorConstructionRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
			ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
			int size = buf.readVarInt();
			List<IngredientEntry> ingredients = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				Ingredient ingredient = Ingredient.fromNetwork(buf);
				int count = buf.readVarInt();
				ingredients.add(new IngredientEntry(ingredient, count));
			}
			int fluidSize = buf.readVarInt();
			List<FluidIngredientEntry> fluidIngredients = new ArrayList<>();
			for (int i = 0; i < fluidSize; i++) {
				fluidIngredients.add(FluidIngredientEntry.fromNetwork(buf));
			}
			return new SpaceElevatorConstructionRecipe(id, dimension, ingredients, fluidIngredients);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SpaceElevatorConstructionRecipe recipe) {
			buf.writeResourceLocation(recipe.dimension.location());
			buf.writeVarInt(recipe.ingredients.size());
			for (IngredientEntry ingredient : recipe.ingredients) {
				ingredient.ingredient().toNetwork(buf);
				buf.writeVarInt(ingredient.count());
			}
			buf.writeVarInt(recipe.fluidIngredients.size());
			for (FluidIngredientEntry fluidIngredient : recipe.fluidIngredients) {
				fluidIngredient.toNetwork(buf);
			}
		}
	}
}