package dev.celestiacraft.cmi.common.block.general_astronautic_platform;

import org.jetbrains.annotations.Nullable;

public enum AstronauticPlatformDisplayState {
	NONE(null),
	BOUND_EMPTY("cmi"),
	BOUND_ROCKET("ground"),
	LAUNCH_FAILED("error"),
	TARGET_OCCUPIED("occupied");

	@Nullable
	private final String textureName;

	AstronauticPlatformDisplayState(@Nullable String textureName) {
		this.textureName = textureName;
	}

	// 屏幕贴图名, 为空表示不绘制图标
	@Nullable
	public String getTextureName() {
		return textureName;
	}

	// 按名称解析, 未知名称回落到不显示
	public static AstronauticPlatformDisplayState fromName(@Nullable String name) {
		if (name == null) {
			return NONE;
		}
		for (AstronauticPlatformDisplayState state : values()) {
			if (state.name().equals(name)) {
				return state;
			}
		}
		return NONE;
	}
}
