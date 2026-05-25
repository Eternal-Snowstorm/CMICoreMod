package dev.celestiacraft.cmi.common.block.test_coke_oven;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.block.BasicBlockFacing;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import dev.celestiacraft.libs.api.register.multiblock.machine.MultiblockContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TestCokeOvenBlock extends ControllerBlock<TestCokeOvenBlockEntity> {
	public TestCokeOvenBlock(Properties properties) {
		super(Properties.copy(Blocks.STONE));
	}

	@Override
	public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> type) {
		if (level.isClientSide()) {
			return null;
		}

		return createTickerHelper(
				type,
				CmiBlockEntity.TEST_COKE_OVEN.get(),
				(lvl, pos, st, be) -> {
					be.tick(MultiblockContext.of(be));
				});
	}

	@Override
	protected BasicBlockFacing useFacingType() {
		return BasicBlockFacing.HORIZONTAL;
	}

	@Override
	public Class<TestCokeOvenBlockEntity> getBlockEntityClass() {
		return TestCokeOvenBlockEntity.class;
	}

	public BlockEntityType<? extends TestCokeOvenBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.TEST_COKE_OVEN.get();
	}
}
