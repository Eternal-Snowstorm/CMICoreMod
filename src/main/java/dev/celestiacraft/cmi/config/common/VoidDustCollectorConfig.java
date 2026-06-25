package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class VoidDustCollectorConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue WORK_TIME;
	public static ForgeConfigSpec.IntValue ENERGY_CONSUMPTION;
	public static ForgeConfigSpec.IntValue ENERGY_CAPACITY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue MAX_WORK_HEIGHT;
	public static ForgeConfigSpec.IntValue MIN_WORK_HEIGHT;

	public VoidDustCollectorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "void_dust_collector", "Void Dust Collector");
	}

	@Override
	protected void addConfigs() {
		WORK_TIME = builder
				.comment("Work time")
				.comment("20 ticks = 1 second")
				.comment("type: int")
				.comment("default: 20")
				.defineInRange("work_time", 20, 1, 100000);

		ENERGY_CONSUMPTION = builder
				.comment("Energy consumed per tick of void dust collector (FE)")
				.comment("type: int")
				.comment("default: 100")
				.defineInRange("energy_consumption", 100, 0, Integer.MAX_VALUE);

		ENERGY_CAPACITY = builder
				.comment("Energy capacity of void dust collector (FE)")
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("energy_capacity", 50000, 0, Integer.MAX_VALUE);

		MAX_RECEIVE = builder
				.comment("Max receive of void dust collector (FE)")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("max_receive", 1000, 0, Integer.MAX_VALUE);

		MAX_WORK_HEIGHT = builder
				.comment("Maximum work height of void dust collector")
				.comment("type: int")
				.comment("default: -60")
				.defineInRange("max_work_height", -60, -63, 319);

		MIN_WORK_HEIGHT = builder
				.comment("Minimum work height of void dust collector")
				.comment("Collector doesnt work if greater than max height")
				.comment("type: int")
				.comment("default: -63")
				.defineInRange("min_work_height", -63, -63, 319);
	}
}