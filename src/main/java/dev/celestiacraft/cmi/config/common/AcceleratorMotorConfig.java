package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class AcceleratorMotorConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue DEFAULT_SPEED;
	public static ForgeConfigSpec.IntValue MAX_SPEED;

	public AcceleratorMotorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "accelerator_motor", "Accelerator Motor");
	}

	@Override
	protected void addConfigs() {
		DEFAULT_SPEED = builder
				.comment("Default speed when placing the Accelerator Motor (RPM)")
				.comment("type: int")
				.comment("default: 16")
				.defineInRange("default_speed", 16, 1, 256);

		MAX_SPEED = builder
				.comment("Maximum RPM the accelerator motor")
				.comment("type: int")
				.comment("default: 128")
				.defineInRange("max_speed", 128, 1, 256);
	}
}