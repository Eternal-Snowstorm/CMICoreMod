package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelInfo;

public class CmiCogwheel extends MetalCogWheelRegister {
	public static final MetalCogWheelInfo BRONZE;
	public static final MetalCogWheelInfo CAST_IRON;
	public static final MetalCogWheelInfo STEEL;

	static {
		BRONZE = register("bronze");
		CAST_IRON = register("cast_iron");
		STEEL = register("steel");
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core CogWheels Registered!");
	}
}