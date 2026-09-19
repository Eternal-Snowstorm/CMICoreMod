package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.datagen.worldgen.tree.GlacianFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CmiFoliagePlacer {
	private static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS;

	public static final RegistryObject<FoliagePlacerType<GlacianFoliagePlacer>> GLACIAN;

	static {
		FOLIAGE_PLACERS = DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, Cmi.MODID);

		GLACIAN = FOLIAGE_PLACERS.register("glacian_foliage_placer", () -> {
			return new FoliagePlacerType<>(GlacianFoliagePlacer.CODEC);
		});
	}

	public static void register(IEventBus bus) {
		Cmi.LOGGER.info("{} FoliagePlacerTypes Registered!", Cmi.NAME);
		FOLIAGE_PLACERS.register(bus);
	}
}