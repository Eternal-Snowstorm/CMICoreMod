package top.nebula.cmi.common.block.advanced_spout;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.common.register.ModBlockEntityTypes;

public class AdvancedSpoutBlock extends Block implements IWrenchable, IBE<AdvancedSpoutBlockEntity> {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final VoxelShape SUPPORT_SHAPE = Shapes.join(
			Shapes.block(),
			Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D),
			BooleanOp.ONLY_FIRST
	);

	public AdvancedSpoutBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
		if (!level.isClientSide) {
			boolean powered = level.hasNeighborSignal(pos);
			if (state.getValue(POWERED) != powered) {
				level.setBlock(pos, state.setValue(POWERED, powered), 2);
			}
		}
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return AllShapes.SPOUT;
	}

	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos) {
		return ComparatorUtil.levelOfSmartFluidTank(worldIn, pos);
	}

	@Override
	public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull PathComputationType type) {
		return false;
	}

	@Override
	public Class<AdvancedSpoutBlockEntity> getBlockEntityClass() {
		return AdvancedSpoutBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends AdvancedSpoutBlockEntity> getBlockEntityType() {
		return ModBlockEntityTypes.ADVANCED_SPOUT.get();
	}
	@Override
	public @NotNull VoxelShape getBlockSupportShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return SUPPORT_SHAPE;
	}
}