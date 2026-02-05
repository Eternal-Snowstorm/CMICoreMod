package top.nebula.cmi.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.foundation.gui.AllGuiTextures;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.void_dust_collector.VoidDustCollectorRecipe;
import top.nebula.cmi.common.register.ModBlocks;
import top.nebula.cmi.compat.jei.category.multiblock.VoidDustCollectorMultiblock;

public class VoidDustCollectorCategory implements IRecipeCategory<VoidDustCollectorRecipe> {
	private static final Lazy<Block> VOID_SPRING = Lazy.of(() -> {
		return ForgeRegistries.BLOCKS.getValue(Cmi.loadResource("void_spring"));
	});
	private static final Lazy<Item> VOID_DUST = Lazy.of(() -> {
		return ForgeRegistries.ITEMS.getValue(Cmi.loadResource("void_dust"));
	});
	private final IDrawable background;
	private final IDrawable icon;
	private final VoidDustCollectorMultiblock voidDustCollectorMB = new VoidDustCollectorMultiblock();

	public static final RecipeType<VoidDustCollectorRecipe> VOID_DUST_COLLECTOR_TYPE = RecipeType.create(
			Cmi.MODID,
			"void_dust_collector",
			VoidDustCollectorRecipe.class
	);

	public VoidDustCollectorCategory(IGuiHelper helper) {
		this.background = helper.createBlankDrawable(0, 0);
		this.icon = new DoubleItemIcon(
				() -> VOID_SPRING.get().asItem().getDefaultInstance(),
				() -> ModBlocks.VOID_DUST_COLLECTOR.get().asItem().getDefaultInstance()
		);
	}

	@Override
	public @NotNull RecipeType<VoidDustCollectorRecipe> getRecipeType() {
		return VOID_DUST_COLLECTOR_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.category.cmi.void_dust_collector");
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
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull VoidDustCollectorRecipe recipe, @NotNull IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 35)
				.addItemStack(VOID_DUST.get().getDefaultInstance());
		builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
				.addItemStack(ModBlocks.VOID_DUST_COLLECTOR.get().asItem().getDefaultInstance());
	}

	@Override
	public void draw(@NotNull VoidDustCollectorRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 50, 50);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 94, 20);
		this.voidDustCollectorMB.draw(graphics, 60, 10);
		PoseStack pose = graphics.pose();
		pose.popPose();
	}
}