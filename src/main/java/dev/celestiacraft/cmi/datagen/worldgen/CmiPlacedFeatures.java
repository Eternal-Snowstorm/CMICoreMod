package dev.celestiacraft.cmi.datagen.worldgen;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

/**
* 只做给 /place feature cmi:glacian_tree 调试树形用的占位地物
* <p>
* 修饰器留空 = 直接生成在命令指定的坐标;
* 以后要让它在 Glacio 上自然生成, 再往这里加修饰器并挂进生物群系.
*/
public class CmiPlacedFeatures {
	public static final ResourceKey<PlacedFeature> GLACIAN_TREE = register("glacian_tree");

	public static void bootstrap(BootstapContext<PlacedFeature> context) {
		context.register(GLACIAN_TREE, new PlacedFeature(
				context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(CmiConfiguredFeatures.GLACIAN_TREE),
				List.of()
		));
	}

	private static ResourceKey<PlacedFeature> register(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, Cmi.loadResource(name));
	}
}
