package dev.celestiacraft.cmi.mixin;

import com.teammoeg.immersiveindustry.content.chemical_reactor.ChemicalLogic;
import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChemicalLogic.class, remap = false)
public interface ChemicalLogicAccessor {

	@Accessor("out")
	static CapabilityFacing[] cmi$getOut() {
		throw new AssertionError();
	}
}
