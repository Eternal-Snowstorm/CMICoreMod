package dev.celestiacraft.cmi.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	@Inject(
			method = "onPlace",
			at = @At("RETURN")
	)
	private void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving, CallbackInfo info) {
		BlockEntity blockEntity = level.getBlockEntity(pos.above());
		if (blockEntity instanceof BasinBlockEntity basinBlockEntity) {
			basinBlockEntity.notifyChangeOfContents();
		}
	}
}