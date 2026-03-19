package dev.celestiacraft.cmi.common.block.test_coke_oven;

import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlock;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockFacing;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;
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

		return (lvl, pos, st, be) -> {
			if (be instanceof TestCokeOvenBlockEntity entity) {
				TestCokeOvenBlockEntity.tick(lvl, pos, st, entity);
			}
		};
	}

	@Override
	protected ControllerBlockFacing useFacingType() {
		return ControllerBlockFacing.HORIZONTAL;
	}

	@Override
	public Class<TestCokeOvenBlockEntity> getBlockEntityClass() {
		return TestCokeOvenBlockEntity.class;
	}

	public BlockEntityType<? extends TestCokeOvenBlockEntity> getBlockEntityType() {
		return CmiBlockEntityTypes.TEST_COKE_OVEN.get();
	}
}
