package dev.celestiacraft.cmi.common.block.test_multiblock;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlockFacing;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TestMultiblockBlock extends ControllerBlock<TestMultiblockBlockEntity> {
	public TestMultiblockBlock(Properties properties) {
		super(Properties.copy(Blocks.STONE));
	}

	@Override
	protected ControllerBlockFacing useFacingType() {
		return ControllerBlockFacing.HORIZONTAL;
	}

	@Override
	public Class<TestMultiblockBlockEntity> getBlockEntityClass() {
		return TestMultiblockBlockEntity.class;
	}

	public BlockEntityType<? extends TestMultiblockBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.TEST_MULTIBLOCK.get();
	}
}