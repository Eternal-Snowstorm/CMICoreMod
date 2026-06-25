package dev.celestiacraft.cmi.common.block.accelerator_motor;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;

public class AcceleratorMotorBlock extends DirectionalKineticBlock implements IBE<AcceleratorMotorBlockEntity> {
	public AcceleratorMotorBlock(Properties properties) {
		super(properties.mapColor(MapColor.DIRT)
				.strength(5.0F, 6.0F)
				.sound(SoundType.STONE));
	}

	@Override
	public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return AllShapes.MOTOR_BLOCK.get(state.getValue(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction preferred = getPreferredFacing(context);
		if ((context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) || preferred == null) {
			return super.getStateForPlacement(context);
		}
		return defaultBlockState().setValue(FACING, preferred);
	}

	@Override
	public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction direction) {
		return direction == state.getValue(FACING);
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}

	@Override
	public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull PathComputationType type) {
		return false;
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}

	@Override
	public Class<AcceleratorMotorBlockEntity> getBlockEntityClass() {
		return AcceleratorMotorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends AcceleratorMotorBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.ACCELERATOR_MOTOR.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
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
		};
	}
}