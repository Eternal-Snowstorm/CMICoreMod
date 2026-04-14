package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorBlockEntity;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorRenderer;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlockEntity;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutRenderer;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderBlockEntity;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderInstance;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderRenderer;
import dev.celestiacraft.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.pump.blazing_blood.BlazingBloodPumpBlockEntity;
import dev.celestiacraft.cmi.common.block.pump.lava.LavaPumpBlockEntity;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlockEntity;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerInstance;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerRenderer;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlockEntity;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenIOBlockEntity;
import dev.celestiacraft.cmi.common.block.test_gravel.TestGravelBlockEntity;
import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlockEntity;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlockEnitiy;
import dev.celestiacraft.cmi.common.block.pump.water.WaterPumpBlockEntity;
import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.bronze.BronzeFluidBurnerBlockEntity;
import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.cast_iron.CastIronFluidBurnerBlockEntity;
import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.steel.SteelFluidBurnerBlockEntity;

public class CmiBlockEntity {
	public static final BlockEntityEntry<TestGravelBlockEntity> TEST_GRAVEL;
	public static final BlockEntityEntry<MarsGeothermalVentBlockEntity> MARS_GEO;
	public static final BlockEntityEntry<MercuryGeothermalVentBlockEntity> MERCURY_GEO;
	public static final BlockEntityEntry<WaterPumpBlockEntity> WATER_PUMP;
	public static final BlockEntityEntry<BlazingBloodPumpBlockEntity> BLAZING_BLOOD_PUMP;
	public static final BlockEntityEntry<LavaPumpBlockEntity> LAVA_PUMP;
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

	static {
		TEST_GRAVEL = Cmi.REGISTRATE.blockEntity("test_gravel", TestGravelBlockEntity::new)
				.validBlock(CmiBlock.TEST_GRAVEL)
				.register();
		MARS_GEO = Cmi.REGISTRATE.blockEntity("mars_geothermal_vent", MarsGeothermalVentBlockEntity::new)
				.validBlock(CmiBlock.MARS_GEO)
				.register();
		MERCURY_GEO = Cmi.REGISTRATE.blockEntity("mercury_geothermal_vent", MercuryGeothermalVentBlockEntity::new)
				.validBlock(CmiBlock.MERCURY_GEO)
				.register();
		WATER_PUMP = Cmi.REGISTRATE.blockEntity("water_pump", WaterPumpBlockEntity::new)
				.validBlock(CmiBlock.WATER_PUMP)
				.register();
		BLAZING_BLOOD_PUMP = Cmi.REGISTRATE.blockEntity("blazing_blood_pump", BlazingBloodPumpBlockEntity::new)
				.validBlock(CmiBlock.BLAZING_BLOOD_PUMP)
				.register();
		LAVA_PUMP = Cmi.REGISTRATE.blockEntity("lava_pump", LavaPumpBlockEntity::new)
				.validBlock(CmiBlock.LAVA_PUMP)
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
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core BlockEntities Registered!");
	}
}