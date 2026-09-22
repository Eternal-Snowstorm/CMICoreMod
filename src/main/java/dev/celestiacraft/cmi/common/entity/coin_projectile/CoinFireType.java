package dev.celestiacraft.cmi.common.entity.coin_projectile;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * coin 弹射物的射击类型
 *
 * <p>
 * 射击类型同时决定弹射物的物理行为, 因此数据包里不再单独提供 knockback / pickup / gravity 字段:
 * </p>
 *
 * <ul>
 *     <li>{@link #KNOCKBACK} —— 受重力; 命中生物按 {@code knockback_strength} 击退; 落地可拾回</li>
 *     <li>{@link #PIERCE} —— 无视重力; 按 {@code pierce_level} 贯穿若干实体; 落地可拾回</li>
 *     <li>{@link #SCATTER} —— 受重力; 一次射出 {@code bullet_count} 发(相邻两发相差 10°);
 *     一次只消耗一枚硬币; 无法拾取且触地即消失</li>
 * </ul>
 *
 * <p>
 * 数据包字段 {@code fire_type} 使用这里的 {@link #getSerializedName()} 值
 * </p>
 */
public enum CoinFireType implements StringRepresentable {
	KNOCKBACK("knockback"),
	PIERCE("pierce"),
	SCATTER("scatter");

	public static final CoinFireType DEFAULT = KNOCKBACK;

	private static final Map<String, CoinFireType> BY_NAME = new HashMap<>();

	static {
		for (CoinFireType type : values()) {
			BY_NAME.put(type.name, type);
		}
	}

	private final String name;

	CoinFireType(String name) {
		this.name = name;
	}

	/**
	 * 按数据包中的名称查找射击类型, 无法识别时回退到 {@link #DEFAULT}
	 */
	public static CoinFireType byName(String name) {
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
