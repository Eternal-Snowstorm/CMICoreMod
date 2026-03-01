package dev.celestiacraft.cmi.compat.jei.category;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import dev.celestiacraft.cmi.common.recipe.water_pump.WaterPumpRecipe;
import dev.celestiacraft.cmi.common.register.CmiBlocks;
import dev.celestiacraft.cmi.compat.jei.api.CmiGuiTextures;
import dev.celestiacraft.cmi.compat.jei.api.CmiJeiRecipeType;
import dev.celestiacraft.cmi.compat.jei.category.multiblock.WaterPumpMultiblock;
import dev.celestiacraft.cmi.api.CmiLang;
import dev.celestiacraft.libs.client.ClientRenderUtils;
import dev.celestiacraft.libs.compat.jei.categoty.SimpleJeiCategory;

import java.util.Collections;

public class WaterPumpCategory {
	private static final WaterPumpMultiblock WATER_PUMP_MB = new WaterPumpMultiblock();

	public static SimpleJeiCategory<WaterPumpRecipe> builder(IGuiHelper helper) {
		return SimpleJeiCategory.builder(CmiJeiRecipeType.WATER_PUMP, helper)
				.setTitle(CmiLang.JeiLang.setTranCategoryTitle("water_pump"))
				.setSize(178, 72)
				.setIcon(() -> {
					return new DoubleItemIcon(
							() -> CmiBlocks.WATER_PUMP.get().asItem().getDefaultInstance(),
							() -> Items.WATER_BUCKET.getDefaultInstance()
					);
				})
				.setBackground(0, 0)
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.OUTPUT, 150, 30)
							.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
							.addFluidStack(Fluids.WATER, Integer.MAX_VALUE)
							.addItemStack(Items.WATER_BUCKET.getDefaultInstance());
				})
				.setTooltips((recipe, view, mouseX, mouseY) -> {
					if (ClientRenderUtils.isCursorInsideBounds(102, 21, 14, 14, mouseX, mouseY)) {
						return ImmutableList.of(Component.translatable("jei.catalyst.cmi.water_pump.complete"));
					}
					return Collections.emptyList();
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					CmiGuiTextures.WATER_PUMP_ARROW.render(graphics, 80, 20);
					WATER_PUMP_MB.draw(graphics, 30, 5);
					PoseStack pose = graphics.pose();
					pose.popPose();
				})
				.build();
	}
}