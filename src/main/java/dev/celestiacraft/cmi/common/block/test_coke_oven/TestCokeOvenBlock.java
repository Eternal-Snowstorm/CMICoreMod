package dev.celestiacraft.cmi.common.block.test_coke_oven;

import dev.celestiacraft.cmi.api.register.multiblock.MultiblockControllerBlock;
import dev.celestiacraft.cmi.api.register.multiblock.MultiblockControllerBlockFacing;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TestCokeOvenBlock extends MultiblockControllerBlock<TestCokeOvenBlockEntity> {
	public TestCokeOvenBlock(Properties properties) {
		super(Properties.copy(Blocks.STONE));
	}

	@Override
	protected MultiblockControllerBlockFacing useFacingType() {
		return MultiblockControllerBlockFacing.HORIZONTAL;
	}

	@Override
	public Class<TestCokeOvenBlockEntity> getBlockEntityClass() {
		return TestCokeOvenBlockEntity.class;
	}

	public BlockEntityType<? extends TestCokeOvenBlockEntity> getBlockEntityType() {
		return CmiBlockEntityTypes.TEST_COKE_OVEN.get();
	}
}
