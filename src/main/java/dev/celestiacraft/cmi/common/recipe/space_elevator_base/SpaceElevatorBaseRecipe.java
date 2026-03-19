package dev.celestiacraft.cmi.common.recipe.space_elevator_base;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiRecipeSerializer;
import dev.celestiacraft.cmi.common.register.CmiRecipeType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

public class SpaceElevatorBaseRecipe implements Recipe<SimpleContainer> {
	private final ResourceLocation id;
	private final ResourceKey<Level> dimension;
	private final List<IngredientEntry> ingredients;

	public SpaceElevatorBaseRecipe(ResourceLocation id, ResourceKey<Level> dimension, List<IngredientEntry> ingredients) {
		this.id = id;
		this.dimension = dimension;
		this.ingredients = ingredients;
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
		return CmiRecipeSerializer.SPACE_ELEVATOR_BASE.get();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return CmiRecipeType.SPACE_ELEVATOR_BASE.get();
	}

	public ResourceKey<Level> dimension() {
		return dimension;
	}

	public List<IngredientEntry> ingredients() {
		return ingredients;
	}

	@Nullable
	public static SpaceElevatorBaseRecipe getRecipe(Level level, ResourceKey<Level> dimension) {
		return level.getRecipeManager().getAllRecipesFor(CmiRecipeType.SPACE_ELEVATOR_BASE.get()).stream()
				.filter(recipe -> recipe.dimension().equals(dimension))
				.findFirst()
				.orElse(null);
	}

	public static boolean hasIngredients(Player player, SpaceElevatorBaseRecipe recipe) {
		if (player.isCreative() || player.isSpectator()) {
			return true;
		}
		Inventory inventory = player.getInventory();
		for (IngredientEntry entry : recipe.ingredients()) {
			int count = 0;
			for (int i = 0; i < inventory.getContainerSize(); i++) {
				ItemStack stack = inventory.getItem(i);
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

	public static void consumeIngredients(Player player, SpaceElevatorBaseRecipe recipe) {
		if (player.isCreative() || player.isSpectator()) {
			return;
		}
		Inventory inventory = player.getInventory();
		for (IngredientEntry entry : recipe.ingredients()) {
			int remaining = entry.count();
			for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
				ItemStack stack = inventory.getItem(i);
				if (!entry.ingredient().test(stack)) {
					continue;
				}
				int removed = Math.min(remaining, stack.getCount());
				stack.shrink(removed);
				remaining -= removed;
			}
		}
	}

	public static List<Pair<ItemStack, Integer>> getDisplayIngredients(Player player, SpaceElevatorBaseRecipe recipe) {
		List<Pair<ItemStack, Integer>> result = new ArrayList<>();
		Inventory inventory = player.getInventory();
		for (IngredientEntry entry : recipe.ingredients()) {
			int amountOwned = 0;
			for (int i = 0; i < inventory.getContainerSize(); i++) {
				ItemStack stack = inventory.getItem(i);
				if (entry.ingredient().test(stack)) {
					amountOwned += stack.getCount();
				}
			}
			ItemStack displayStack = ItemStack.EMPTY;
			ItemStack[] matchingStacks = entry.ingredient().getItems();
			if (matchingStacks.length > 0) {
				displayStack = matchingStacks[0].copyWithCount(entry.count());
			}
			result.add(Pair.of(displayStack, amountOwned));
		}
		return result;
	}

	public record IngredientEntry(Ingredient ingredient, int count) {
	}

	public static class Type implements RecipeType<SpaceElevatorBaseRecipe> {
		public static final Type INSTANCE = new Type();
		private Type() {
		}
	}

	public static class Serializer implements RecipeSerializer<SpaceElevatorBaseRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = Cmi.loadResource("space_elevator_base");

		@Override
		public @NotNull SpaceElevatorBaseRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
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
			return new SpaceElevatorBaseRecipe(id, dimension, ingredients);
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
		public @Nullable SpaceElevatorBaseRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
			ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
			int size = buf.readVarInt();
			List<IngredientEntry> ingredients = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				Ingredient ingredient = Ingredient.fromNetwork(buf);
				int count = buf.readVarInt();
				ingredients.add(new IngredientEntry(ingredient, count));
			}
			return new SpaceElevatorBaseRecipe(id, dimension, ingredients);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SpaceElevatorBaseRecipe recipe) {
			buf.writeResourceLocation(recipe.dimension.location());
			buf.writeVarInt(recipe.ingredients.size());
			for (IngredientEntry ingredient : recipe.ingredients) {
				ingredient.ingredient().toNetwork(buf);
				buf.writeVarInt(ingredient.count());
			}
		}
	}
}
