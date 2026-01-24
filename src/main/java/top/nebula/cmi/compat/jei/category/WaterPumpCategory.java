package top.nebula.cmi.compat.jei.category;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.waterpump.WaterPumpRecipe;
import top.nebula.cmi.common.register.ModBlocks;
import top.nebula.cmi.compat.jei.CmiGuiTextures;
import top.nebula.cmi.compat.jei.category.multiblock.WaterPumpMultiblock;
import top.nebula.libs.client.ClientRenderUtils;

import java.util.Collections;
import java.util.List;

public class WaterPumpCategory implements IRecipeCategory<WaterPumpRecipe> {
	private final IDrawable background;
	private final IDrawable icon;
	private final WaterPumpMultiblock waterPump = new WaterPumpMultiblock();

	public static final RecipeType<WaterPumpRecipe> WATER_PUMP_TYPE = RecipeType.create(
			Cmi.MODID,
			"water_pump",
			WaterPumpRecipe.class
	);

	public WaterPumpCategory(IGuiHelper helper) {
		this.background = helper.createBlankDrawable(0, 0);
		this.icon = helper.createDrawableItemStack(ModBlocks.WATER_PUMP.get().asItem().getDefaultInstance());
	}

	@Override
	public @NotNull RecipeType<WaterPumpRecipe> getRecipeType() {
		return WATER_PUMP_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.category.cmi.water_pump");
	}

	@Override
	public @NotNull IDrawable getBackground() {
		return this.background;
	}

	@Override
	public @NotNull IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public int getWidth() {
		return 178;
	}

	@Override
	public int getHeight() {
		return 72;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull WaterPumpRecipe recipe, @NotNull IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.OUTPUT, 150, 30)
				.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
				.addFluidStack(Fluids.WATER, Integer.MAX_VALUE)
				.addItemStack(Items.WATER_BUCKET.getDefaultInstance());
	}

	@Override
	public @NotNull List<Component> getTooltipStrings(@NotNull WaterPumpRecipe recipe, @NotNull IRecipeSlotsView view, double mouseX, double mouseY) {
		if (ClientRenderUtils.isCursorInsideBounds(102, 21, 14, 14, mouseX, mouseY)) {
			return ImmutableList.of(Component.translatable("jei.catalyst.cmi.water_pump.complete"));
		}
		return Collections.emptyList();
	}

	@Override
	public void draw(@NotNull WaterPumpRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
		CmiGuiTextures.WATER_PUMP_ARROW.render(graphics, 80, 20);
		this.waterPump.draw(graphics, 30, 5);
		PoseStack pose = graphics.pose();
		pose.popPose();
	}
}