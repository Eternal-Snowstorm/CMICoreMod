package dev.celestiacraft.cmi.api.register.multiblock;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.block.IBE;
import dev.celestiacraft.cmi.api.interaction.UseContext;
import dev.celestiacraft.cmi.api.register.block.BaseBlock;
import dev.celestiacraft.libs.compat.patchouli.multiblock.IMultiblockProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

/**
 * 多方块控制器 Block 基类
 *
 * <p>
 * 提供:
 * </p>
 *
 * <ul>
 * <li>扳手右键显示多方块结构</li>
 * <li>自动获取 BlockEntity(基于 {@link IBE})</li>
 * </ul>
 *
 * <p>
 * 子类无需处理事件注册, 只需继承即可
 * </p>
 */
public abstract class MultiblockControllerBlock<T extends BlockEntity & IMultiblockProvider> extends BaseBlock implements IBE<T> {
	public MultiblockControllerBlock(Properties properties) {
		super(properties);

		Property<Direction> property = getFacingProperty();
		if (property != null) {
			this.registerDefaultState(this.defaultBlockState().setValue(property, Direction.NORTH));
		}
	}

	/**
	 * 右键交互
	 *
	 * <p>
	 * 默认行为:
	 * 使用触发器右键后显示多方块结构
	 * </p>
	 */
	@Override
	protected InteractionResult useOn(UseContext context) {
		if (!context.isClient()) {
			return InteractionResult.PASS;
		}

		if (isTrigger(context) && context.getHand() == InteractionHand.MAIN_HAND && !context.getPlayer().isShiftKeyDown()) {
			T be = getBlockEntity(context.getLevel(), context.getPos());

			if (be != null) {
				context.getPlayer().swing(context.getHand());
				be.showMultiblock();
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	/**
	 * 是否为触发器
	 *
	 * <p>
	 * 默认使用 Create 的扳手标签
	 * </p>
	 *
	 * <p>
	 * 子类可重写以支持自定义工具
	 * </p>
	 */
	protected boolean isTrigger(UseContext context) {
		return context.getItem().is(AllTags.AllItemTags.WRENCH.tag);
	}

	/**
	 * 用于定义核心方块的方向属性
	 * <p>
	 * 只提供{@link MultiblockControllerBlockFacing}内的{@code NONE}, {@code FACING} 和 {@code HORIZONTAL} 三种方向属性
	 * </p>
	 * <pre>{@code
	 * @Override
	 * protected FacingType useFacingType() {
	 *     return FacingType.FACING;
	 * }
	 * }
	 *
	 * <p>
	 * 默认不使用方向属性
	 * </p>
	 *
	 * @return 方向属性
	 */
	protected MultiblockControllerBlockFacing useFacingType() {
		return MultiblockControllerBlockFacing.NONE;
	}

	protected Property<Direction> getFacingProperty() {
		return switch (useFacingType()) {
			case FACING -> BlockStateProperties.FACING;
			case HORIZONTAL -> BlockStateProperties.HORIZONTAL_FACING;
			default -> null;
		};
	}

	protected Direction getFacing(BlockState state) {
		Property<Direction> property = getFacingProperty();

		if (property == null || !state.hasProperty(property)) {
			return Direction.NORTH;
		}

		return state.getValue(property);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);

		Property<Direction> property = getFacingProperty();
		if (property != null) {
			builder.add(property);
		}
	}

	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);

		Property<Direction> property = getFacingProperty();
		if (property == null) {
			return state;
		}

		Direction facing = context.getHorizontalDirection().getOpposite();

		if (useFacingType() == MultiblockControllerBlockFacing.FACING) {
			if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
				facing = context.getNearestLookingDirection().getOpposite();
			}
		}

		return state.setValue(property, facing);
	}

	@Override
	public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
		Property<Direction> property = getFacingProperty();

		if (property == null || !state.hasProperty(property)) {
			return state;
		}

		return state.setValue(property, rotation.rotate(state.getValue(property)));
	}

	@Override
	public @NotNull BlockState mirror(@NotNull BlockState state, Mirror mirror) {
		return rotate(state, mirror.getRotation(getFacing(state)));
	}

	public Property<Direction> getFacingPropertyForStructure() {
		return switch (useFacingType()) {
			case FACING -> BlockStateProperties.FACING;
			case HORIZONTAL -> BlockStateProperties.HORIZONTAL_FACING;
			default -> null;
		};
	}

	public static Property<Direction> getFacingProperty(Block block) {
		if (block instanceof MultiblockControllerBlock<?> multiblockControllerBlock) {
			return multiblockControllerBlock.getFacingPropertyForStructure();
		}
		return null;
	}
}