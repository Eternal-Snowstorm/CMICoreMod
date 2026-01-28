package top.nebula.cmi.common.register;

import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.block.accelerator_motor.AcceleratorMotorBlockEntity;
import top.nebula.cmi.common.block.accelerator_motor.AcceleratorMotorRenderer;
import top.nebula.cmi.common.block.hydraulic_press.HydraulicPressBlockEntity;
import top.nebula.cmi.common.block.hydraulic_press.HydraulicPressInstance;
import top.nebula.cmi.common.block.hydraulic_press.HydraulicPressRenderer;
import top.nebula.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlockEntity;
import top.nebula.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlockEntity;
import top.nebula.cmi.common.block.test_gravel.TestGravelBlockEntity;
import top.nebula.cmi.common.block.water_pump.WaterPumpBlockEntity;

public class ModBlockEntityTypes {
	public static final BlockEntityEntry<TestGravelBlockEntity> TEST_GRAVEL;
	public static final BlockEntityEntry<MarsGeothermalVentBlockEntity> MARS_GEO;
	public static final BlockEntityEntry<MercuryGeothermalVentBlockEntity> MERCURY_GEO;
	public static final BlockEntityEntry<WaterPumpBlockEntity> WATER_PUMP;
	public static final BlockEntityEntry<HydraulicPressBlockEntity> HYDRAULIC_PRESS;
	public static final BlockEntityEntry<AcceleratorMotorBlockEntity> AC_MOTOR;

	static {
		TEST_GRAVEL = Cmi.REGISTRATE.blockEntity("test_gravel", TestGravelBlockEntity::new)
				.validBlock(ModBlocks.TEST_GRAVEL)
				.register();
		MARS_GEO = Cmi.REGISTRATE.blockEntity("mars_geothermal_vent", MarsGeothermalVentBlockEntity::new)
				.validBlock(ModBlocks.MARS_GEO)
				.register();
		MERCURY_GEO = Cmi.REGISTRATE.blockEntity("mercury_geothermal_vent", MercuryGeothermalVentBlockEntity::new)
				.validBlock(ModBlocks.MERCURY_GEO)
				.register();
		WATER_PUMP = Cmi.REGISTRATE.blockEntity("water_pump", WaterPumpBlockEntity::new)
				.validBlock(ModBlocks.WATER_PUMP)
				.register();
		HYDRAULIC_PRESS = Cmi.CREATE_REGISTRATE.blockEntity("hydraulic_press", HydraulicPressBlockEntity::new)
				.instance(() -> HydraulicPressInstance::new)
				.renderer(() -> HydraulicPressRenderer::new)
				.validBlock(ModBlocks.HYDRAULIC_PRESS)
				.register();
		AC_MOTOR = Cmi.CREATE_REGISTRATE.blockEntity("accelerator_motor", AcceleratorMotorBlockEntity::new)
				.instance(() -> HalfShaftInstance::new, false)
				.validBlocks(ModBlocks.ACCELERATOR_MOTOR)
				.renderer(() -> AcceleratorMotorRenderer::new)
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core BlockEntities Registered!");
	}
}