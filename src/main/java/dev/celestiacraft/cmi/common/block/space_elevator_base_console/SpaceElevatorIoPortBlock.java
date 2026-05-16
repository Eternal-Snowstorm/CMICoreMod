package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpaceElevatorIoPortBlock extends BaseEntityBlock {
	public static final EnumProperty<IoPortType> IO_TYPE = EnumProperty.create("io_type", IoPortType.class);
	public static final EnumProperty<IoPortShape> SHAPE = EnumProperty.create("shape", IoPortShape.class);

	public SpaceElevatorIoPortBlock(Properties properties) {
		super(Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BLOCK)
				.noOcclusion()
				.strength(-1.0F, 3600000.0F)
				.noLootTable()
				.isViewBlocking((s, l, p) -> false)
				.isRedstoneConductor((s, l, p) -> false));
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(IO_TYPE, IoPortType.NONE)
				.setValue(SHAPE, IoPortShape.FULL));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(IO_TYPE, SHAPE);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return CmiBlockEntity.SPACE_ELEVATOR_IO_PORT.get().create(pos, state);
	}

	@Override
	public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		BlockPos controller = getControllerPos(level, pos);
		if (controller == null) {
			return super.getDestroyProgress(state, player, level, pos);
		}
		return level.getBlockState(controller).getDestroyProgress(player, level, controller);
	}

	@Override
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		BlockPos controller = getControllerPos(level, pos);
		if (controller == null) {
			return InteractionResult.PASS;
		}
		BlockState controllerState = level.getBlockState(controller);
		return controllerState.getBlock().use(controllerState, level, controller, player, hand, hit);
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return state.getValue(SHAPE).shape();
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return state.getValue(SHAPE).solidShape();
	}

	@Override
	public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return state.getValue(SHAPE).shape();
	}

	@Override
	public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return state.getValue(SHAPE).solidShape();
	}

	@Override
	public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return Shapes.empty();
	}

	@Nullable
	public static BlockPos getControllerPos(BlockGetter level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof SpaceElevatorIoPortBlockEntity portBe) {
			return portBe.getControllerPos();
		}
		return null;
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean moved) {
		if (!level.isClientSide() && !state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof SpaceElevatorIoPortBlockEntity portBe) {
				BlockPos controllerPos = portBe.getControllerPos();
				if (controllerPos != null && level.getBlockState(controllerPos).getBlock() instanceof SpaceElevatorBaseConsoleBlock) {
					SpaceElevatorBaseConsoleBlock.dismantleStructure(level, controllerPos);
				}
			}
		}
		super.onRemove(state, level, pos, newState, moved);
	}
}
