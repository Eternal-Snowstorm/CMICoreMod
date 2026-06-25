package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorBlockEntity;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorRenderer;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlockEntity;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutRenderer;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderBlockEntity;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderInstance;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderRenderer;
import dev.celestiacraft.cmi.common.block.fluid_burner.bronze.BronzeFluidBurnerBlockEntity;
import dev.celestiacraft.cmi.common.block.fluid_burner.cast_iron.CastIronFluidBurnerBlockEntity;
import dev.celestiacraft.cmi.common.block.fluid_burner.steel.SteelFluidBurnerBlockEntity;
import dev.celestiacraft.cmi.common.block.geothermal_generator.GGBlockEntity;
import dev.celestiacraft.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.wind_vane.WindVaneBlockEntity;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelBlockEntity;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelVisual;
import dev.celestiacraft.cmi.common.block.solar_boiler.bronze.BronzeSolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.block.solar_boiler.cast_iron.CastIronSolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.block.solar_boiler.steel.SteelSolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockEntity;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlockEntity;
import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopBlockEntity;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlockEntity;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerInstance;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerRenderer;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlockEntity;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenIOBlockEntity;
import dev.celestiacraft.cmi.common.block.test_gravel.TestGravelBlockEntity;
import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlockEntity;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlockEntity;
import dev.celestiacraft.cmi.common.block.well.blazing_blood.BlazingBloodWellBlockEntity;
import dev.celestiacraft.cmi.common.block.well.lava.LavaWellBlockEntity;
import dev.celestiacraft.cmi.common.block.well.water.WaterWellBlockEntity;
import dev.celestiacraft.cmi.common.register.block.*;

public class CmiBlockEntity {
	public static final BlockEntityEntry<TestGravelBlockEntity> TEST_GRAVEL;
	public static final BlockEntityEntry<MarsGeothermalVentBlockEntity> MARS_GEO;
	public static final BlockEntityEntry<MercuryGeothermalVentBlockEntity> MERCURY_GEO;
	public static final BlockEntityEntry<WaterWellBlockEntity> WATER_WELL;
	public static final BlockEntityEntry<BlazingBloodWellBlockEntity> BLAZING_BLOOD_WELL;
	public static final BlockEntityEntry<LavaWellBlockEntity> LAVA_WELL;
	public static final BlockEntityEntry<SteamHammerBlockEntity> STEAM_HAMMER;
	public static final BlockEntityEntry<AcceleratorMotorBlockEntity> ACCELERATOR_MOTOR;
	public static final BlockEntityEntry<AdvancedSpoutBlockEntity> ADVANCED_SPOUT;
	public static final BlockEntityEntry<VoidDustCollectorBlockEntity> VOID_DUST_COLLECTOR;
	public static final BlockEntityEntry<BeltGrinderBlockEntity> BELT_GRINDER;
	public static final BlockEntityEntry<TestMultiblockBlockEntity> TEST_MULTIBLOCK;
	public static final BlockEntityEntry<TestCokeOvenBlockEntity> TEST_COKE_OVEN;
	public static final BlockEntityEntry<TestCokeOvenIOBlockEntity> TEST_COKE_OVEN_IO;
	public static final BlockEntityEntry<BronzeFluidBurnerBlockEntity> BRONZE_FLUID_BURNER;
	public static final BlockEntityEntry<CastIronFluidBurnerBlockEntity> CAST_IRON_FLUID_BURNER;
	public static final BlockEntityEntry<SteelFluidBurnerBlockEntity> STEEL_FLUID_BURNER;
	public static final BlockEntityEntry<BronzeSolarBoilerBlockEntity> BRONZE_SOLAR_BOILER;
	public static final BlockEntityEntry<CastIronSolarBoilerBlockEntity> CAST_IRON_SOLAR_BOILER;
	public static final BlockEntityEntry<SteelSolarBoilerBlockEntity> STEEL_SOLAR_BOILER;
	public static final BlockEntityEntry<SpaceElevatorBaseConsoleBlockEntity> SPACE_ELEVATOR_BASE_CONSOLE;
	public static final BlockEntityEntry<SpaceElevatorIoPortBlockEntity> SPACE_ELEVATOR_IO_PORT;
	public static final BlockEntityEntry<SpaceElevatorTopBlockEntity> SPACE_ELEVATOR_TOP;
	public static final BlockEntityEntry<MetalCogWheelBlockEntity> COGWHEEL;
	public static final BlockEntityEntry<WindVaneBlockEntity> WIND_VANE;
	public static final BlockEntityEntry<GGBlockEntity> GEOTHERMAL_GENERATOR;

