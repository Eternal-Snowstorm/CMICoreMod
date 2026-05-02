package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.block.CogWheelRegister;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelSet;

public class CmiCogwheel extends CogWheelRegister {
	public static final MetalCogWheelSet BRONZE;
	public static final MetalCogWheelSet CAST_IRON;
	public static final MetalCogWheelSet STEEL;

	static {
		BRONZE = register("bronze");
		CAST_IRON = register("cast_iron");
		STEEL = register("steel");
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core CogWheels Registered!");
	}
}