package dev.celestiacraft.cmi.common.recipe.accelerator;

import com.google.gson.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.celestiacraft.cmi.Cmi;

import java.util.*;

public class AcceleratorRecipe implements Recipe<SimpleContainer> {
	public final ResourceLocation id;
	public final List<Ingredient> inputs;
	public final Block targetBlock;
	public final List<OutputEntry> outputs;

	public AcceleratorRecipe(ResourceLocation id, List<Ingredient> inputs, Block targetBlock, List<OutputEntry> outputs) {
		this.id = id;
		this.inputs = inputs;
		this.targetBlock = targetBlock;
		this.outputs = outputs;
	}

	@Override
	public boolean matches(@NotNull SimpleContainer container, @NotNull Level level) {
		ItemStack stack = container.getItem(0);
		for (Ingredient ingredient : inputs) {
			if (ingredient.test(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return Type.INSTANCE;
	}

	public static class Type implements RecipeType<AcceleratorRecipe> {
		private Type() {
		}

		public static final Type INSTANCE = new Type();
	}

	public static class OutputEntry {
		public final Block block;
		public final float chance;

		public OutputEntry(Block block, float chance) {
			this.block = block;
			this.chance = chance;
		}
	}

	public static class Serializer implements RecipeSerializer<AcceleratorRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = Cmi.loadResource("accelerator");

		@Override
		public @NotNull AcceleratorRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
			List<Ingredient> inputs = new ArrayList<>();
			if (json.get("input").isJsonArray()) {
				JsonArray arr = json.getAsJsonArray("input");
				for (JsonElement el : arr) {
					inputs.add(Ingredient.fromJson(el));
				}
			} else {
				inputs.add(Ingredient.fromJson(json.get("input")));
			}

			String targetName = json.get("target").getAsString();
			Block targetBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(targetName));
			Objects.requireNonNull(targetBlock, "Unknown target block: " + targetName);

			List<OutputEntry> outputs = new ArrayList<>();
			JsonElement outputEl = json.get("output");

			if (outputEl.isJsonArray()) {
				for (JsonElement e : outputEl.getAsJsonArray()) {
					outputs.add(readOutput(e.getAsJsonObject()));
				}
			} else {
				outputs.add(readOutput(outputEl.getAsJsonObject()));
			}

			return new AcceleratorRecipe(id, inputs, targetBlock, outputs);
		}

		private static OutputEntry readOutput(JsonObject obj) {
			String id = obj.get("id").getAsString();
			Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(id));
			Objects.requireNonNull(block, "Unknown output block: " + id);

			float chance = obj.get("chance").getAsFloat();
			return new OutputEntry(block, chance);
		}

		@Override
		public @Nullable AcceleratorRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {

			int inSize = buf.readInt();
			List<Ingredient> inputs = new ArrayList<>();
			for (int i = 0; i < inSize; i++) {
				inputs.add(Ingredient.fromNetwork(buf));
			}

			Block targetBlock = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());

			int outSize = buf.readInt();
			List<OutputEntry> outputs = new ArrayList<>();
			for (int i = 0; i < outSize; i++) {
				Block b = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
				float c = buf.readFloat();
				outputs.add(new OutputEntry(b, c));
			}

			return new AcceleratorRecipe(id, inputs, targetBlock, outputs);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull AcceleratorRecipe recipe) {
			buf.writeInt(recipe.inputs.size());
			for (Ingredient ingredient : recipe.inputs) {
				ingredient.toNetwork(buf);
			}
			buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(recipe.targetBlock)));

			buf.writeInt(recipe.outputs.size());
			for (OutputEntry o : recipe.outputs) {
				buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(o.block)));
				buf.writeFloat(o.chance);
			}
		}
	}
}