package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.celestiacraft.cmi.common.recipe.freezing.FreezingRecipe;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.belt_grinder.GrindingRecipe;

import java.util.Optional;
import java.util.function.Supplier;

public class CmiCreateRecipe {
	private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	private static final DeferredRegister<RecipeType<?>> TYPES;

	public static final TypeInfo<GrindingRecipe> GRINDING;
	public static final TypeInfo<FreezingRecipe> FREEZING;

	static {
		SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Cmi.MODID);
		TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Cmi.MODID);

		GRINDING = register("grinding", GrindingRecipe::new);
		FREEZING = register("freezing", FreezingRecipe::new);
	}

	private static <T extends ProcessingRecipe<?>> TypeInfo<T> register(String name, ProcessingRecipeBuilder.ProcessingRecipeFactory<T> factory) {
		ResourceLocation id = Cmi.loadResource(name);

		Supplier<RecipeSerializer<?>> serializer = SERIALIZERS.register(name, () -> {
			return new ProcessingRecipeSerializer<>(factory);
		});

		Supplier<RecipeType<?>> type = TYPES.register(name, () -> {
			return RecipeType.simple(id);
		});

		return new TypeInfo<>(id, serializer, type);
	}

	public static class TypeInfo<T> implements IRecipeTypeInfo {
		@Getter
		private final ResourceLocation id;
		private final Supplier<RecipeSerializer<?>> serializer;
		private final Supplier<RecipeType<?>> type;

		private TypeInfo(ResourceLocation id, Supplier<RecipeSerializer<?>> serializer, Supplier<RecipeType<?>> type) {
			this.id = id;
			this.serializer = serializer;
			this.type = type;
		}

		public <S extends RecipeSerializer<?>> S getSerializer() {
			return (S) serializer.get();
		}

		public <R extends RecipeType<?>> R getType() {
			return (R) type.get();
		}

		public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level level) {
			return level.getRecipeManager().getRecipeFor(getType(), inv, level);
		}
	}

	public static void register(IEventBus bus) {
		SERIALIZERS.register(bus);
		TYPES.register(bus);
	}
}