package dev.celestiacraft.cmi.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.recipe.void_dust_collector.VoidDustCollectorRecipe;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.compat.jei.api.CmiJeiRecipeType;
import dev.celestiacraft.cmi.compat.jei.category.structure.VoidDustCollectorStructure;
import dev.celestiacraft.cmi.utils.ModResources;
import dev.celestiacraft.libs.compat.jei.categoty.SimpleJeiCategory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;

public class VoidDustCollectorCategory {
	private static final VoidDustCollectorStructure VOID_MB = new VoidDustCollectorStructure();

	public static SimpleJeiCategory<VoidDustCollectorRecipe> builder(IGuiHelper helper) {
		return SimpleJeiCategory.builder(CmiJeiRecipeType.VOID_DUST_COLLECTOR, helper)
				.setTitle(CmiLang.JeiLang.setTranCategoryTitle("void_dust_collector"))
				.setSize(178, 72)
				.setIcon(() -> {
					return new DoubleItemIcon(
							() -> ModResources.VOID_SPRING.getItemStack(),
							() -> CmiBlock.VOID_DUST_COLLECTOR.asStack()
					);
				})
				.setBackground(0, 0)
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 35)
							.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
							.addItemStack(ModResources.VOID_DUST.getItemStack());
					builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
							.addItemStack(ModResources.VOID_SPRING.getItemStack())
							.addItemStack(CmiBlock.VOID_DUST_COLLECTOR.asStack());
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					AllGuiTextures.JEI_SHADOW.render(graphics, 50, 50);
					AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 94, 20);
					VOID_MB.draw(graphics, 60, 10);
					PoseStack pose = graphics.pose();
					pose.popPose();
				})
				.build();
	}
}