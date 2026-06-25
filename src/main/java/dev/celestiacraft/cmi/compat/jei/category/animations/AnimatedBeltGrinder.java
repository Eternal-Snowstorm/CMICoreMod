package dev.celestiacraft.cmi.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import dev.celestiacraft.cmi.client.block.CmiBlockPartialModel;
import dev.celestiacraft.cmi.common.register.block.MachineBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedBeltGrinder extends AnimatedKinetics {
	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack pose = graphics.pose();
		pose.pushPose();
		pose.translate(xOffset, yOffset, 0);
		pose.translate(2, 22, 200);
		pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
		pose.mulPose(Axis.YP.rotationDegrees(22.5f + 90.0f));

		int scale = 25;

		blockElement(shaft(Direction.Axis.X))
				.rotateBlock(-getCurrentAngle(), 0, 0)
				.scale(scale)
				.render(graphics);

		blockElement(MachineBlocks.BELT_GRINDER.getDefaultState()
				.setValue(HorizontalKineticBlock.HORIZONTAL_FACING, Direction.WEST))
				.rotateBlock(0, 0, 0)
				.scale(scale)
				.render(graphics);

		blockElement(CmiBlockPartialModel.GRINDER_BELT)
				.rotateBlock(0, -90, 0)
				.scale(scale)
				.render(graphics);

		pose.popPose();
	}
}