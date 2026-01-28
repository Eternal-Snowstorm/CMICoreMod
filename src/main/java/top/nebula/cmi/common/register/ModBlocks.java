package top.nebula.cmi.common.register;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.block.golden_sapling.GoldenSaplingBlock;
import top.nebula.cmi.common.block.hydraulic_press.HydraulicPressBlock;
import top.nebula.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlock;
import top.nebula.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlock;
import top.nebula.cmi.common.block.test_gravel.TestGravelBlock;
import top.nebula.cmi.common.block.water_pump.WaterPumpBlock;

public class ModBlocks {
	public static final BlockEntry<GoldenSaplingBlock> GOLD_SAPLING;
	public static final BlockEntry<WaterPumpBlock> WATER_PUMP;
	public static final BlockEntry<MarsGeothermalVentBlock> MARS_GEO;
	public static final BlockEntry<MercuryGeothermalVentBlock> MERCURY_GEO;
	public static final BlockEntry<TestGravelBlock> TEST_GRAVEL;
	public static final BlockEntry<HydraulicPressBlock> HYDRAULIC_PRESS;

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
		HYDRAULIC_PRESS = Cmi.REGISTRATE.block("hydraulic_press", HydraulicPressBlock::new)
				.initialProperties(SharedProperties::stone)
				.properties(BlockBehaviour.Properties::noOcclusion)
				.blockstate(BlockStateGen.horizontalBlockProvider(true))
				.transform(BlockStressDefaults.setImpact(24.0))
				.item(AssemblyOperatorBlockItem::new)
				.transform(ModelGen.customItemModel())
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Blocks Registered!");
	}
}