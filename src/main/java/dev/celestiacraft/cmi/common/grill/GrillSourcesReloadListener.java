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

public class GrillSourcesReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new GsonBuilder()
			.setLenient()
			.create();

	public GrillSourcesReloadListener() {
		super(GSON, CmiGrillSources.DIRECTORY);
	}

	// 解析结果交给 CmiGrillSources 保管
	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager manager, @NotNull ProfilerFiller filler) {
		CmiGrillSources.apply(objects);
	}
}