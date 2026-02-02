package top.nebula.cmi.common.register;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.block.accelerator_motor.AcceleratorMotorBlock;
import top.nebula.cmi.common.block.accelerator_motor.AcceleratorMotorItem;
import top.nebula.cmi.common.block.advanced_spout.AdvancedSpoutBlock;
import top.nebula.cmi.common.block.golden_sapling.GoldenSaplingBlock;
import top.nebula.cmi.common.block.steam_hammer.SteamHammerBlock;
import top.nebula.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlock;
import top.nebula.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlock;
import top.nebula.cmi.common.block.test_gravel.TestGravelBlock;
import top.nebula.cmi.common.block.void_dust_collector.VoidDustCollectorBlock;
import top.nebula.cmi.common.block.void_dust_collector.VoidDustCollectorItem;
import top.nebula.cmi.common.block.water_pump.WaterPumpBlock;
import top.nebula.cmi.common.block.steam_hammer.SteamHammerItem;

public class ModBlocks {
	public static final BlockEntry<GoldenSaplingBlock> GOLD_SAPLING;
	public static final BlockEntry<WaterPumpBlock> WATER_PUMP;
	public static final BlockEntry<MarsGeothermalVentBlock> MARS_GEO;
	public static final BlockEntry<MercuryGeothermalVentBlock> MERCURY_GEO;
	public static final BlockEntry<TestGravelBlock> TEST_GRAVEL;
	public static final BlockEntry<SteamHammerBlock> STEAM_HAMMER;
	public static final BlockEntry<AcceleratorMotorBlock> ACCELERATOR_MOTOR;
	public static final BlockEntry<AdvancedSpoutBlock> ADVANCED_SPOUT;
	public static final BlockEntry<VoidDustCollectorBlock> VOID_DUST_COLLECTOR;

	static {
		TEST_GRAVEL = Cmi.REGISTRATE.block("test_gravel", TestGravelBlock::new)
				.item()
				.build()
				.register();
		GOLD_SAPLING = Cmi.REGISTRATE.block("gold_sapling", GoldenSaplingBlock::new)
				.item()
				.build()
				.register();
		WATER_PUMP = Cmi.REGISTRATE.block("water_pump", WaterPumpBlock::new)
				.item()
				.build()
				.register();
		MARS_GEO = Cmi.REGISTRATE.block("mars_geothermal_vent", MarsGeothermalVentBlock::new)
				.item()
				.build()
				.register();
		MERCURY_GEO = Cmi.REGISTRATE.block("mercury_geothermal_vent", MercuryGeothermalVentBlock::new)
				.item()
				.build()
				.register();
		STEAM_HAMMER = Cmi.CREATE_REGISTRATE.block("steam_hammer", SteamHammerBlock::new)
				.initialProperties(SharedProperties::stone)
				.blockstate(BlockStateGen.horizontalBlockProvider(true))
				.transform(BlockStressDefaults.setImpact(16.0))
				.item(SteamHammerItem::new)
				.build()
				.register();
		ACCELERATOR_MOTOR = Cmi.REGISTRATE.block("accelerator_motor", AcceleratorMotorBlock::new)
				.initialProperties(SharedProperties::stone)
				.transform(BlockStressDefaults.setCapacity(0))
				.transform(BlockStressDefaults.setGeneratorSpeed(() -> Couple.create(0, 256)))
				.item(AcceleratorMotorItem::new)
				.build()
				.register();
		ADVANCED_SPOUT = Cmi.CREATE_REGISTRATE.block("advanced_spout", AdvancedSpoutBlock::new)
				.initialProperties(SharedProperties::copperMetal)
				.addLayer(() -> RenderType::cutoutMipped)
				.item(AssemblyOperatorBlockItem::new)
				.build()
				.register();
		VOID_DUST_COLLECTOR = Cmi.REGISTRATE.block("void_dust_collector", VoidDustCollectorBlock::new)
				.item(VoidDustCollectorItem::new)
				.build()
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Blocks Registered!");
	}
}