package dev.celestiacraft.cmi.common.block.belt_grinder;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiCreateRecipe;
import dev.celestiacraft.cmi.compat.jei.category.CmiSequencedAssemblySubCategory;
import dev.celestiacraft.cmi.api.CmiLang;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class GrindingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {
	public GrindingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
		super(CmiCreateRecipe.GRINDING, params);
	}

	public static final ProcessingRecipeSerializer<GrindingRecipe> SERIALIZER = new ProcessingRecipeSerializer<>(GrindingRecipe::new);

	@Override
	public boolean matches(RecipeWrapper inv, @NotNull Level worldIn) {
		if (inv.isEmpty()) {
			return false;
		}
		return ingredients.get(0).test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 64;
	}

	@Override
	protected int getMaxOutputCount() {
		return 64;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getDescriptionForAssembly() {
		return CmiLang.translateDirect("recipe.assembly.grinding");
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(CmiBlock.BELT_GRINDER.get());
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {
	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> CmiSequencedAssemblySubCategory.AssemblyGrinding::new;
	}
}