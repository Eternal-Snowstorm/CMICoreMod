package dev.celestiacraft.cmi.common.recipe.fluid_burn;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.celestiacraft.cmi.common.register.CmiRecipeSerializer;
import dev.celestiacraft.cmi.common.register.CmiRecipeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class FluidBurnRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	@Getter
	private final FluidIngredient fluid;
	@Getter
	private final int hu;

	@Override
	public boolean matches(@NotNull Container container, @NotNull Level level) {
		return false;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return false;
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	public boolean matches(FluidStack stack) {
		return fluid.test(stack);
	}

	public int getAmount() {
		return fluid.getRequiredAmount();
	}

	public float getHuPerMb() {
		return (float) hu / getAmount();
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<FluidBurnRecipe> getSerializer() {
		return CmiRecipeSerializer.FLUID_BURN.get();
	}

	@Override
	public @NotNull RecipeType<FluidBurnRecipe> getType() {
		return CmiRecipeType.FLUID_BURN.get();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static class Type implements RecipeType<FluidBurnRecipe> {
		public static final Type INSTANCE = new Type();

		private Type() {
		}
	}

	public static class Serializer implements RecipeSerializer<FluidBurnRecipe> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public @NotNull FluidBurnRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
			JsonObject fluidObj = GsonHelper.getAsJsonObject(json, "fluid");

			FluidIngredient ingredient = FluidIngredient.deserialize(fluidObj);
			int hu = GsonHelper.getAsInt(json, "hu");

			return new FluidBurnRecipe(id, ingredient, hu);
		}

		@Override
		public FluidBurnRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
			FluidIngredient ingredient = FluidIngredient.read(buf);
			int hu = buf.readInt();

			return new FluidBurnRecipe(id, ingredient, hu);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, FluidBurnRecipe recipe) {
			recipe.fluid.write(buf);
			buf.writeInt(recipe.getHu());
		}
	}
}