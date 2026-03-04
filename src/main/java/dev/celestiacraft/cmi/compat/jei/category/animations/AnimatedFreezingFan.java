package dev.celestiacraft.cmi.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AnimatedFreezingFan extends AnimatedKinetics {
	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack pose = graphics.pose();
		pose.pushPose();
		pose.translate(xOffset, yOffset, 0);
		pose.translate(2, 22, 200);
		pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
		pose.mulPose(Axis.YP.rotationDegrees(22.5f + 90f));

		int scale = 25;

		blockElement(Blocks.POWDER_SNOW.defaultBlockState())
				.rotateBlock(0, 0, 0)
				.scale(scale)
				.render(graphics);

		blockElement(AllBlocks.ENCASED_FAN.get().defaultBlockState()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST))
				.atLocal(0, 0, 2)
				.rotateBlock(0, 0, 0)
				.scale(scale)
				.render(graphics);

		pose.popPose();
	}
}
