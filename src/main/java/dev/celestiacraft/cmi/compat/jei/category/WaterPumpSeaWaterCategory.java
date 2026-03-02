package dev.celestiacraft.cmi.compat.jei.category;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.water_pump.WaterPumpSeaWaterRecipe;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.compat.jei.api.CmiGuiTextures;
import dev.celestiacraft.cmi.compat.jei.api.CmiJeiRecipeType;
import dev.celestiacraft.cmi.compat.jei.category.structure.WaterPumpStructure;
import dev.celestiacraft.cmi.api.CmiLang;
import dev.celestiacraft.libs.client.ClientRenderUtils;
import dev.celestiacraft.libs.compat.jei.categoty.SimpleJeiCategory;

import java.util.Collections;

public class WaterPumpSeaWaterCategory {
	private static final WaterPumpStructure WATER_PUMP_MB = new WaterPumpStructure();
	private static final Fluid SEA_WATER = ForgeRegistries.FLUIDS.getValue(Cmi.loadResource("sea_water"));
	private static final Item SEA_WATER_BUCKET = ForgeRegistries.ITEMS.getValue(Cmi.loadResource("sea_water_bucket"));

	public static SimpleJeiCategory<WaterPumpSeaWaterRecipe> builder(IGuiHelper helper) {
		return SimpleJeiCategory.builder(CmiJeiRecipeType.SEA_WATER_PUMP, helper)
				.setTitle(CmiLang.JeiLang.setTranCategoryTitle("water_pump_sea_water"))
				.setSize(178, 72)
				.setIcon(() -> {
					return new DoubleItemIcon(
							() -> CmiBlock.WATER_PUMP.get().asItem().getDefaultInstance(),
							() -> SEA_WATER_BUCKET.getDefaultInstance()
					);
				})
				.setBackground(0, 0)
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.OUTPUT, 150, 30)
							.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
							.addFluidStack(SEA_WATER, Integer.MAX_VALUE)
							.addItemStack(SEA_WATER_BUCKET.getDefaultInstance());
				})
				.setTooltips((recipe, view, mouseX, mouseY) -> {
					if (ClientRenderUtils.isCursorInsideBounds(86, 21, 14, 14, mouseX, mouseY)) {
						return ImmutableList.of(Component.translatable("jei.catalyst.cmi.water_pump.complete"));
					}
					if (ClientRenderUtils.isCursorInsideBounds(102, 21, 14, 14, mouseX, mouseY)) {
						return ImmutableList.of(Component.translatable("jei.catalyst.cmi.water_pump.ocean"));
					}
					if (ClientRenderUtils.isCursorInsideBounds(118, 21, 14, 14, mouseX, mouseY)) {
						return ImmutableList.of(Component.translatable("jei.catalyst.cmi.water_pump.pos"));
					}
					return Collections.emptyList();
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					CmiGuiTextures.WATER_PUMP_SEA_WATER_ARROW.render(graphics, 80, 20);
					WATER_PUMP_MB.draw(graphics, 30, 5);
					PoseStack pose = graphics.pose();
					pose.popPose();
				})
				.build();
	}
}