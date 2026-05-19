package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.common.block.wind_vane.WindVaneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {
	@Inject(
			method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cmi$shouldFreeze(LevelReader reader, BlockPos pos, boolean mustBeAtEdge, CallbackInfoReturnable<Boolean> cir) {
		if (reader instanceof Level level && WindVaneManager.isSealed(level, pos)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
			method = "shouldSnow",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cmi$shouldSnow(LevelReader reader, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (reader instanceof Level level && WindVaneManager.isSealed(level, pos)) {
			cir.setReturnValue(false);
		}
	}
}