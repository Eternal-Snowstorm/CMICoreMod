package dev.celestiacraft.cmi.mixin;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NonMirrorableWithActiveBlock.class, remap = false)
public abstract class NonMirrorableWithActiveBlockMixin extends Block {
	private NonMirrorableWithActiveBlockMixin() {
		super(Properties.of());
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void cmi$fixActiveDefault(BlockBehaviour.Properties properties, MultiblockRegistration<?> multiblock, CallbackInfo ci) {
		registerDefaultState(defaultBlockState().setValue(IEProperties.ACTIVE, false));
	}
}
