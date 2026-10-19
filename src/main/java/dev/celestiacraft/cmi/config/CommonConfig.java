package dev.celestiacraft.cmi.config;

import dev.celestiacraft.cmi.config.common.*;
import dev.celestiacraft.cmi.config.common.mbd2.ReinforcedCokeOvenConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final AcceleratorMotorConfig ACCELERATOR_MOTOR;
	public static final VoidDustCollectorConfig VOID_DUST_COLLECTOR;
	public static final SpaceElevatorConfig SPACE_ELEVATOR;
	public static final GeothermalGeneratorConfig GEOTHERMAL_GENERATOR;
	public static final MetalDetectorConfig METAL_DETECTOR;

	public static final ReinforcedCokeOvenConfig REINFORCED_COKE;

	static {
		BUILDER.comment("All settings below will only take effect after restarting the server or client.")
				.push("general");

		ACCELERATOR_MOTOR = new AcceleratorMotorConfig(BUILDER);
		VOID_DUST_COLLECTOR = new VoidDustCollectorConfig(BUILDER);
		SPACE_ELEVATOR = new SpaceElevatorConfig(BUILDER);
		GEOTHERMAL_GENERATOR = new GeothermalGeneratorConfig(BUILDER);
		METAL_DETECTOR = new MetalDetectorConfig(BUILDER);

		REINFORCED_COKE = new ReinforcedCokeOvenConfig(BUILDER);

		SPEC = BUILDER.build();

		BUILDER.pop();
	}
}