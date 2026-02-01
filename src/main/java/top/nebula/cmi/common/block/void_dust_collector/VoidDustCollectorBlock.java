package top.nebula.cmi.common.block.void_dust_collector;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import top.nebula.cmi.common.register.ModBlockEntityTypes;

public class VoidDustCollectorBlock extends Block implements IBE<VoidDustCollectorBlockEnitiy> {
	public static final BooleanProperty WORKING = BooleanProperty.create("working");

	public VoidDustCollectorBlock(Properties properties) {
		super(Properties.copy(Blocks.IRON_BLOCK));
		this.registerDefaultState(this.stateDefinition.any().setValue(WORKING, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WORKING);
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