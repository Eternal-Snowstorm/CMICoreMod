package dev.celestiacraft.cmi.mixin;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.mixer.MixerLogic;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MixerLogic.State.class, remap = false)
public interface MixerStateAccessor {

	@Accessor("outputRef")
	CapabilityReference<IFluidHandler> cmi$getOutputRef();
}
