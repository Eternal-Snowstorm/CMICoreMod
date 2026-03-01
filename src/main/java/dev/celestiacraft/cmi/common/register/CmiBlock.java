package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.accelerator.AcceleratorBlock;
import dev.celestiacraft.cmi.common.block.accelerator.AcceleratorItem;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorBlock;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorItem;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlock;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderBlock;
import dev.celestiacraft.cmi.common.block.golden_sapling.GoldenSaplingBlock;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlock;
import dev.celestiacraft.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlock;
import dev.celestiacraft.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlock;
import dev.celestiacraft.cmi.common.block.test_gravel.TestGravelBlock;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlock;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorItem;
import dev.celestiacraft.cmi.common.block.water_pump.WaterPumpBlock;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerItem;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;

public class CmiBlock {
	public static final BlockEntry<TestGravelBlock> TEST_GRAVEL;
	public static final BlockEntry<GoldenSaplingBlock> GOLD_SAPLING;
	public static final BlockEntry<MarsGeothermalVentBlock> MARS_GEO;
	public static final BlockEntry<MercuryGeothermalVentBlock> MERCURY_GEO;
	public static final BlockEntry<WaterPumpBlock> WATER_PUMP;
	public static final BlockEntry<SteamHammerBlock> STEAM_HAMMER;
	public static final BlockEntry<AcceleratorMotorBlock> ACCELERATOR_MOTOR;
	public static final BlockEntry<AdvancedSpoutBlock> ADVANCED_SPOUT;
	public static final BlockEntry<VoidDustCollectorBlock> VOID_DUST_COLLECTOR;
	public static final BlockEntry<BeltGrinderBlock> BELT_GRINDER;
	public static final BlockEntry<AcceleratorBlock> ACCELERATOR_BLOCK;

	static {
		ACCELERATOR_BLOCK = Cmi.REGISTRATE.block("accelerator", AcceleratorBlock::new)
				.item(AcceleratorItem::new)
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(Cmi.loadResource("block/accelerator")))
										.build();
							});
				})
				.register();

		TEST_GRAVEL = Cmi.REGISTRATE.block("test_gravel", TestGravelBlock::new)
				.item()
				.build()
				.register();
		GOLD_SAPLING = Cmi.REGISTRATE.block("gold_sapling", GoldenSaplingBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.item()
				.build()
				.register();
		WATER_PUMP = Cmi.REGISTRATE.block("water_pump", WaterPumpBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.item()
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		MARS_GEO = Cmi.REGISTRATE.block("mars_geothermal_vent", MarsGeothermalVentBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.item()
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		MERCURY_GEO = Cmi.REGISTRATE.block("mercury_geothermal_vent", MercuryGeothermalVentBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.item()
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		STEAM_HAMMER = Cmi.REGISTRATE.block("steam_hammer", SteamHammerBlock::new)
				.initialProperties(SharedProperties::stone)
				.blockstate(NonNullBiConsumer.noop())
				.transform(BlockStressDefaults.setImpact(16.0))
				.item(SteamHammerItem::new)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		ACCELERATOR_MOTOR = Cmi.REGISTRATE.block("accelerator_motor", AcceleratorMotorBlock::new)
				.initialProperties(SharedProperties::stone)
				.blockstate(NonNullBiConsumer.noop())
				.transform(BlockStressDefaults.setCapacity(0))
				.transform(BlockStressDefaults.setGeneratorSpeed(() -> Couple.create(0, 256)))
				.item(AcceleratorMotorItem::new)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		ADVANCED_SPOUT = Cmi.REGISTRATE.block("advanced_spout", AdvancedSpoutBlock::new)
				.initialProperties(SharedProperties::copperMetal)
				.blockstate(NonNullBiConsumer.noop())
				.item(AssemblyOperatorBlockItem::new)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		VOID_DUST_COLLECTOR = Cmi.REGISTRATE.block("void_dust_collector", VoidDustCollectorBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.item(VoidDustCollectorItem::new)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		BELT_GRINDER = Cmi.REGISTRATE.block("mechanical_belt_grinder", BeltGrinderBlock::new)
				.initialProperties(SharedProperties::stone)
				.blockstate(NonNullBiConsumer.noop())
				.transform(BlockStressDefaults.setImpact(8.0))
				.item()
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Blocks Registered!");
	}
}
