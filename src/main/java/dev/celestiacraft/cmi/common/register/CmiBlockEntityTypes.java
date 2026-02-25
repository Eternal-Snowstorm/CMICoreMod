package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorBlockEntity;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorRenderer;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlockEntity;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutRenderer;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderBlockEntity;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderInstance;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderRenderer;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlockEntity;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerInstance;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerRenderer;
import dev.celestiacraft.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlockEntity;
import dev.celestiacraft.cmi.common.block.test_gravel.TestGravelBlockEntity;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlockEnitiy;
import dev.celestiacraft.cmi.common.block.water_pump.WaterPumpBlockEntity;

public class CmiBlockEntityTypes {
	public static final BlockEntityEntry<TestGravelBlockEntity> TEST_GRAVEL;
	public static final BlockEntityEntry<MarsGeothermalVentBlockEntity> MARS_GEO;
	public static final BlockEntityEntry<MercuryGeothermalVentBlockEntity> MERCURY_GEO;
	public static final BlockEntityEntry<WaterPumpBlockEntity> WATER_PUMP;
	public static final BlockEntityEntry<SteamHammerBlockEntity> STEAM_HAMMER;
	public static final BlockEntityEntry<AcceleratorMotorBlockEntity> ACCELERATOR_MOTOR;
	public static final BlockEntityEntry<AdvancedSpoutBlockEntity> ADVANCED_SPOUT;
	public static final BlockEntityEntry<VoidDustCollectorBlockEnitiy> VOID_DUST_COLLECTOR;
	public static final BlockEntityEntry<BeltGrinderBlockEntity> BELT_GRINDER;

	static {
		TEST_GRAVEL = Cmi.REGISTRATE.blockEntity("test_gravel", TestGravelBlockEntity::new)
				.validBlock(CmiBlocks.TEST_GRAVEL)
				.register();
		MARS_GEO = Cmi.REGISTRATE.blockEntity("mars_geothermal_vent", MarsGeothermalVentBlockEntity::new)
				.validBlock(CmiBlocks.MARS_GEO)
				.register();
		MERCURY_GEO = Cmi.REGISTRATE.blockEntity("mercury_geothermal_vent", MercuryGeothermalVentBlockEntity::new)
				.validBlock(CmiBlocks.MERCURY_GEO)
				.register();
		WATER_PUMP = Cmi.REGISTRATE.blockEntity("water_pump", WaterPumpBlockEntity::new)
				.validBlock(CmiBlocks.WATER_PUMP)
				.register();
		STEAM_HAMMER = Cmi.REGISTRATE.blockEntity("steam_hammer", SteamHammerBlockEntity::new)
				.instance(() -> SteamHammerInstance::new)
				.renderer(() -> SteamHammerRenderer::new)
				.validBlock(CmiBlocks.STEAM_HAMMER)
				.register();
		ACCELERATOR_MOTOR = Cmi.REGISTRATE.blockEntity("accelerator_motor", AcceleratorMotorBlockEntity::new)
				.instance(() -> HalfShaftInstance::new, false)
				.validBlocks(CmiBlocks.ACCELERATOR_MOTOR)
				.renderer(() -> AcceleratorMotorRenderer::new)
				.register();
		ADVANCED_SPOUT = Cmi.REGISTRATE.blockEntity("advanced_spout", AdvancedSpoutBlockEntity::new)
				.validBlocks(CmiBlocks.ADVANCED_SPOUT)
				.renderer(() -> AdvancedSpoutRenderer::new)
				.register();
		VOID_DUST_COLLECTOR = Cmi.REGISTRATE.blockEntity("void_dust_collector", VoidDustCollectorBlockEnitiy::new)
				.validBlock(CmiBlocks.VOID_DUST_COLLECTOR)
				.register();
		BELT_GRINDER = Cmi.REGISTRATE.blockEntity("mechanical_belt_grinder", BeltGrinderBlockEntity::new)
				.instance(() -> BeltGrinderInstance::new)
				.validBlocks(CmiBlocks.BELT_GRINDER)
				.renderer(() -> BeltGrinderRenderer::new)
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core BlockEntities Registered!");
	}
}