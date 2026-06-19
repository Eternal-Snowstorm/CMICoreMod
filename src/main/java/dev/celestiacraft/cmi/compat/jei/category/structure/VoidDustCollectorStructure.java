package dev.celestiacraft.cmi.compat.jei.category.structure;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlock;
import dev.celestiacraft.cmi.common.register.block.MachineBlocks;
import dev.celestiacraft.cmi.utils.ModResources;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class VoidDustCollectorStructure extends AnimatedKinetics {
	/**
	 * @param graphics
	 * @param offsetX
	 * @param offsetY
	 */
	@Override
	public void draw(@NotNull GuiGraphics graphics, int offsetX, int offsetY) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(offsetX, offsetY, 100.0F);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));

		int scale = 23;
		defaultBlockElement(MachineBlocks.VOID_DUST_COLLECTOR.get().defaultBlockState()
				.setValue(BasicBlock.LIT, true)
				.setValue(VoidDustCollectorBlock.HORIZONTAL_FACING, Direction.SOUTH))
				.atLocal(0.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(ModResources.VOID_SPRING.defaultBlockState())
				.atLocal(0.0F, 2.0F, 0.0F)
				.scale(scale)
				.render(graphics);
	}
}