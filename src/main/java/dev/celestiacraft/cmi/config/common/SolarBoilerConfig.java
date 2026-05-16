package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SolarBoilerConfig extends ConfigModule {
	public SolarBoilerConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "solar_boilder", "Solar Boiler setting");
	}

	private final String consumComment = "Water consumption and steam production per tick";
	private final String capacityComment = "Boiler's Fluid Capacity";
	private final String efficienctTextComment = "_pre_tick_consum_and_production";
	private final String capacityTextComment = "_boiler_capacity";

	public static ForgeConfigSpec.IntValue BRONZE_EFFICIENCY;
	public static ForgeConfigSpec.IntValue BRONZE_CAPACITY;

	public static ForgeConfigSpec.IntValue CAST_IRON_EFFICIENCY;
	public static ForgeConfigSpec.IntValue CAST_IRON_CAPACITY;

	public static ForgeConfigSpec.IntValue STEEL_EFFICIENCY;
	public static ForgeConfigSpec.IntValue STEEL_CAPACITY;

	@Override
	protected void register() {
		BRONZE_EFFICIENCY = builder
				.comment(consumComment)
				.comment("type: int")
				.comment("default: 2")
				.defineInRange("bronze" + efficienctTextComment, 2, 1, 1024);

		BRONZE_CAPACITY = builder
				.comment(capacityComment)
				.comment("type: int")
				.comment("default: 4000")
				.defineInRange("bronze" + capacityTextComment, 4000, 1, 100000000);

		CAST_IRON_EFFICIENCY = builder
				.comment(consumComment)
				.comment("type: int")
				.comment("default: 4")
				.defineInRange("cast_iron" + efficienctTextComment, 4, 1, 1024);

		CAST_IRON_CAPACITY = builder
				.comment(capacityComment)
				.comment("type: int")
				.comment("default: 8000")
				.defineInRange("cast_iron" + capacityTextComment, 8000, 1, 100000000);

		STEEL_EFFICIENCY = builder
				.comment(consumComment)
				.comment("type: int")
				.comment("default: 8")
				.defineInRange("steel" + efficienctTextComment, 8, 1, 1024);

		STEEL_CAPACITY = builder
				.comment(capacityComment)
				.comment("type: int")
				.comment("default: 12000")
				.defineInRange("steel" + capacityTextComment, 12000, 1, 100000000);
	}
}