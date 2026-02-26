package dev.celestiacraft.cmi.mixin;

import com.teammoeg.immersiveindustry.content.chemical_reactor.ChemicalState;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChemicalState.class, remap = false)
public interface ChemicalStateAccessor {

	@Accessor("outTank")
	FluidTank[] cmi$getOutTank();

	@Accessor("capabilities")
	CapabilityProcessor cmi$getCapabilities();
}
