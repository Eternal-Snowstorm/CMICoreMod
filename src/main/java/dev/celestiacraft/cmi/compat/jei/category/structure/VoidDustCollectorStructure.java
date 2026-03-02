package dev.celestiacraft.cmi.compat.jei.category.structure;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlock;
import dev.celestiacraft.cmi.common.register.CmiBlock;

public class VoidDustCollectorStructure extends AnimatedKinetics {
	private static final Lazy<Block> VOID_SPRING = Lazy.of(() -> {
		return ForgeRegistries.BLOCKS.getValue(Cmi.loadResource("void_spring"));
	});

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
		defaultBlockElement(CmiBlock.VOID_DUST_COLLECTOR.get().defaultBlockState()
				.setValue(VoidDustCollectorBlock.WORKING, true)
				.setValue(VoidDustCollectorBlock.FACING, Direction.SOUTH))
				.atLocal(0.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(VOID_SPRING.get().defaultBlockState())
				.atLocal(0.0F, 2.0F, 0.0F)
				.scale(scale)
				.render(graphics);
	}
}