package top.nebula.cmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.IntValue STEAM_HAMMER_STEAM_CONSUMPTION;
	public static final ForgeConfigSpec.IntValue STEAM_HAMMER_STEAM_CAPACITY;
	public static final ForgeConfigSpec.DoubleValue STEAM_HAMMER_STRESS_IMPACT;

	public static final ForgeConfigSpec.IntValue ACCELERATOR_MOTOR_DEFAULT_SPEED;
	public static final ForgeConfigSpec.DoubleValue ACCELERATOR_MOTOR_STRESS_CAPACITY;
	public static final ForgeConfigSpec.IntValue ACCELERATOR_MOTOR_MAX_SPEED;

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

		STEAM_HAMMER_STRESS_IMPACT = BUILDER
				.comment("Stress impact of steam hammer (su)")
				.comment("type: double")
				.comment("default: 16.0")
				.defineInRange("stress_impact", 16.0, 0.0, 1024.0);

		BUILDER.pop();

		BUILDER.comment("Accelerator Motor settings")
				.push("accelerator_motor");

		ACCELERATOR_MOTOR_DEFAULT_SPEED = BUILDER
				.comment("Default speed when placing the Accelerator Motor (RPM)")
				.comment("type: int")
				.comment("default: 16")
				.defineInRange("default_speed", 16, 1, 256);

		ACCELERATOR_MOTOR_STRESS_CAPACITY = BUILDER
				.comment("Stress capacity of accelerator motor (su)")
				.comment("type: double")
				.comment("default: 0.0")
				.defineInRange("stress_capacity", 0.0, 0.0, 65536.0);

		ACCELERATOR_MOTOR_MAX_SPEED = BUILDER
				.comment("Maximum RPM the accelerator motor")
				.comment("type: int")
				.comment("default: 128")
				.defineInRange("max_speed", 128, 1, 256);

		BUILDER.pop();

		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();

	private static boolean validateString(Object object) {
		return object instanceof String;
	}
}