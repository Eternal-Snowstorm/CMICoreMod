package top.nebula.cmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.IntValue STEAM_HAMMER_STEAM_CONSUMPTION;
	public static final ForgeConfigSpec.IntValue STEAM_HAMMER_STEAM_CAPACITY;
	public static final ForgeConfigSpec.DoubleValue STEAM_HAMMER_STRESS_IMPACT;

	public static final ForgeConfigSpec.IntValue ACCELERATOR_MOTOR_DEFAULT_SPEED;
	public static final ForgeConfigSpec.IntValue ACCELERATOR_MOTOR_MAX_SPEED;

	public static final ForgeConfigSpec.IntValue FAST_SPOUT_FILLING_TIME;

	public static final ForgeConfigSpec.IntValue VOID_DUST_COLLECTOR_WORK_TIME;
	public static final ForgeConfigSpec.IntValue VOID_DUST_COLLECTOR_ENERGY_CONSUMPTION;
	public static final ForgeConfigSpec.IntValue VOID_DUST_COLLECTOR_ENERGY_CAPACITY;
	public static final ForgeConfigSpec.IntValue VOID_DUST_COLLECTOR_WORK_HEIGHT;
	public static final ForgeConfigSpec.IntValue VOID_DUST_COLLECTOR_MAX_RECEIVE;

	static {
		BUILDER.comment("All settings below will only take effect after restarting the server or client.")
				.push("general");

		BUILDER.comment("Steam Hammer settings")
				.push("steam_hammer");

		STEAM_HAMMER_STEAM_CONSUMPTION = BUILDER
				.comment("Steam consumption per run of steam hammer (mB)")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("steam_consumption", 1000, 0, 1000000);

		STEAM_HAMMER_STEAM_CAPACITY = BUILDER
				.comment("Steam capacity in steam hammer (mB)")
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("steam_capacity", 10000, 1000, 1000000);

		STEAM_HAMMER_STRESS_IMPACT = BUILDER
				.comment("Stress impact of steam hammer (su)")
				.comment("type: double")
				.comment("default: 16.0")
				.defineInRange("stress_impact", 16.0, 0.0, 64);

		BUILDER.pop();

		BUILDER.comment("Accelerator Motor settings")
				.push("accelerator_motor");

		ACCELERATOR_MOTOR_DEFAULT_SPEED = BUILDER
				.comment("Default speed when placing the Accelerator Motor (RPM)")
				.comment("type: int")
				.comment("default: 16")
				.defineInRange("default_speed", 16, 1, 256);

		ACCELERATOR_MOTOR_MAX_SPEED = BUILDER
				.comment("Maximum RPM the accelerator motor")
				.comment("type: int")
				.comment("default: 128")
				.defineInRange("max_speed", 128, 1, 256);

		BUILDER.pop();

		BUILDER.comment("Fast Spout settings")
				.push("fast_spout");

		FAST_SPOUT_FILLING_TIME = BUILDER
				.comment("Filling time in ticks")
				.comment("20 ticks = 1 second")
				.comment("Original spout uses 20 ticks")
				.comment("type: int")
				.comment("default: 5")
				.defineInRange("filling_time", 5, 1, 100);

		BUILDER.comment("Void Dust Collector settings")
				.push("void_dust_collector");

		BUILDER.pop();

		VOID_DUST_COLLECTOR_WORK_TIME = BUILDER
				.comment("Work time")
				.comment("20 ticks = 1 second")
				.comment("type: int")
				.comment("default: 20")
				.defineInRange("filling_time", 20, 1, 100000);

		VOID_DUST_COLLECTOR_ENERGY_CONSUMPTION = BUILDER
				.comment("Energy consumed per tick of void dust collector (FE)")
				.comment("type: int")
				.comment("default: 100")
				.defineInRange("energy_consumption", 100, 0, Integer.MAX_VALUE);

		VOID_DUST_COLLECTOR_ENERGY_CAPACITY = BUILDER
				.comment("Energy capacity of void dust collector (FE)")
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("energy_capacity", 50000, 0, Integer.MAX_VALUE);

		VOID_DUST_COLLECTOR_MAX_RECEIVE = BUILDER
				.comment("Max receive of void dust collector (FE)")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("max_receive", 1000, 0, Integer.MAX_VALUE);

		VOID_DUST_COLLECTOR_WORK_HEIGHT = BUILDER
				.comment("Work height of void dust collector")
				.comment("type: int")
				.comment("default: -63")
				// 调试用, 回头要改回-63
				.defineInRange("work_height", 65, -64, 319);

		BUILDER.pop();

		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();

	private static boolean validateString(Object object) {
		return object instanceof String;
	}
}