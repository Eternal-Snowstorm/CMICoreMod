package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityVisual;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.block.CogWheelRegister;
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
import dev.celestiacraft.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelBlockEntity;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelRenderer;
import dev.celestiacraft.cmi.common.block.solar_boiler.bronze.BronzeSolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.block.solar_boiler.cast_iron.CastIronSolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.block.solar_boiler.steel.SteelSolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlockEntity;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerInstance;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerRenderer;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlockEntity;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenIOBlockEntity;
import dev.celestiacraft.cmi.common.block.test_gravel.TestGravelBlockEntity;
import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlockEntity;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlockEnitiy;
import dev.celestiacraft.cmi.common.block.well.blazing_blood.BlazingBloodWellBlockEntity;
import dev.celestiacraft.cmi.common.block.well.lava.LavaWellBlockEntity;
import dev.celestiacraft.cmi.common.block.well.water.WaterWellBlockEntity;

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
	public static final BlockEntityEntry<VoidDustCollectorBlockEnitiy> VOID_DUST_COLLECTOR;
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

	static {
		registerCogWheel();

		TEST_GRAVEL = Cmi.REGISTRATE.blockEntity("test_gravel", TestGravelBlockEntity::new)
				.validBlock(CmiBlock.TEST_GRAVEL)
				.register();
		MARS_GEO = Cmi.REGISTRATE.blockEntity("mars_geothermal_vent", MarsGeothermalVentBlockEntity::new)
				.validBlock(CmiBlock.MARS_GEO)
				.register();
		MERCURY_GEO = Cmi.REGISTRATE.blockEntity("mercury_geothermal_vent", MercuryGeothermalVentBlockEntity::new)
				.validBlock(CmiBlock.MERCURY_GEO)
				.register();
		WATER_WELL = Cmi.REGISTRATE.blockEntity("water_well", WaterWellBlockEntity::new)
				.validBlock(CmiBlock.WATER_WELL)
				.register();
		BLAZING_BLOOD_WELL = Cmi.REGISTRATE.blockEntity("blazing_blood_well", BlazingBloodWellBlockEntity::new)
				.validBlock(CmiBlock.BLAZING_BLOOD_WELL)
				.register();
		LAVA_WELL = Cmi.REGISTRATE.blockEntity("lava_well", LavaWellBlockEntity::new)
				.validBlock(CmiBlock.LAVA_WELL)
				.register();
		STEAM_HAMMER = Cmi.REGISTRATE.blockEntity("steam_hammer", SteamHammerBlockEntity::new)
				.visual(() -> SteamHammerInstance::new)
				.renderer(() -> SteamHammerRenderer::new)
				.validBlock(CmiBlock.STEAM_HAMMER)
				.register();
		ACCELERATOR_MOTOR = Cmi.REGISTRATE.blockEntity("accelerator_motor", AcceleratorMotorBlockEntity::new)
				.visual(() -> ShaftVisual::new, false)
				.validBlocks(CmiBlock.ACCELERATOR_MOTOR)
				.renderer(() -> AcceleratorMotorRenderer::new)
				.register();
		ADVANCED_SPOUT = Cmi.REGISTRATE.blockEntity("advanced_spout", AdvancedSpoutBlockEntity::new)
				.validBlocks(CmiBlock.ADVANCED_SPOUT)
				.renderer(() -> AdvancedSpoutRenderer::new)
				.register();
		VOID_DUST_COLLECTOR = Cmi.REGISTRATE.blockEntity("void_dust_collector", VoidDustCollectorBlockEnitiy::new)
				.validBlock(CmiBlock.VOID_DUST_COLLECTOR)
				.register();
		BELT_GRINDER = Cmi.REGISTRATE.blockEntity("mechanical_belt_grinder", BeltGrinderBlockEntity::new)
				.visual(() -> BeltGrinderInstance::new)
				.validBlocks(CmiBlock.BELT_GRINDER)
				.renderer(() -> BeltGrinderRenderer::new)
				.register();
		TEST_MULTIBLOCK = Cmi.REGISTRATE.blockEntity("test_multiblock_controller", TestMultiblockBlockEntity::new)
				.validBlock(CmiBlock.TEST_MULTIBLOCK)
				.register();
		TEST_COKE_OVEN = Cmi.REGISTRATE.blockEntity("test_coke_oven", TestCokeOvenBlockEntity::new)
				.validBlock(CmiBlock.TEST_COKE_OVEN)
				.register();
		TEST_COKE_OVEN_IO = Cmi.REGISTRATE.blockEntity("test_coke_oven_io", TestCokeOvenIOBlockEntity::new)
				.validBlock(CmiBlock.TEST_COKE_OVEN_IO)
				.register();
		BRONZE_FLUID_BURNER = Cmi.REGISTRATE.blockEntity("bronze_fluid_burner", BronzeFluidBurnerBlockEntity::new)
				.validBlock(CmiBlock.BRONZE_FLUID_BURNER)
				.register();
		CAST_IRON_FLUID_BURNER = Cmi.REGISTRATE.blockEntity("cast_iron_fluid_burner", CastIronFluidBurnerBlockEntity::new)
				.validBlock(CmiBlock.CAST_IRON_FLUID_BURNER)
				.register();
		STEEL_FLUID_BURNER = Cmi.REGISTRATE.blockEntity("steel_fluid_burner", SteelFluidBurnerBlockEntity::new)
				.validBlock(CmiBlock.STEEL_FLUID_BURNER)
				.register();
		BRONZE_SOLAR_BOILER = Cmi.REGISTRATE.blockEntity("bronze_solar_boiler", BronzeSolarBoilerBlockEntity::new)
				.validBlock(CmiBlock.BRONZE_SOLAR_BOILER)
				.register();
		CAST_IRON_SOLAR_BOILER = Cmi.REGISTRATE.blockEntity("cast_iron_solar_boiler", CastIronSolarBoilerBlockEntity::new)
				.validBlock(CmiBlock.CAST_IRON_SOLAR_BOILER)
				.register();
		STEEL_SOLAR_BOILER = Cmi.REGISTRATE.blockEntity("steel_solar_boiler", SteelSolarBoilerBlockEntity::new)
				.validBlock(CmiBlock.STEEL_SOLAR_BOILER)
				.register();
	}

	private static BlockEntityEntry registerCogWheel() {
		CreateBlockEntityBuilder<MetalCogWheelBlockEntity, CreateRegistrate> builder = Cmi.REGISTRATE.blockEntity("cogwheel", MetalCogWheelBlockEntity::new);

		CogWheelRegister.COMMON_LIST.forEach((cogwheel) -> {
			builder.validBlock(cogwheel);
		});
		builder.visual(() -> BracketedKineticBlockEntityVisual::create, true);
		builder.renderer(() -> MetalCogWheelRenderer::new);

		return builder.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core BlockEntities Registered!");
	}
}