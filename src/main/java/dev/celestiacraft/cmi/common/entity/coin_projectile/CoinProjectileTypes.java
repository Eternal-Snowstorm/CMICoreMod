package dev.celestiacraft.cmi.common.entity.coin_projectile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * coin 弹射物类型注册表
 *
 * <p>
 * 数据包目录: {@code data/<namespace>/cmi/coin_projectile/<name>.json}
 * </p>
 *
 * <p>
 * 匹配规则: 按类型 id 字典序遍历, 返回第一个命中物品的类型; 全都不命中时使用内置的
 * {@link CoinProjectileType#fallback()} (普通硬币物品也能打出去, 只是使用默认参数)
 * </p>
 */
public class CoinProjectileTypes {
	public static final String DIRECTORY = "cmi/coin_projectile";

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final CoinProjectileType FALLBACK = CoinProjectileType.fallback();

	private static volatile List<CoinProjectileType> types = List.of();

	/**
	 * 在 mod 事件总线上注册数据包监听器
	 */
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new CoinProjectileReloadListener());
	}

	public static @NotNull CoinProjectileType fallback() {
		return FALLBACK;
	}

	/**
	 * 查找匹配该物品的弹射物类型
	 */
	public static @NotNull Optional<CoinProjectileType> findFor(ItemStack stack) {
		if (stack.isEmpty()) {
			return Optional.empty();
		}

		for (CoinProjectileType type : types) {
			if (type.matches(stack)) {
				return Optional.of(type);
			}
		}

		return Optional.empty();
	}

	/**
	 * 查找匹配该物品的弹射物类型, 找不到就用内置默认值
	 */
	public static @NotNull CoinProjectileType getOrFallback(ItemStack stack) {
		return findFor(stack).orElse(FALLBACK);
	}

	public static @NotNull List<CoinProjectileType> all() {
		return types;
	}

	/**
	 * 数据包重载时重建注册表
	 */
	public static void apply(Map<ResourceLocation, JsonElement> objects) {
		List<CoinProjectileType> parsed = new ArrayList<>();

		for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
			ResourceLocation id = entry.getKey();

			try {
				JsonElement element = entry.getValue();

				if (!element.isJsonObject()) {
					LOGGER.error("coin_projectile {} 不是合法的 JSON 对象, 已跳过", id);
					continue;
				}

				JsonObject json = element.getAsJsonObject();
				parsed.add(CoinProjectileType.fromJson(id, json));
			} catch (Exception exception) {
				LOGGER.error("解析 coin_projectile {} 失败, 已跳过", id, exception);
			}
		}

		parsed.sort(Comparator.comparing((type) -> {
			return type.getId().toString();
		}));

		types = List.copyOf(parsed);

		if (!parsed.isEmpty()) {
			Cmi.LOGGER.info("Loaded {} coin projectile type(s)", parsed.size());
		}
	}
}
