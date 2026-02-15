package top.nebula.cmi.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.recipe.void_dust_collector.VoidDustCollectorRecipe;
import top.nebula.cmi.common.register.CmiBlocks;
import top.nebula.cmi.compat.jei.api.CmiJeiRecipeType;
import top.nebula.cmi.compat.jei.category.multiblock.VoidDustCollectorMultiblock;
import top.nebula.cmi.api.CmiLang;
import top.nebula.libs.compat.jei.categoty.SimpleJeiCategory;

public class VoidDustCollectorCategory {
	public static final Lazy<Block> VOID_SPRING = Lazy.of(() -> {
		return ForgeRegistries.BLOCKS.getValue(Cmi.loadResource("void_spring"));
	});
	private static final Lazy<Item> VOID_DUST = Lazy.of(() -> {
		return ForgeRegistries.ITEMS.getValue(Cmi.loadResource("void_dust"));
	});
	private static final VoidDustCollectorMultiblock VOID_MB = new VoidDustCollectorMultiblock();

	public static SimpleJeiCategory<VoidDustCollectorRecipe> builder(IGuiHelper helper) {
		return SimpleJeiCategory.builder(CmiJeiRecipeType.VOID_DUST_COLLECTOR, helper)
				.setTitle(CmiLang.JeiLang.setTranCategoryTitle("void_dust_collector"))
				.setSize(178, 72)
				.setIcon(() -> {
					return new DoubleItemIcon(
							() -> VOID_SPRING.get().asItem().getDefaultInstance(),
							() -> CmiBlocks.VOID_DUST_COLLECTOR.get().asItem().getDefaultInstance()
					);
				})
				.setBackground(0, 0)
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 35)
							.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
							.addItemStack(VOID_DUST.get().getDefaultInstance());
					builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
							.addItemStack(VOID_SPRING.get().asItem().getDefaultInstance())
							.addItemStack(CmiBlocks.VOID_DUST_COLLECTOR.get().asItem().getDefaultInstance());
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