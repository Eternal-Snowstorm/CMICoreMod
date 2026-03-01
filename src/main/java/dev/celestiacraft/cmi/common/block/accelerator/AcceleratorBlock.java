package dev.celestiacraft.cmi.common.block.accelerator;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
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
				Shapes.box(1 / 16D, 1 / 16D, 1 / 16D, 15 / 16D, 15 / 16D, 15 / 16D),
				Shapes.box(0, 0, 0, 1, 1, 2 / 16D),
				Shapes.box(0, 0, 14 / 16D, 1, 1, 1),
				Shapes.box(0, 0, 2 / 16D, 2 / 16D, 1, 14 / 16D),
				Shapes.box(14 / 16D, 0, 2 / 16D, 1, 1, 14 / 16D)
		);
	}
}