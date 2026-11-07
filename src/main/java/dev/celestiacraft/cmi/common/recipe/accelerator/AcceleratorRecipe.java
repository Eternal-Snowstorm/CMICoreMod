package dev.celestiacraft.cmi.common.recipe.accelerator;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.celestiacraft.cmi.Cmi;

import java.util.*;

@Getter
@AllArgsConstructor
public class AcceleratorRecipe implements Recipe<SimpleContainer> {
	public final ResourceLocation id;
	public final List<Ingredient> inputs;
	public final Target target;
	public final List<OutputEntry> outputs;

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

	/**
	 * 被催生的方块。block 与 tag 互斥，同一时刻只有一个非空。
	 */
	public record Target(@Nullable Block block, @Nullable TagKey<Block> tag) {
		public static Target ofBlock(Block block) {
			return new Target(Objects.requireNonNull(block, "Target block cannot be null"), null);
		}

		public static Target ofTag(TagKey<Block> tag) {
			return new Target(null, Objects.requireNonNull(tag, "Target tag cannot be null"));
		}

		public boolean isTag() {
			return tag != null;
		}

		public boolean matches(BlockState state) {
			if (tag != null) {
				return state.is(tag);
			}
			return block != null && state.is(block);
		}

		public List<Block> displayBlocks() {
			if (block != null) {
				return List.of(block);
			}

			List<Block> blocks = new ArrayList<>();
			for (Block candidate : ForgeRegistries.BLOCKS.tags().getTag(tag)) {
				blocks.add(candidate);
			}
			return blocks;
		}

		public List<ItemStack> displayStacks() {
			List<ItemStack> stacks = new ArrayList<>();
			for (Block candidate : displayBlocks()) {
				ItemStack stack = candidate.asItem().getDefaultInstance();
				if (!stack.isEmpty()) {
					stacks.add(stack);
				}
			}

			if (stacks.isEmpty()) {
				stacks.add(Items.BARRIER.getDefaultInstance());
			}
			return stacks;
		}
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

			Target target = readTarget(json.get("target").getAsString());

			List<OutputEntry> outputs = new ArrayList<>();
			JsonElement outputEl = json.get("output");

			if (outputEl.isJsonArray()) {
				for (JsonElement e : outputEl.getAsJsonArray()) {
					outputs.add(readOutput(e.getAsJsonObject()));
				}
			} else {
				outputs.add(readOutput(outputEl.getAsJsonObject()));
			}

			return new AcceleratorRecipe(id, inputs, target, outputs);
		}

		/**
		 * target 支持两种写法："minecraft:stone" 与 "#minecraft:logs"
		 */
		private static Target readTarget(String value) {
			if (!value.isEmpty() && value.charAt(0) == '#') {
				return Target.ofTag(TagKey.create(Registries.BLOCK, ResourceLocation.parse(value.substring(1))));
			}

			Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(value));
			Objects.requireNonNull(block, "Unknown target block: " + value);
			return Target.ofBlock(block);
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

			Target target;
			if (buf.readBoolean()) {
				target = Target.ofTag(TagKey.create(Registries.BLOCK, buf.readResourceLocation()));
			} else {
				Block targetBlock = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
				target = Target.ofBlock(Objects.requireNonNull(targetBlock, "Unknown target block"));
			}

			int outSize = buf.readInt();
			List<OutputEntry> outputs = new ArrayList<>();
			for (int i = 0; i < outSize; i++) {
				Block block = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
				float chance = buf.readFloat();
				outputs.add(new OutputEntry(block, chance));
			}

			return new AcceleratorRecipe(id, inputs, target, outputs);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull AcceleratorRecipe recipe) {
			buf.writeInt(recipe.inputs.size());
			for (Ingredient ingredient : recipe.inputs) {
				ingredient.toNetwork(buf);
			}

			buf.writeBoolean(recipe.target.isTag());
			if (recipe.target.isTag()) {
				buf.writeResourceLocation(recipe.target.tag().location());
			} else {
				buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(recipe.target.block())));
			}

			buf.writeInt(recipe.outputs.size());
			for (OutputEntry o : recipe.outputs) {
				buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(o.block)));
				buf.writeFloat(o.chance);
			}
		}
	}
}