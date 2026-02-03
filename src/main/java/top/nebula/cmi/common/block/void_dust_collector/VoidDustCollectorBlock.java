package top.nebula.cmi.common.block.void_dust_collector;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.common.register.ModBlockEntityTypes;

public class VoidDustCollectorBlock extends Block implements IBE<VoidDustCollectorBlockEnitiy> {
	public static final BooleanProperty WORKING = BooleanProperty.create("working");
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public VoidDustCollectorBlock(Properties properties) {
		super(Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.NETHERITE_BLOCK));
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(WORKING, false)
				.setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WORKING, FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return null;
		}

		return (lvl, pos, st, be) -> {
			if (be instanceof VoidDustCollectorBlockEnitiy entity) {
				VoidDustCollectorBlockEnitiy.tick(lvl, pos, st, entity);
			}
		};
	}

	@Override
	public Class<VoidDustCollectorBlockEnitiy> getBlockEntityClass() {
		return VoidDustCollectorBlockEnitiy.class;
	}

	@Override
	public BlockEntityType<? extends VoidDustCollectorBlockEnitiy> getBlockEntityType() {
		return ModBlockEntityTypes.VOID_DUST_COLLECTOR.get();
	}
}