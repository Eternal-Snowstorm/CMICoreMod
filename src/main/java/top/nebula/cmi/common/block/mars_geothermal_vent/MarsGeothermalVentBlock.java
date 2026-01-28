package top.nebula.cmi.common.block.mars_geothermal_vent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.nebula.cmi.common.register.ModBlockEntityTypes;

public class MarsGeothermalVentBlock extends BaseEntityBlock {
	public static final IntegerProperty SMOKE_TYPE = IntegerProperty.create("smoke_type", 0, 3);
	public static final BooleanProperty SPAWNING_PARTICLES = BooleanProperty.create("spawning_particles");

	public MarsGeothermalVentBlock(Properties properties) {
		super(Properties.of()
				.mapColor(MapColor.STONE)
				.requiresCorrectToolForDrops()
				.strength(2.0F, 5.0F)
				.sound(SoundType.TUFF));
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(SMOKE_TYPE, Integer.valueOf(0))
				.setValue(SPAWNING_PARTICLES, true));
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		LevelAccessor accessor = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		return this.defaultBlockState()
				.setValue(SMOKE_TYPE, getSmokeType(accessor, blockpos))
				.setValue(SPAWNING_PARTICLES, isSpawningParticles(blockpos, accessor));
	}

	public @NotNull BlockState updateShape(BlockState state, @NotNull Direction direction, @NotNull BlockState state1, @NotNull LevelAccessor accessor, @NotNull BlockPos pos, @NotNull BlockPos pos1) {
		return state.setValue(SMOKE_TYPE, getSmokeType(accessor, pos))
				.setValue(SPAWNING_PARTICLES, isSpawningParticles(pos, accessor));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SMOKE_TYPE, SPAWNING_PARTICLES);
	}

	public int getSmokeType(LevelAccessor level, BlockPos pos) {
		BlockState state = level.getBlockState(pos.below());
		if (state.getBlock() instanceof MarsGeothermalVentBlock) {
			return state.getValue(SMOKE_TYPE);
		}
		if (state.getFluidState().getFluidType() == Fluids.LAVA.getFluidType()) {
			return 3;
		} else if (state.getFluidState().is(FluidTags.WATER)) {
			return 1;
		} else if (state.getFluidState().is(FluidTags.LAVA)) {
			return 2;
		}
		return 0;
	}

	public boolean isSpawningParticles(BlockPos pos, LevelAccessor level) {
		BlockState above = level.getBlockState(pos.above());
		return (above.isAir() || !above.blocksMotion());
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return state.getValue(SMOKE_TYPE) > 0 && state.getValue(SPAWNING_PARTICLES) ?
					createTickerHelper(type, ModBlockEntityTypes.MARS_GEO.get(),
							MarsGeothermalVentBlockEntity::particleTick) : null;
		} else {
			return null;
		}
	}

	public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new MarsGeothermalVentBlockEntity(ModBlockEntityTypes.MARS_GEO.get(), pos, state);
	}
}