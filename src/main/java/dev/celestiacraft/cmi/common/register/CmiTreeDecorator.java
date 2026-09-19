package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.datagen.worldgen.tree.SnowCapDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CmiTreeDecorator {
	private static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS;

	public static final RegistryObject<TreeDecoratorType<SnowCapDecorator>> SNOW_CAP;

	static {
		TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, Cmi.MODID);

		SNOW_CAP = TREE_DECORATORS.register("snow_cap", () -> {
			return new TreeDecoratorType<>(SnowCapDecorator.CODEC);
		});
	}

	public static void register(IEventBus bus) {
		Cmi.LOGGER.info("{} TreeDecoratorTypes Registered!", Cmi.NAME);
		TREE_DECORATORS.register(bus);
	}
}