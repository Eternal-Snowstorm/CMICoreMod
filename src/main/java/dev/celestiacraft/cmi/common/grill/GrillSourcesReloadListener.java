package dev.celestiacraft.cmi.common.grill;

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
 * grill_sources 数据包加载器
 *
 * <p>
 * 扫描 {@code data/<namespace>/cmi/grill_sources/*.json}, 解析结果交给 {@link CmiGrillSources} 保管
 * </p>
 *
 * <p>
 * 通过 {@code AddReloadListenerEvent} 注册, 服务端生效; 客户端由 {@link CmiGrillSources#onDatapackSync}
 * 下发同一份配置, 保证两侧判定一致
 * </p>
 */
public class GrillSourcesReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new GsonBuilder()
			.setLenient()
			.create();

	public GrillSourcesReloadListener() {
		super(GSON, CmiGrillSources.DIRECTORY);
	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		CmiGrillSources.apply(objects);
	}
}