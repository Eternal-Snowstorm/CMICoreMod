package top.nebula.cmi.compat.jei.category;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.water_pump.WaterPumpSeaWaterRecipe;
import top.nebula.cmi.common.register.CmiBlocks;
import top.nebula.cmi.compat.jei.CmiGuiTextures;
import top.nebula.cmi.compat.jei.api.CmiJeiRecipeType;
import top.nebula.cmi.compat.jei.category.multiblock.WaterPumpMultiblock;
import top.nebula.cmi.utils.CmiLang;
import top.nebula.libs.client.ClientRenderUtils;
import top.nebula.libs.compat.jei.categoty.SimpleJeiCategory;

import java.util.Collections;

public class WaterPumpSeaWaterCategory {
	private static final WaterPumpMultiblock WATER_PUMP_MB = new WaterPumpMultiblock();

	private static final Lazy<Fluid> SEA_WATER = Lazy.of(() -> {
		return ForgeRegistries.FLUIDS.getValue(Cmi.loadResource("sea_water"));
	});

	private static final Lazy<Item> SEA_WATER_BUCKET = Lazy.of(() -> {
		return ForgeRegistries.ITEMS.getValue(Cmi.loadResource("sea_water_bucket"));
	});

	public static SimpleJeiCategory<WaterPumpSeaWaterRecipe> builder(IGuiHelper helper) {
		return SimpleJeiCategory.builder(CmiJeiRecipeType.SEA_WATER_PUMP, helper)
				.setTitle(CmiLang.JeiLang.setCategory("water_pump_sea_water"))
				.setSize(178, 72)
				.setIcon(CmiBlocks.WATER_PUMP.get().asItem().getDefaultInstance())
				.setBackground(0, 0)
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.OUTPUT, 150, 30)
							.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
							.addFluidStack(SEA_WATER.get(), Integer.MAX_VALUE)
							.addItemStack(SEA_WATER_BUCKET.get().getDefaultInstance());
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