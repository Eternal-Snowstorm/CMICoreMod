package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.datagen.worldgen.tree.GlacianTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CmiTrunkPlacer {
	private static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS;

	public static final RegistryObject<TrunkPlacerType<GlacianTrunkPlacer>> GLACIAN;

	static {
		TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Cmi.MODID);

		GLACIAN = TRUNK_PLACERS.register("glacian_trunk_placer", () -> {
			return new TrunkPlacerType<>(GlacianTrunkPlacer.CODEC);
		});
	}

	public static void register(IEventBus bus) {
		Cmi.LOGGER.info("{} TrunkPlacerTypes Registered!", Cmi.NAME);
		TRUNK_PLACERS.register(bus);
	}
}