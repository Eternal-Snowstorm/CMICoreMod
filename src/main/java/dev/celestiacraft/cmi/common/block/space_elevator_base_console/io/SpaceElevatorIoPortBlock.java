package dev.celestiacraft.cmi.common.block.space_elevator_base_console.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.particle.AstronauticDestructionParticles;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlock;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpaceElevatorIoPortBlock extends BasicBlock implements IBE<SpaceElevatorIoPortBlockEntity> {
	public static final EnumProperty<IoPortType> IO_TYPE = EnumProperty.create("io_type", IoPortType.class);
	public static final EnumProperty<IoPortShape> SHAPE = EnumProperty.create("shape", IoPortShape.class);

	public SpaceElevatorIoPortBlock(Properties properties) {
		super(Properties.copy(Blocks.IRON_BLOCK)
				.noOcclusion()
				.strength(-1.0F, 3600000.0F)
				.noLootTable()
				.isViewBlocking((state, getter, pos) -> {
					return false;
				})
				.isRedstoneConductor((state, getter, pos) -> {
					return false;
				}));
		registerDefaultState(stateDefinition.any()
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

	@Override
	public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		BlockPos controller = getControllerPos(level, pos);
		if (controller == null) {
			return super.getDestroyProgress(state, player, level, pos);
		}
		return level.getBlockState(controller).getDestroyProgress(player, level, controller);
	}

	// 右键端口时把交互转发给控制器方块
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockPos controller = getControllerPos(level, pos);
		if (controller == null) {
			return InteractionResult.PASS;
		}
		BlockState controllerState = level.getBlockState(controller);
		return controllerState.getBlock().use(controllerState, level, controller, player, hand, hit);
	}

	// 玩家破坏接口方块时立即生成破坏粒子
	@Override
	public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
		super.playerWillDestroy(level, pos, state, player);
		AstronauticDestructionParticles.spawnDirect(level, pos, state);
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return state.getValue(SHAPE).shape(facing(level, pos));
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return state.getValue(SHAPE).solidShape(facing(level, pos));
	}

	@Override
	public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return state.getValue(SHAPE).shape(facing(level, pos));
	}

	@Override
	public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return state.getValue(SHAPE).solidShape(facing(level, pos));
	}

	@Override
	public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return Shapes.empty();
	}

	// 端口形状跟随控制器朝向, 无朝向的控制器按朝南处理
	private static Direction facing(@NotNull BlockGetter level, @NotNull BlockPos pos) {
		BlockPos controller = getControllerPos(level, pos);
		if (controller == null) {
			return Direction.SOUTH;
		}
		return GeneralAstronauticPlatformBlock.getPlatformFacing(level, controller);
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
				if (controllerPos != null) {
					Block controllerBlock = level.getBlockState(controllerPos).getBlock();
					if (controllerBlock instanceof SpaceElevatorBaseConsoleBlock) {
						SpaceElevatorBaseConsoleBlock.dismantleStructure(level, controllerPos);
					} else if (controllerBlock instanceof GeneralAstronauticPlatformBlock) {
						GeneralAstronauticPlatformBlock.dismantleStructure(level, controllerPos);
					}
				}
			}
		}
		super.onRemove(state, level, pos, newState, moved);
	}

	@Override
	public Class<SpaceElevatorIoPortBlockEntity> getBlockEntityClass() {
		return SpaceElevatorIoPortBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SpaceElevatorIoPortBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.SPACE_ELEVATOR_IO_PORT.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.simpleBlock(
					context.get(),
					provider.models()
							.withExistingParent(
									context.getName(), provider.mcLoc("block/block"))
							.texture("particle", Cmi.loadResource("block/astronautic_particles"))
			);
		};
	}
}
