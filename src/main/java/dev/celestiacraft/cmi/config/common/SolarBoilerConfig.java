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

	/**
	 * 默认效率 (mB / Tick) 取"同级流体燃烧室的 150%":
	 * <p>
	 * 蒸汽锅炉每 Tick 可处理的 HU 上限为 青铜 125 / 铸铁 250 / 钢 500,
	 * <p>
	 * 按 10 HU = 1 mB 蒸汽 换算即 12.5 / 25 / 50 mB/Tick, 再乘 1.5 得:
	 * <p>
	 * 青铜 125 * 1.5 / 10 = 18.75 ≈ 19
	 * <p>
	 * 铸铁 250 * 1.5 / 10 = 37.5 ≈ 38
	 * <p>
	 * 钢 500 * 1.5 / 10 = 75
	 * <p>
	 * 该数值以熔岩 (fluid_burn 配方 hu = 134.6) 为参考燃料标定
	 */
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

	/**
	 * 雨天效率倍率: 白天获得自然光照, 但当前位置正在下雨时的效率倍率
	 */
	public static ForgeConfigSpec.DoubleValue RAIN_EFFICIENCY_MULTIPLIER;

	/**
	 * 雷暴效率倍率: 白天获得自然光照, 但当前处于雷暴天气时的效率倍率
	 */
	public static ForgeConfigSpec.DoubleValue THUNDER_EFFICIENCY_MULTIPLIER;

	@Override
	protected void addConfigs() {
		BRONZE_EFFICIENCY = builder.comment(CONSUM_COMMENT)
				.comment("type: int")
				.comment("default: 19")
				.defineInRange("bronze" + EFFICIENCT_TEXT_COMMENT, 19, 1, 1024);

		BRONZE_CAPACITY = builder.comment(CAPACITY_COMMENT)
				.comment("type: int")
				.comment("default: 4000")
				.defineInRange("bronze" + CAPACITY_TEXT_COMMENT, 4000, 1, 100000000);

		CAST_IRON_EFFICIENCY = builder.comment(CONSUM_COMMENT)
				.comment("type: int")
				.comment("default: 38")
				.defineInRange("cast_iron" + EFFICIENCT_TEXT_COMMENT, 38, 1, 1024);

		CAST_IRON_CAPACITY = builder.comment(CAPACITY_COMMENT)
				.comment("type: int")
				.comment("default: 8000")
				.defineInRange("cast_iron" + CAPACITY_TEXT_COMMENT, 8000, 1, 100000000);

		STEEL_EFFICIENCY = builder.comment(CONSUM_COMMENT)
				.comment("type: int")
				.comment("default: 75")
				.defineInRange("steel" + EFFICIENCT_TEXT_COMMENT, 75, 1, 1024);

		STEEL_CAPACITY = builder.comment(CAPACITY_COMMENT)
				.comment("type: int")
				.comment("default: 12000")
				.defineInRange("steel" + CAPACITY_TEXT_COMMENT, 12000, 1, 100000000);

		ARTIFICIAL_LIGHT_EFFICIENCY_MULTIPLIER = builder.comment("Efficiency multiplier when the boiler runs on artificial light (block light)")
				.comment("1.0 = same as natural light, 0.5 = half efficiency. Set to 0.0 to disable artificial light mode.")
				.comment("type: double")
				.comment("default: 0.5")
				.defineInRange("artificial_light_efficiency_multiplier", 0.5, 0.0, 1.0);

		RAIN_EFFICIENCY_MULTIPLIER = builder.comment("Efficiency multiplier when the boiler runs under natural light while it is raining")
				.comment("1.0 = same as clear weather, 0.5 = half efficiency. Set to 0.0 to disable running in rain.")
				.comment("type: double")
				.comment("default: 0.5")
				.defineInRange("rain_efficiency_multiplier", 0.5, 0.0, 1.0);

		THUNDER_EFFICIENCY_MULTIPLIER = builder.comment("Efficiency multiplier when the boiler runs under natural light during a thunderstorm")
				.comment("1.0 = same as clear weather, 0.25 = quarter efficiency. Set to 0.0 to disable running in thunderstorms.")
				.comment("type: double")
				.comment("default: 0.25")
				.defineInRange("thunder_efficiency_multiplier", 0.25, 0.0, 1.0);
	}
}