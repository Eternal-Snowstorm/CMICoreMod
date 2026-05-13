package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
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

import java.util.HashMap;
import java.util.Map;

public class SpaceElevatorBaseConsoleBlock extends BaseEntityBlock {
	public SpaceElevatorBaseConsoleBlock(Properties properties) {
		super(Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BLOCK)
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
		return CmiBlockEntity.SPACE_ELEVATOR_BASE_CONSOLE.get().create(pos, state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		for (Vec3i offset : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.keySet()) {
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos check = clickedPos.offset(offset);
			if (!level.getWorldBorder().isWithinBounds(check)) {
				return null;
			}
			BlockState existing = level.getBlockState(check);
			if (existing.getBlock() instanceof SpaceElevatorIoPortBlock) {
				continue;
			}
			if (!existing.canBeReplaced(context)) {
				return null;
			}
		}
		return this.defaultBlockState();
	}

	@Override
	public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide()) {
			deployStructure(level, pos);
		}
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean moved) {
		if (!level.isClientSide() && !state.is(newState.getBlock())) {
			dismantlePorts(level, pos);
		}
		super.onRemove(state, level, pos, newState, moved);
	}

	public static void ensureStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		int missing = 0;
		for (Vec3i offset : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.keySet()) {
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			if (!(level.getBlockState(portPos).getBlock() instanceof SpaceElevatorIoPortBlock)) {
				missing++;
			}
		}
		if (missing > 0) {
			deployStructure(level, controllerPos);
		}
	}

	public static void deployStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		Map<Vec3i, IoPortType> ioByOffset = new HashMap<>();
		for (Map.Entry<IoPortType, Vec3i> entry : SpaceElevatorBaseStructure.IO_PORT_OFFSETS.entrySet()) {
			ioByOffset.put(entry.getValue(), entry.getKey());
		}

		for (Map.Entry<Vec3i, IoPortShape> entry : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.entrySet()) {
			Vec3i offset = entry.getKey();
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			BlockState existing = level.getBlockState(portPos);
			if (!(existing.getBlock() instanceof SpaceElevatorIoPortBlock) && !existing.canBeReplaced()) {
				continue;
			}
			IoPortType ioType = ioByOffset.getOrDefault(offset, IoPortType.NONE);
			BlockState portState = CmiBlock.SPACE_ELEVATOR_IO_PORT.getDefaultState()
					.setValue(SpaceElevatorIoPortBlock.IO_TYPE, ioType)
					.setValue(SpaceElevatorIoPortBlock.SHAPE, entry.getValue());
			level.setBlock(portPos, portState, 3);
			if (level.getBlockEntity(portPos) instanceof SpaceElevatorIoPortBlockEntity portBe) {
				portBe.setControllerPos(controllerPos);
			}
		}
	}

	public static void dismantleStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		dismantlePorts(level, controllerPos);
		if (level.getBlockState(controllerPos).getBlock() instanceof SpaceElevatorBaseConsoleBlock) {
			level.removeBlock(controllerPos, false);
		}
	}

	private static void dismantlePorts(@NotNull Level level, @NotNull BlockPos controllerPos) {
		for (Vec3i offset : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.keySet()) {
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			if (level.getBlockState(portPos).getBlock() instanceof SpaceElevatorIoPortBlock) {
				level.removeBlock(portPos, false);
			}
		}
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
