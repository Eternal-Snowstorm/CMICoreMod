package dev.celestiacraft.cmi.common.register.block;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.accelerator.AcceleratorBlock;
import dev.celestiacraft.cmi.common.block.accelerator.AcceleratorItem;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorBlock;
import dev.celestiacraft.cmi.common.block.accelerator_motor.AcceleratorMotorItem;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlock;
import dev.celestiacraft.cmi.common.block.belt_grinder.BeltGrinderBlock;
import dev.celestiacraft.cmi.common.block.geothermal_generator.GGBlock;
import dev.celestiacraft.cmi.common.block.geothermal_generator.GGItem;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerBlock;
import dev.celestiacraft.cmi.common.block.steam_hammer.SteamHammerItem;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlock;
import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorItem;
import dev.celestiacraft.cmi.compat.create.CmiStress;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.Tags;

public class MachineBlocks {
	public static final BlockEntry<SteamHammerBlock> STEAM_HAMMER;
	public static final BlockEntry<AcceleratorMotorBlock> ACCELERATOR_MOTOR;
	public static final BlockEntry<AdvancedSpoutBlock> ADVANCED_SPOUT;
	public static final BlockEntry<VoidDustCollectorBlock> VOID_DUST_COLLECTOR;
	public static final BlockEntry<BeltGrinderBlock> BELT_GRINDER;
	public static final BlockEntry<AcceleratorBlock> ACCELERATOR;
	public static final BlockEntry<GGBlock> GEOTHERMAL_GENERATOR;

	static {
		ACCELERATOR = Cmi.REGISTRATE.block("accelerator", AcceleratorBlock::new)
				.item(AcceleratorItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/accelerator")
					);
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

		STEAM_HAMMER = Cmi.REGISTRATE.block("steam_hammer", SteamHammerBlock::new)
				.initialProperties(SharedProperties::stone)
				.transform(CmiStress.setImpact(16.0))
				.item(SteamHammerItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/steam_hammer/item")
					);
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
				.transform(CmiStress.setCapacity(0))
				.onRegister(BlockStressValues.setGeneratorSpeed(256, true))
				.item(AcceleratorMotorItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/accelerator_motor/item")
					);
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
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/advanced_spout/item")
					);
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
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/void_dust_collector/off")
					);
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
								boolean working = state.getValue(BasicBlock.LIT);
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc(working ? "block/void_dust_collector/on" : "block/void_dust_collector/off")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();

		BELT_GRINDER = Cmi.REGISTRATE.block("mechanical_belt_grinder", BeltGrinderBlock::new)
				.initialProperties(SharedProperties::stone)
				.transform(CmiStress.setImpact(8.0))
				.item()
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/mechanical_belt_grinder/item")
					);
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

		GEOTHERMAL_GENERATOR = Cmi.REGISTRATE.block("geothermal_generator", GGBlock::new)
				.item(GGItem::new)
				.model(NonNullBiConsumer.noop())
				.build()
				.blockstate(NonNullBiConsumer.noop())
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Machine Blocks Registered!", Cmi.MODID);
	}
}