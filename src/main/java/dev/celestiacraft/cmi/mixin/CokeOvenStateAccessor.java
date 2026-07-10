package dev.celestiacraft.cmi.mixin;

import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CokeOvenLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CokeOvenLogic.State.class, remap = false)
public interface CokeOvenStateAccessor {
	@Accessor("processMax")
	int cmi$getProcessMax();
}
