package dev.celestiacraft.cmi.common.register.block;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenBlock;
import dev.celestiacraft.cmi.common.block.test_coke_oven.TestCokeOvenIOBlock;
import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlock;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlockItem;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class MultiblockBlocks {
	public static final BlockEntry<TestMultiblockBlock> TEST_MULTIBLOCK;
	public static final BlockEntry<TestCokeOvenBlock> TEST_COKE_OVEN;
	public static final BlockEntry<TestCokeOvenIOBlock> TEST_COKE_OVEN_IO;

	static {
		TEST_MULTIBLOCK = Cmi.REGISTRATE.block("test_multiblock_controller", TestMultiblockBlock::new)
				.initialProperties(SharedProperties::stone)
				.item(ControllerBlockItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/mechanical_belt_grinder/item")
					);
				})
				.build()
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/void_dust_collector/on")))
										.build();
							});
				})
				.register();

		TEST_COKE_OVEN = Cmi.REGISTRATE.block("test_coke_oven", TestCokeOvenBlock::new)
				.initialProperties(SharedProperties::stone)
				.item(ControllerBlockItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc("block/coke_oven/coke_oven_controller")
					);
				})
				.build()
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/coke_oven/coke_oven_controller")))
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();

		TEST_COKE_OVEN_IO = Cmi.REGISTRATE.block("test_coke_oven_io", TestCokeOvenIOBlock::new)
				.initialProperties(SharedProperties::stone)
				.item()
				.build()
				.blockstate((context, provider) -> {
					provider.getVariantBuilder(context.get())
							.forAllStatesExcept((state) -> {
								BlockModelProvider models = provider.models();
								return ConfiguredModel.builder()
										.modelFile(models.getExistingFile(provider.modLoc("block/coke_oven/coke_oven_io")))
										.build();
							});
				})
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Multiblocks Blocks Registered!", Cmi.MODID);
	}
}