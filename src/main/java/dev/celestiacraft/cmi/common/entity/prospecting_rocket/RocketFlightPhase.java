package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

public enum RocketFlightPhase {
	IDLE,
	COUNTDOWN,
	ASCENDING,
	DESCENDING;

	// 序号越界的存档或同步值视为待机
	public static RocketFlightPhase byOrdinal(int ordinal) {
		RocketFlightPhase[] values = values();
		if (ordinal < 0 || ordinal >= values.length) {
			return IDLE;
		}
		return values[ordinal];
	}
}