	static {
		TEST_GRAVEL = Cmi.REGISTRATE.blockEntity("test_gravel", TestGravelBlockEntity::new)
				.validBlock(OtherBlocks.TEST_GRAVEL)
				.register();

		MARS_GEO = Cmi.REGISTRATE.blockEntity("mars_geothermal_vent", MarsGeothermalVentBlockEntity::new)
				.validBlock(VentBlocks.MARS_GEO)
				.register();

		MERCURY_GEO = Cmi.REGISTRATE.blockEntity("mercury_geothermal_vent", MercuryGeothermalVentBlockEntity::new)
				.validBlock(VentBlocks.MERCURY_GEO)
				.register();

		WATER_WELL = Cmi.REGISTRATE.blockEntity("water_well", WaterWellBlockEntity::new)
				.validBlock(WallBlocks.WATER_WELL)
				.register();

		BLAZING_BLOOD_WELL = Cmi.REGISTRATE.blockEntity("blazing_blood_well", BlazingBloodWellBlockEntity::new)
				.validBlock(WallBlocks.BLAZING_BLOOD_WELL)
				.register();

		LAVA_WELL = Cmi.REGISTRATE.blockEntity("lava_well", LavaWellBlockEntity::new)
				.validBlock(WallBlocks.LAVA_WELL)
				.register();

		STEAM_HAMMER = Cmi.REGISTRATE.blockEntity("steam_hammer", SteamHammerBlockEntity::new)
				.visual(() -> SteamHammerInstance::new)
				.renderer(() -> SteamHammerRenderer::new)
				.validBlock(MachineBlocks.STEAM_HAMMER)
				.register();

		ACCELERATOR_MOTOR = Cmi.REGISTRATE.blockEntity("accelerator_motor", AcceleratorMotorBlockEntity::new)
				.visual(() -> ShaftVisual::new, false)
				.validBlocks(MachineBlocks.ACCELERATOR_MOTOR)
				.renderer(() -> AcceleratorMotorRenderer::new)
				.register();

		ADVANCED_SPOUT = Cmi.REGISTRATE.blockEntity("advanced_spout", AdvancedSpoutBlockEntity::new)
				.validBlocks(MachineBlocks.ADVANCED_SPOUT)
				.renderer(() -> AdvancedSpoutRenderer::new)
				.register();

		VOID_DUST_COLLECTOR = Cmi.REGISTRATE.blockEntity("void_dust_collector", VoidDustCollectorBlockEntity::new)
				.validBlock(MachineBlocks.VOID_DUST_COLLECTOR)
				.register();

		BELT_GRINDER = Cmi.REGISTRATE.blockEntity("mechanical_belt_grinder", BeltGrinderBlockEntity::new)
				.visual(() -> BeltGrinderInstance::new)
				.validBlocks(MachineBlocks.BELT_GRINDER)
				.renderer(() -> BeltGrinderRenderer::new)
				.register();

		TEST_MULTIBLOCK = Cmi.REGISTRATE.blockEntity("test_multiblock_controller", TestMultiblockBlockEntity::new)
				.validBlock(MultiblockBlocks.TEST_MULTIBLOCK)
				.register();

		TEST_COKE_OVEN = Cmi.REGISTRATE.blockEntity("test_coke_oven", TestCokeOvenBlockEntity::new)
				.validBlock(MultiblockBlocks.TEST_COKE_OVEN)
				.register();

		TEST_COKE_OVEN_IO = Cmi.REGISTRATE.blockEntity("test_coke_oven_io", TestCokeOvenIOBlockEntity::new)
				.validBlock(MultiblockBlocks.TEST_COKE_OVEN_IO)
				.register();

		BRONZE_FLUID_BURNER = Cmi.REGISTRATE.blockEntity("bronze_fluid_burner", BronzeFluidBurnerBlockEntity::new)
				.validBlock(FluidBurnerBlocks.BRONZE_FLUID_BURNER)
				.register();

		CAST_IRON_FLUID_BURNER = Cmi.REGISTRATE.blockEntity("cast_iron_fluid_burner", CastIronFluidBurnerBlockEntity::new)
				.validBlock(FluidBurnerBlocks.CAST_IRON_FLUID_BURNER)
				.register();

		STEEL_FLUID_BURNER = Cmi.REGISTRATE.blockEntity("steel_fluid_burner", SteelFluidBurnerBlockEntity::new)
				.validBlock(FluidBurnerBlocks.STEEL_FLUID_BURNER)
				.register();

		BRONZE_SOLAR_BOILER = Cmi.REGISTRATE.blockEntity("bronze_solar_boiler", BronzeSolarBoilerBlockEntity::new)
				.validBlock(SolarBoilerBlocks.BRONZE_SOLAR_BOILER)
				.register();

		CAST_IRON_SOLAR_BOILER = Cmi.REGISTRATE.blockEntity("cast_iron_solar_boiler", CastIronSolarBoilerBlockEntity::new)
				.validBlock(SolarBoilerBlocks.CAST_IRON_SOLAR_BOILER)
				.register();

		STEEL_SOLAR_BOILER = Cmi.REGISTRATE.blockEntity("steel_solar_boiler", SteelSolarBoilerBlockEntity::new)
				.validBlock(SolarBoilerBlocks.STEEL_SOLAR_BOILER)
				.register();

		SPACE_ELEVATOR_BASE_CONSOLE = Cmi.REGISTRATE.blockEntity("space_elevator_base_console", SpaceElevatorBaseConsoleBlockEntity::new)
				.validBlock(SpaceElevatorBlocks.SPACE_ELEVATOR_BASE_CONSOLE)
				.register();

		SPACE_ELEVATOR_IO_PORT = Cmi.REGISTRATE.blockEntity("space_elevator_io_port", SpaceElevatorIoPortBlockEntity::new)
				.validBlock(SpaceElevatorBlocks.SPACE_ELEVATOR_IO_PORT)
				.register();

		SPACE_ELEVATOR_TOP = Cmi.REGISTRATE.blockEntity("space_elevator_top", SpaceElevatorTopBlockEntity::new)
				.validBlock(SpaceElevatorBlocks.SPACE_ELEVATOR_TOP)
				.register();

		WIND_VANE = Cmi.REGISTRATE.blockEntity("wind_vane", WindVaneBlockEntity::new)
				.validBlock(OtherBlocks.WIND_VANE)
				.register();

		GEOTHERMAL_GENERATOR = Cmi.REGISTRATE.blockEntity("geothermal_generator", GGBlockEntity::new)
				.validBlock(MachineBlocks.GEOTHERMAL_GENERATOR)
				.register();

		CreateBlockEntityBuilder<MetalCogWheelBlockEntity, CreateRegistrate> cogwheelBuilder = Cmi.REGISTRATE
				.blockEntity("cogwheel", MetalCogWheelBlockEntity::new)
				.visual(() -> MetalCogWheelVisual::create);
		MetalCogWheelRegister.COMMON_LIST.forEach(cogwheelBuilder::validBlock);

		COGWHEEL = cogwheelBuilder.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} BlockEntities Registered!", Cmi.MODID);
	}
}