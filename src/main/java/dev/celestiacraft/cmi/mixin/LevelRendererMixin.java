package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.common.block.wind_vane.WindVaneManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Inject(
			method = "renderSnowAndRain",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cmi$renderSnowAndRain(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null && WindVaneManager.isSealed(level, BlockPos.containing(camX, camY, camZ))) {
			ci.cancel();
		}
	}

	@Inject(
			method = "tickRain",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cmi$tickRain(Camera camera, CallbackInfo ci) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null && WindVaneManager.isSealed(level, BlockPos.containing(camera.getPosition()))) {
			ci.cancel();
		}
	}
}
