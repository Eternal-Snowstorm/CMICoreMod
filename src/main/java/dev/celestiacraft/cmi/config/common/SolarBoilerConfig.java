package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SolarBoilerConfig extends ConfigModule {
	public SolarBoilerConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "solar_boilder", "Solar Boiler");
	}

	private static final String CONSUM_COMMENT = "Water consumption and steam production per tick";
	private static final String CAPACITY_COMMENT = "Boiler's Fluid Capacity";
	private static final String EFFICIENCT_TEXT_COMMENT = "_pre_tick_consum_and_production";
	private static final String CAPACITY_TEXT_COMMENT = "_boiler_capacity";

	public static ForgeConfigSpec.IntValue BRONZE_EFFICIENCY;
	public static ForgeConfigSpec.IntValue BRONZE_CAPACITY;

	public static ForgeConfigSpec.IntValue CAST_IRON_EFFICIENCY;
	public static ForgeConfigSpec.IntValue CAST_IRON_CAPACITY;

	public static ForgeConfigSpec.IntValue STEEL_EFFICIENCY;
	public static ForgeConfigSpec.IntValue STEEL_CAPACITY;

	/**
	 * 人造光照效率倍率: 当锅炉无法获得自然光照时, 依靠人造光照(方块光照)运行时的效率倍率
	 */
	public static ForgeConfigSpec.DoubleValue ARTIFICIAL_LIGHT_EFFICIENCY_MULTIPLIER;

	@Override
	protected void addConfigs() {
		BRONZE_EFFICIENCY = builder.comment(CONSUM_COMMENT)
				.comment("type: int")
				.comment("default: 2")
				.defineInRange("bronze" + EFFICIENCT_TEXT_COMMENT, 2, 1, 1024);

		BRONZE_CAPACITY = builder.comment(CAPACITY_COMMENT)
				.comment("type: int")
				.comment("default: 4000")
				.defineInRange("bronze" + CAPACITY_TEXT_COMMENT, 4000, 1, 100000000);

		CAST_IRON_EFFICIENCY = builder.comment(CONSUM_COMMENT)
				.comment("type: int")
				.comment("default: 4")
				.defineInRange("cast_iron" + EFFICIENCT_TEXT_COMMENT, 4, 1, 1024);

		CAST_IRON_CAPACITY = builder.comment(CAPACITY_COMMENT)
				.comment("type: int")
				.comment("default: 8000")
				.defineInRange("cast_iron" + CAPACITY_TEXT_COMMENT, 8000, 1, 100000000);

		STEEL_EFFICIENCY = builder.comment(CONSUM_COMMENT)
				.comment("type: int")
				.comment("default: 8")
				.defineInRange("steel" + EFFICIENCT_TEXT_COMMENT, 8, 1, 1024);

		STEEL_CAPACITY = builder.comment(CAPACITY_COMMENT)
				.comment("type: int")
				.comment("default: 12000")
				.defineInRange("steel" + CAPACITY_TEXT_COMMENT, 12000, 1, 100000000);

		ARTIFICIAL_LIGHT_EFFICIENCY_MULTIPLIER = builder.comment("Efficiency multiplier when the boiler runs on artificial light (block light)")
				.comment("1.0 = same as natural light, 0.5 = half efficiency. Set to 0.0 to disable artificial light mode.")
				.comment("type: double")
				.comment("default: 0.5")
				.defineInRange("artificial_light_efficiency_multiplier", 0.5, 0.0, 1.0);
	}
}