package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.tags.CmiBlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {
	@Redirect(
			method = "isSmokeSource",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
			)
	)
	private boolean cmi$isSmokeSource(BlockState state, Block block) {
		return state.is(CmiBlockTags.SMOKE_SOURCE);
	}
}