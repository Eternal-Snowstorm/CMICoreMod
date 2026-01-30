package top.nebula.cmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.IntValue STEAM_HAMMER_STEAM_CONSUMPTION;
	public static final ForgeConfigSpec.IntValue STEAM_HAMMER_STEAM_CAPACITY;

	public static final ForgeConfigSpec.IntValue ACCELERATOR_MOTOR_DEFAULT_SPEED;

	static {
		BUILDER.comment("All settings below will only take effect after restarting the server or client.")
				.push("general");

		BUILDER.comment("Steam Hammer settings")
				.push("steam_hammer");

		STEAM_HAMMER_STEAM_CONSUMPTION = BUILDER
				.comment("Steam consumption per run of steam hammer (mB)")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("steam_consumption", 1000, 0, 10000);

		STEAM_HAMMER_STEAM_CAPACITY = BUILDER
				.comment("Steam capacity in steam hammer (mB)")
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("steam_capacity", 10000, 1000, 32000);
		BUILDER.pop();

		BUILDER.comment("Accelerator Motor settings")
				.push("accelerator_motor");

		ACCELERATOR_MOTOR_DEFAULT_SPEED = BUILDER
				.comment("Default speed of the Accelerator Motor")
				.comment("type: int")
				.comment("default: 16")
				.defineInRange("default_speed", 16, 1, 256);
		BUILDER.pop();

		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();

	private static boolean validateString(Object object) {
		return object instanceof String;
	}
}