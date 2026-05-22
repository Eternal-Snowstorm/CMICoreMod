package dev.celestiacraft.cmi.config.main;

import dev.celestiacraft.cmi.config.common.*;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final SneakyLinkConfig SNEAKY_LINK;
	public static final SteamHammerConfig STEAM_HAMMER;
	public static final AcceleratorMotorConfig ACCELERATOR_MOTOR;
	public static final AdvancedSpoutConfig ADVANCED_SPOUT;
	public static final VoidDustCollectorConfig VOID_DUST_COLLECTOR;
	public static final SolarBoilerConfig SOLAR_BOILER;
	public static final StackStorageConfig STACK_STORAGE;

	static {
		BUILDER.comment("All settings below will only take effect after restarting the server or client.")
				.push("general");

		SNEAKY_LINK = new SneakyLinkConfig(BUILDER);
		STEAM_HAMMER = new SteamHammerConfig(BUILDER);
		ACCELERATOR_MOTOR = new AcceleratorMotorConfig(BUILDER);
		ADVANCED_SPOUT = new AdvancedSpoutConfig(BUILDER);
		VOID_DUST_COLLECTOR = new VoidDustCollectorConfig(BUILDER);
		SOLAR_BOILER = new SolarBoilerConfig(BUILDER);
		STACK_STORAGE = new StackStorageConfig(BUILDER);

		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}