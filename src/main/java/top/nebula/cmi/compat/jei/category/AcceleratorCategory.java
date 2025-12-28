package top.nebula.cmi.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.accelerator.AcceleratorRecipe;

public class AcceleratorCategory implements IRecipeCategory<AcceleratorRecipe> {
	private final IDrawable background;
	private final IDrawable icon;

	public static final Lazy<Item> ACCELERATOR_ITEM = Lazy.of(() -> {
		return BuiltInRegistries.ITEM.get(Cmi.loadResource("accelerator"));
	});
	public static final Lazy<Block> ACCELERATOR_BLOCK = Lazy.of(() -> {
		return BuiltInRegistries.BLOCK.get(Cmi.loadResource("accelerator"));
	});
	private static final Lazy<Item> PRECISION_MECHANISM = Lazy.of(() -> {
		return BuiltInRegistries.ITEM.get(ResourceLocation.parse("create:precision_mechanism"));
	});
	public static final ResourceLocation UID = Cmi.loadResource("accelerator");
	public static final RecipeType<AcceleratorRecipe> ACCELERATOR_TYPE = RecipeType.create(
			Cmi.MODID,
			"accelerator",
			AcceleratorRecipe.class
	);

	public AcceleratorCategory(IGuiHelper helper) {
		this.background = helper.createBlankDrawable(0, 0);
		this.icon = new DoubleItemIcon(
				() -> ACCELERATOR_ITEM.get().getDefaultInstance(),
				() -> PRECISION_MECHANISM.get().getDefaultInstance()
		);
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
	public @NotNull RecipeType<AcceleratorRecipe> getRecipeType() {
		return ACCELERATOR_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.category.cmi.accelerator");
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
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull AcceleratorRecipe recipe, @NotNull IFocusGroup group) {
		builder.addSlot(RecipeIngredientRole.INPUT, 51, 5)
				.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
				.addIngredients(Ingredient.merge(recipe.inputs));

		builder.addSlot(RecipeIngredientRole.INPUT, 27, 38)
				.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
				.addItemStack(recipe.targetBlock.asItem().getDefaultInstance());

		int xStart = 120;
		int yStart = 5;
		int id = 0;

		for (AcceleratorRecipe.OutputEntry out : recipe.outputs) {
			final int OUTPUT_SLOT_SIZE = 18;
			final int OUTPUT_SLOT_GAP = 1;

			int x = xStart + (id % 3) * (OUTPUT_SLOT_SIZE + OUTPUT_SLOT_GAP);
			int y = yStart + (id / 3) * (OUTPUT_SLOT_SIZE + OUTPUT_SLOT_GAP);
			float chance = out.chance;

			builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
					.setBackground(CreateRecipeCategory.getRenderedSlot(chance), -1, -1)
					.addItemStack(out.block.asItem().getDefaultInstance())
					.addTooltipCallback((view, tooltip) -> {
						MutableComponent tranKey = Component.translatable(
								"create.recipe.processing.chance",
								chance < 0.01 ? "<1" : (int) (chance * 100)
						).withStyle(ChatFormatting.GOLD);

						if (chance != 1) {
							/*
							 * index参数指的是在第几行添加Tooltip
							 * 如果写1就指在第一行添加Tooltip
							 * 其它数字同理
							 */
							tooltip.add(1, tranKey);
						}
					});
			id++;
		}
	}

	@Override
	public void draw(@NotNull AcceleratorRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 62, 47);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 74, 10);

		PoseStack pose = graphics.pose();

		pose.pushPose();
		pose.translate(74, 51, 100);
		pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
		pose.mulPose(Axis.YP.rotationDegrees(22.5f));

		AnimatedKinetics.defaultBlockElement(ACCELERATOR_BLOCK.get().defaultBlockState())
				.rotateBlock(0, 180, 0)
				.atLocal(0.0, 0.0, 0.0)
				.scale(24.0)
				.render(graphics);

		pose.popPose();
	}
}