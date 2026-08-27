package dev.celestiacraft.cmi.event.worldgen;

import com.mojang.datafixers.util.Pair;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.mixin.StructureTemplatePoolMixin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TinkerCabin {
	@SubscribeEvent
	public static void onAboutToStart(ServerAboutToStartEvent event) {
		MinecraftServer server = event.getServer();

		final List<String> TINKER_STRUCTURES = List.of(
				"desert",
				"plains",
				"savanna",
				"snowy",
				"taiga"
		);

		Registry<StructureTemplatePool> registry = server.registryAccess()
				.registryOrThrow(Registries.TEMPLATE_POOL);

		TINKER_STRUCTURES.forEach((name) -> {
			ResourceLocation poolId = ResourceLocation.withDefaultNamespace(
					"village/%s/houses".formatted(name)
			);

			ResourceLocation structureId = Cmi.loadResource("village/tinker/" + name);

			ResourceKey<StructureTemplatePool> poolKey = ResourceKey.create(
					Registries.TEMPLATE_POOL,
					poolId
			);

			StructureTemplatePool pool = registry.get(poolKey);

			if (pool == null) {
				Cmi.LOGGER.warn("Cannot find village structure pool: {}", poolId);
				return;
			}

			Function<StructureTemplatePool.Projection, SinglePoolElement> element = StructurePoolElement.single(
					structureId.toString()
			);

			SinglePoolElement projectedElement = element.apply(StructureTemplatePool.Projection.RIGID);

			StructureTemplatePoolMixin accessor = (StructureTemplatePoolMixin) (Object) pool;
			ObjectArrayList<StructurePoolElement> templates = accessor.cmi$getTemplates();
			List<Pair<StructurePoolElement, Integer>> rawTemplates = accessor.cmi$getRawTemplates();

			templates.add(projectedElement);
			rawTemplates.add(Pair.of(projectedElement, 1));

			Cmi.LOGGER.info(
					"Added {} to village structure pool {}",
					structureId,
					poolId
			);
		});
	}
}