package dev.celestiacraft.cmi.mixin.create;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import dev.celestiacraft.cmi.common.grill.CmiGrillSources;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnimatedBlazeBurner.class, remap = false)
public abstract class AnimatedBlazeBurnerMixin extends AnimatedKinetics {
	@Shadow
	private BlazeBurnerBlock.HeatLevel heatLevel;

	@Inject(method = "draw", at = @At("HEAD"), remap = false, cancellable = true)
	public void draw(GuiGraphics graphics, int xOffset, int yOffset, CallbackInfo info) {
		if (heatLevel == BlazeBurnerBlock.HeatLevel.valueOf("GRILLED")) {
			PoseStack stack = graphics.pose();

			stack.pushPose();
			stack.translate(xOffset, yOffset, 200.0F);
			stack.mulPose(Axis.XP.rotationDegrees(-15.5F));
			stack.mulPose(Axis.YP.rotationDegrees(22.5F));

			int scale = 23;

			// 展示方块取自炙烤热源注册表
			Block block = CmiGrillSources.displayBlock().orElse(Blocks.FIRE);

			blockElement(block.defaultBlockState())
					.atLocal(0.0F, 1.65, 0.0F)
					.scale(scale)
					.render(graphics);

			stack.popPose();
			info.cancel();
		}
	}
}