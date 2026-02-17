package dev.celestiacraft.cmi.common.block.test_gravel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiItems;

public class TestGravelBlockEntity extends BrushableBlockEntity {
	public TestGravelBlockEntity(BlockEntityType<? extends TestGravelBlockEntity> type, BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public boolean brush(long gameTime, @NotNull Player player, @NotNull Direction direction) {
		if (level == null || level.isClientSide()) {
			return false;
		}

		BlockState state = this.getBlockState();
		int current = state.getValue(TestGravelBlock.DUSTED);

		if (current < 3) {
			level.setBlock(worldPosition, state.setValue(TestGravelBlock.DUSTED, current + 1), 3);
		}

		// 设置战利品表
		if (player.getMainHandItem().is(CmiItems.TEST_BRUSH.get())) {
			ResourceLocation customLootTables = Cmi.loadResource("archaeology/custom_brush_loot");
			this.setLootTable(customLootTables, gameTime);
		} else {
			ResourceLocation vanillaLootTables =
					ResourceLocation.withDefaultNamespace("archaeology/desert_pyramid");
			this.setLootTable(vanillaLootTables, gameTime);
		}
		level.setBlock(worldPosition, Blocks.GRAVEL.defaultBlockState(), 3);
		return super.brush(gameTime, player, direction);
	}
}