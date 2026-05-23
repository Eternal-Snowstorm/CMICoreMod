package dev.celestiacraft.cmi.common.block.space_elevator_top;

import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpaceElevatorTopBlock extends BaseEntityBlock {
	public SpaceElevatorTopBlock(Properties properties) {
		super(Properties.copy(Blocks.IRON_BLOCK)
				.sound(SoundType.NETHERITE_BLOCK)
				.noOcclusion()
				.dynamicShape()
				.strength(5.0F, 1200.0F));
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CmiBlockEntity.SPACE_ELEVATOR_TOP.get().create(pos, state);
	}

	@Override
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}
		if (SpaceElevatorConstructionHandler.isWrench(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.PASS;
		}

		SpaceElevatorEntity existing = SpaceElevatorConstructionHandler.getNearbyElevator(serverLevel, pos);
		if (existing != null && existing.getFirstPassenger() == null) {
			serverPlayer.startRiding(existing);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}
}
