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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.common.register.ModBlockEntityTypes;

public class AdvancedSpoutBlock extends Block implements IWrenchable, IBE<AdvancedSpoutBlockEntity> {
	public AdvancedSpoutBlock(Properties properties) {
		super(properties);
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
}