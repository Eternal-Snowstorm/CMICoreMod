package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

// 货运火箭发射结果, 失败原因用于提示玩家
public enum RocketLaunchResult {
	SUCCESS(""),
	BUSY(""),
	NOT_SERVER(""),
	NO_BINDING("prospecting_rocket.no_binding"),
	NO_DIMENSION("prospecting_rocket.no_dimension"),
	NO_PLATFORM("prospecting_rocket.no_platform"),
	OCCUPIED("prospecting_rocket.occupied"),
	NO_FUEL("prospecting_rocket.no_fuel"),
	WRONG_FUEL("prospecting_rocket.wrong_fuel"),
	TOO_LOW_TIER("prospecting_rocket.too_low_tier"),
	SPAWN_FAILED("prospecting_rocket.spawn_failed");

	private final String messageKey;

	RocketLaunchResult(String messageKey) {
		this.messageKey = messageKey;
	}

	// 提示语言键, 空串表示失败无需提示
	public String messageKey() {
		return messageKey;
	}

	public boolean shouldNotify() {
		return !messageKey.isEmpty();
	}
}