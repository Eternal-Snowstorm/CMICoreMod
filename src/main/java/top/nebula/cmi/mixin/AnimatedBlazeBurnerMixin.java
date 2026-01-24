package top.nebula.cmi.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.nebula.cmi.common.tag.ModBlockTags;
import top.nebula.libs.client.TagAnimatedBlock;

@Mixin(value = AnimatedBlazeBurner.class, remap = false)
public abstract class AnimatedBlazeBurnerMixin extends AnimatedKinetics {
	@Shadow
	private BlazeBurnerBlock.HeatLevel heatLevel;

	@Inject(method = "draw", at = @At("HEAD"), remap = false, cancellable = true)
	public void draw(GuiGraphics graphics, int xOffset, int yOffset, CallbackInfo info) {
		if (heatLevel == BlazeBurnerBlock.HeatLevel.valueOf("GRILLED")) {
			PoseStack matrixStack = graphics.pose();
			matrixStack.pushPose();
			matrixStack.translate((float) xOffset, (float) yOffset, 200.0F);
			matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
			matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));
			int scale = 23;

			Block block = TagAnimatedBlock.get(ModBlockTags.GRILL_SOURCES, 20f);
			blockElement(block.defaultBlockState())
					.atLocal(0.0F, 1.65, 0.0F)
					.scale(scale)
					.render(graphics);

			matrixStack.popPose();
			info.cancel();
		}
	}
}