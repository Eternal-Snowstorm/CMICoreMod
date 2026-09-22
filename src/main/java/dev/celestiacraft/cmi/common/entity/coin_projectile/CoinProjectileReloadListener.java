package dev.celestiacraft.cmi.common.entity.coin_projectile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * coin 弹射物数据包加载器
 *
 * <p>
 * 扫描 {@code data/<namespace>/cmi/coin_projectile/*.json}, 解析结果交给
 * {@link CoinProjectileTypes} 保管
 * </p>
 *
 * <p>
 * 通过 {@code AddReloadListenerEvent} 注册, 因此只在服务端(含单人存档)生效 ——
 * 弹射物的伤害与速度都在服务端结算, 客户端渲染只需要同步过去的物品与姿态数据
 * </p>
 */
public class CoinProjectileReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setLenient().create();

	public CoinProjectileReloadListener() {
		super(GSON, CoinProjectileTypes.DIRECTORY);
	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		CoinProjectileTypes.apply(objects);
	}
}