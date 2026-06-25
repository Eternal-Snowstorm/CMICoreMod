package dev.celestiacraft.cmi.common.block.accelerator;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import org.jetbrains.annotations.NotNull;

public class AcceleratorBlock extends Block {
	public AcceleratorBlock(Properties properties) {
		super(properties.sound(SoundType.METAL)
				.strength(4, 4)
				.requiresCorrectToolForDrops());
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.or(
				Block.box(1, 1, 1, 15, 15, 15),
				Block.box(0, 0, 0, 16, 16, 2),
				Block.box(0, 0, 14, 16, 16, 16),
				Block.box(0, 0, 2, 2, 16, 14),
				Block.box(14, 0, 2, 16, 16, 14)
		);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						BlockModelProvider models = provider.models();
						return ConfiguredModel.builder()
								.modelFile(models.getExistingFile(provider.modLoc("block/accelerator")))
								.build();
					});
		};
	}
}