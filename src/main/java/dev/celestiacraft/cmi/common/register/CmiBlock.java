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
import dev.celestiacraft.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlock;
import dev.celestiacraft.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlock;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlock;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerItem;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlock;
import dev.celestiacraft.cmi.common.block.test_gravel.TestGravelBlock;
import dev.celestiacraft.cmi.common.block.usb_socket.UsbSocketBlock;
import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlock;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlock;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorItem;
import dev.celestiacraft.cmi.common.block.water_pump.WaterPumpBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
	public static final BlockEntry<AcceleratorBlock> ACCELERATOR;
	public static final BlockEntry<UsbSocketBlock> USB_SOCKET;
	public static final BlockEntry<TestMultiblockBlock> TEST_MULTIBLOCK;
	public static final BlockEntry<TestCokeOvenBlock> TEST_COKE_OVEN;

	static {
		ACCELERATOR = Cmi.REGISTRATE.block("accelerator", AcceleratorBlock::new)
				.item(AcceleratorItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/accelerator"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/accelerator")))
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
				.item()
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/water_pump"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_AXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/water_pump")))
										.build();
							});
				})
				.register();
		MARS_GEO = Cmi.REGISTRATE.block("mars_geothermal_vent", MarsGeothermalVentBlock::new)
				.item()
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/mars_geothermal_vent"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/mars_geothermal_vent")))
										.build();
							});
				})
				.register();
		MERCURY_GEO = Cmi.REGISTRATE.block("mercury_geothermal_vent", MercuryGeothermalVentBlock::new)
				.item()
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/mercury_geothermal_vent"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/mercury_geothermal_vent")))
										.build();
							});
				})
				.register();
		STEAM_HAMMER = Cmi.REGISTRATE.block("steam_hammer", SteamHammerBlock::new)
				.initialProperties(SharedProperties::stone)
				.transform(BlockStressDefaults.setImpact(16.0))
				.item(SteamHammerItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/steam_hammer/item"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/steam_hammer/block")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();
		ACCELERATOR_MOTOR = Cmi.REGISTRATE.block("accelerator_motor", AcceleratorMotorBlock::new)
				.initialProperties(SharedProperties::stone)
				.transform(BlockStressDefaults.setCapacity(0))
				.transform(BlockStressDefaults.setGeneratorSpeed(() -> Couple.create(0, 256)))
				.item(AcceleratorMotorItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/accelerator_motor/item"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								Direction facing = state.getValue(BlockStateProperties.FACING);
								if (facing.getAxis() == Direction.Axis.Y) {
									return ConfiguredModel.builder()
											.modelFile(models.getExistingFile(provider.modLoc("block/accelerator_motor/block_vertical")))
											.rotationX(facing == Direction.DOWN ? 180 : 0)
											.build();
								}
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/accelerator_motor/block")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();
		ADVANCED_SPOUT = Cmi.REGISTRATE.block("advanced_spout", AdvancedSpoutBlock::new)
				.initialProperties(SharedProperties::copperMetal)
				.item(AssemblyOperatorBlockItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/advanced_spout/item"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								boolean powered = state.getValue(BlockStateProperties.POWERED);
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc(powered ? "block/advanced_spout/block" : "block/advanced_spout/block_off")))
										.build();
							});
				})
				.register();
		VOID_DUST_COLLECTOR = Cmi.REGISTRATE.block("void_dust_collector", VoidDustCollectorBlock::new)
				.item(VoidDustCollectorItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/void_dust_collector/off"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
								boolean working = state.getValue(VoidDustCollectorBlock.WORKING);
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc(working ? "block/void_dust_collector/on" : "block/void_dust_collector/off")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();
		BELT_GRINDER = Cmi.REGISTRATE.block("mechanical_belt_grinder", BeltGrinderBlock::new)
				.initialProperties(SharedProperties::stone)
				.transform(BlockStressDefaults.setImpact(8.0))
				.item()
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/mechanical_belt_grinder/item"));
				})
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/mechanical_belt_grinder/block")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();
		USB_SOCKET = Cmi.REGISTRATE.block("usb_socket", UsbSocketBlock::new)
				.item()
				.build()
		TEST_MULTIBLOCK = Cmi.REGISTRATE.block("test_multiblock_controller", TestMultiblockBlock::new)
				.initialProperties(SharedProperties::stone)
				.item()
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/test_multiblock_controller"));
				})
				.build()
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
								boolean working = state.getValue(VoidDustCollectorBlock.WORKING);
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc(working ? "block/void_dust_collector/on" : "block/void_dust_collector/off")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();
		TEST_COKE_OVEN = Cmi.REGISTRATE.block("test_coke_oven", TestCokeOvenBlock::new)
				.initialProperties(SharedProperties::stone)
				.item()
				.model((context, provider) -> {
					provider.withExistingParent(context.getName(), provider.modLoc("block/test_coke_oven"));
				})
				.build()
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
								boolean working = state.getValue(VoidDustCollectorBlock.WORKING);
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc(working ? "block/test_coke_oven/on" : "block/test_coke_oven/off")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Blocks Registered!");
	}
}
