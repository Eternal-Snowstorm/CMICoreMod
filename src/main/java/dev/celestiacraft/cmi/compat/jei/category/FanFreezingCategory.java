package dev.celestiacraft.cmi.compat.jei.category;

import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.celestiacraft.cmi.common.recipe.fan_processig.freezing.FreezingRecipe;
import dev.celestiacraft.cmi.tags.ModBlockTags;
import dev.celestiacraft.libs.client.TagAnimatedBlock;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class FanFreezingCategory extends ProcessingViaFanCategory.MultiOutput<FreezingRecipe> {
	public FanFreezingCategory(Info<FreezingRecipe> info) {
		super(info);
	}

	@Override
	protected AllGuiTextures getBlockShadow() {
		return AllGuiTextures.JEI_LIGHT;
	}

	@Override
	protected void renderAttachedBlock(@NotNull GuiGraphics graphics) {
		Block block = TagAnimatedBlock.get(ModBlockTags.FREEZING_CATALYST, 20);

		GuiGameElement.of(block.defaultBlockState())
				.scale(SCALE)
				.atLocal(0, 0, 2)
				.lighting(AnimatedKinetics.DEFAULT_LIGHTING)
				.render(graphics);
	}
}