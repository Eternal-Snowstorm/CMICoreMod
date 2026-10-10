package dev.celestiacraft.cmi.mixin.adastra;

import earth.terrarium.adastra.common.entities.vehicles.Rover;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Rover.class)
public class RoverMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(longValue = 3000L))
	private long cmi$modifyFuelCapacity(long amount) {
		return 10000;
	}
}