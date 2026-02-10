package top.nebula.cmi.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.accelerator.AcceleratorRecipe;
import top.nebula.cmi.compat.jei.api.CmiJeiRecipeType;
import top.nebula.cmi.api.CmiLang;
import top.nebula.libs.compat.jei.categoty.SimpleJeiCategory;

public class AcceleratorCategory {
	public static final Lazy<Item> ACCELERATOR_ITEM = Lazy.of(() -> {
		return ForgeRegistries.ITEMS.getValue(Cmi.loadResource("accelerator"));
	});
	private static final Lazy<Block> ACCELERATOR_BLOCK = Lazy.of(() -> {
		return ForgeRegistries.BLOCKS.getValue(Cmi.loadResource("accelerator"));
	});
	private static final Lazy<Item> PRECISION_MECHANISM = Lazy.of(() -> {
		return ForgeRegistries.ITEMS.getValue(ResourceLocation.parse("create:precision_mechanism"));
	});

	public static SimpleJeiCategory<AcceleratorRecipe> builder(IGuiHelper helper) {
		return SimpleJeiCategory.builder(CmiJeiRecipeType.ACCELERATOR, helper)
				.setTitle(CmiLang.JeiLang.setCategory("accelerator"))
				.setSize(178, 72)
				.setIcon(() -> {
					return new DoubleItemIcon(
							() -> ACCELERATOR_ITEM.get().getDefaultInstance(),
							() -> PRECISION_MECHANISM.get().getDefaultInstance()
					);
				})
				.setBackground(0, 0)
				.setRecipe((builder, recipe, group) -> {
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
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
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
				})
				.build();
	}
}