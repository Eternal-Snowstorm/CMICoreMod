package dev.celestiacraft.cmi.common.entity.coin_projectile;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 硬币弹射物的渲染模式
 *
 * <p>
 * 只保留弹射物飞行过程中的三种基础姿态:
 * </p>
 *
 * <ul>
 *     <li>{@link #BILLBOARD} —— 始终正对摄像机(视觉上是平铺的硬币)</li>
 *     <li>{@link #TUMBLE} —— 面向摄像机的基础上叠加自旋, 看上去在翻滚</li>
 *     <li>{@link #TOWARD_MOTION} —— 硬币平面朝向飞行方向, 并绕飞行轴自旋</li>
 * </ul>
 *
 * <p>
 * 数据包字段 {@code render_mode} 使用这里的 {@link #getSerializedName()} 值
 * </p>
 */
public enum CoinProjectileRenderMode implements StringRepresentable {
	BILLBOARD("billboard"),
	TUMBLE("tumble"),
	TOWARD_MOTION("toward_motion");

	public static final CoinProjectileRenderMode DEFAULT = TOWARD_MOTION;

	private static final Map<String, CoinProjectileRenderMode> BY_NAME = new HashMap<>();

	static {
		for (CoinProjectileRenderMode mode : values()) {
			BY_NAME.put(mode.name, mode);
		}
	}

	private final String name;

	CoinProjectileRenderMode(String name) {
		this.name = name;
	}

	/**
	 * 按数据包中的名称查找渲染模式, 无法识别时回退到 {@link #DEFAULT}
	 */
	public static CoinProjectileRenderMode byName(String name) {
		if (name == null) {
			return DEFAULT;
		}

		return BY_NAME.getOrDefault(name.toLowerCase(Locale.ROOT), DEFAULT);
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}
}
