package dev.celestiacraft.cmi.common.block.test_gravel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;

public class TestGravelBlock extends BaseEntityBlock implements EntityBlock {
	public TestGravelBlock(Properties properties) {
		super(Properties.copy(Blocks.GRAVEL));
		this.registerDefaultState(this.stateDefinition.any().setValue(DUSTED, 0));
	}

	public static final IntegerProperty DUSTED = IntegerProperty.create("dusted", 0, 3);

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DUSTED);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new TestGravelBlockEntity(CmiBlockEntityTypes.TEST_GRAVEL.get(), pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return null;
	}
}