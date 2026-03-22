package dev.celestiacraft.cmi.common.block.test_coke_oven;

import com.simibubi.create.foundation.block.IBE;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TestCokeOvenIOBlock extends Block implements IBE<TestCokeOvenIOBlockEntity> {

	public TestCokeOvenIOBlock(Properties properties) {
		super(Properties.copy(Blocks.STONE));
		this.registerDefaultState(this.stateDefinition.any());
	}

	@Override
	public Class<TestCokeOvenIOBlockEntity> getBlockEntityClass() {
		return TestCokeOvenIOBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends TestCokeOvenIOBlockEntity> getBlockEntityType() {
		return CmiBlockEntityTypes.TEST_COKE_OVEN_IO.get();
	}

}
