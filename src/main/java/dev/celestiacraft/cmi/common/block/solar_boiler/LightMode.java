package dev.celestiacraft.cmi.common.block.solar_boiler;

public enum LightMode {
	/**
	 * 晴天的自然光照 (100% 效率)
	 */
	SUNLIGHT,
	/**
	 * 雨天的自然光照, 效率由 {@code rain_efficiency_multiplier} 决定 (默认 50%)
	 */
	RAIN,
	/**
	 * 雷暴天的自然光照, 效率由 {@code thunder_efficiency_multiplier} 决定 (默认 25%)
	 */
	THUNDER,
	/**
	 * 人造光照, 效率由 {@code artificial_light_efficiency_multiplier} 决定 (默认 50%)
	 */
	ARTIFICIAL,
	/**
	 * 没有可用光源, 锅炉停止工作
	 */
	NONE
}