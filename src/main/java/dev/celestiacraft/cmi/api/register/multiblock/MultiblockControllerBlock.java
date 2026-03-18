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
 * 作为多方块结构的核心控制方块, 负责交互入口与基础行为封装
 * </p>
 *
 * <p>
 * 提供功能:
 * </p>
 * <ul>
 *     <li>使用触发器(默认扳手)右键显示多方块结构预览</li>
 *     <li>自动获取并绑定 {@link BlockEntity}(基于 {@link IBE})</li>
 *     <li>内置方向系统(支持无方向 / 水平 / 六面)</li>
 *     <li>统一处理旋转与镜像逻辑</li>
 * </ul>
 *
 * <p>
 * 扩展点:
 * </p>
 * <ul>
 *     <li>{@link #useFacingType()}: 定义方向类型</li>
 *     <li>{@link #isTrigger(UseContext)}: 自定义触发工具</li>
 * </ul>
 *
 * <p>
 * 子类通常只需:
 * </p>
 * <ul>
 *     <li>实现对应的 {@link BlockEntity}</li>
 *     <li>选择合适的方向类型</li>
 * </ul>
 *
 * <p>
 * 无需手动处理事件注册或基础交互逻辑
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
	 * 定义该控制器使用的方向类型
	 *
	 * <p>
	 * 用于决定:
	 * </p>
	 * <ul>
	 *     <li>是否具有方向属性</li>
	 *     <li>使用哪种方向属性(六面 / 水平)</li>
	 *     <li>结构旋转与匹配的方式</li>
	 * </ul>
	 *
	 * <p>
	 * 可选类型:
	 * </p>
	 * <ul>
	 *     <li>{@code NONE}: 无方向(默认)</li>
	 *     <li>{@code HORIZONTAL}: 仅水平方向(N/E/S/W)</li>
	 *     <li>{@code FACING}: 六面方向(包含上下)</li>
	 * </ul>
	 *
	 * <p>
	 * 行为影响:
	 * </p>
	 * <ul>
	 *     <li>自动注册对应的 {@link Property}</li>
	 *     <li>控制 {@link #getStateForPlacement(BlockPlaceContext)} 的朝向逻辑</li>
	 *     <li>影响 {@link #rotate(BlockState, Rotation)} 与 {@link #mirror(BlockState, Mirror)}</li>
	 *     <li>用于多方块结构的方向对齐</li>
	 * </ul>
	 *
	 * <p>
	 * 子类通常应根据机器类型选择:
	 * </p>
	 * <ul>
	 *     <li>简单机器: {@code HORIZONTAL}</li>
	 *     <li>立体结构 / 可朝上放置: {@code FACING}</li>
	 *     <li>纯逻辑控制器: {@code NONE}</li>
	 * </ul>
	 *
	 * <pre>{@code
	 * @Override
	 * protected MultiblockControllerBlockFacing useFacingType() {
	 *     return MultiblockControllerBlockFacing.HORIZONTAL;
	 * }
	 * }</pre>
	 *
	 * @return 使用的方向类型, 默认 {@code NONE}
	 */
	protected MultiblockControllerBlockFacing useFacingType() {
		return MultiblockControllerBlockFacing.NONE;
	}

	/**
	 * 获取当前 Block 使用的方向属性
	 *
	 * <p>
	 * 根据 {@link #useFacingType()} 返回对应的 {@link Property}:
	 * </p>
	 *
	 * <ul>
	 *     <li>{@code FACING} => {@link BlockStateProperties#FACING}</li>
	 *     <li>{@code HORIZONTAL} => {@link BlockStateProperties#HORIZONTAL_FACING}</li>
	 *     <li>{@code NONE} => {@code null}</li>
	 * </ul>
	 *
	 * <p>
	 * 该方法主要用于:
	 * </p>
	 * <ul>
	 *     <li>注册 BlockState 属性</li>
	 *     <li>读取或写入朝向</li>
	 * </ul>
	 *
	 * @return 当前使用的方向属性, 若未启用方向则返回 {@code null}
	 */
	protected Property<Direction> getFacingProperty() {
		return switch (useFacingType()) {
			case FACING -> BlockStateProperties.FACING;
			case HORIZONTAL -> BlockStateProperties.HORIZONTAL_FACING;
			default -> null;
		};
	}

	/**
	 * 从 BlockState 中获取当前朝向
	 *
	 * <p>
	 * 若未启用方向属性或属性不存在, 则默认返回 {@link Direction#NORTH}
	 * </p>
	 *
	 * @param state 方块状态
	 * @return 当前朝向(保证非空)
	 */
	protected Direction getFacing(BlockState state) {
		Property<Direction> property = getFacingProperty();

		if (property == null || !state.hasProperty(property)) {
			return Direction.NORTH;
		}

		return state.getValue(property);
	}

	/**
	 * 注册 BlockState 属性
	 *
	 * <p>
	 * 自动根据 {@link #useFacingType()} 注册方向属性
	 * </p>
	 *
	 * <p>
	 * 子类一般无需重写, 除非需要额外属性
	 * </p>
	 *
	 * @param builder 状态构建器
	 */
	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);

		Property<Direction> property = getFacingProperty();
		if (property != null) {
			builder.add(property);
		}
	}

	/**
	 * 方块放置时的状态初始化
	 *
	 * <p>
	 * 默认行为:
	 * </p>
	 * <ul>
	 *     <li>水平模式: 朝向玩家反方向</li>
	 *     <li>六面模式:
	 *         <ul>
	 *             <li>正常: 水平反方向</li>
	 *             <li>潜行: 使用玩家视线方向</li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 *
	 * @param context 放置上下文
	 * @return 初始化后的 BlockState
	 */
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

	/**
	 * 旋转方块状态
	 *
	 * <p>
	 * 自动对方向属性应用 {@link Rotation}
	 * </p>
	 *
	 * @param state    原状态
	 * @param rotation 旋转类型
	 * @return 旋转后的状态
	 */
	@Override
	public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
		Property<Direction> property = getFacingProperty();

		if (property == null || !state.hasProperty(property)) {
			return state;
		}

		return state.setValue(property, rotation.rotate(state.getValue(property)));
	}

	/**
	 * 镜像方块状态
	 *
	 * <p>
	 * 实际通过 {@link #rotate(BlockState, Rotation)} 实现
	 * </p>
	 *
	 * @param state  原状态
	 * @param mirror 镜像类型
	 * @return 镜像后的状态
	 */
	@Override
	public @NotNull BlockState mirror(@NotNull BlockState state, Mirror mirror) {
		return rotate(state, mirror.getRotation(getFacing(state)));
	}

	/**
	 * 获取用于多方块结构匹配的方向属性
	 *
	 * <p>
	 * 与 {@link #getFacingProperty()} 类似, 但语义上用于结构系统
	 * </p>
	 *
	 * <p>
	 * 主要用于:
	 * </p>
	 * <ul>
	 *     <li>多方块结构旋转</li>
	 *     <li>结构匹配方向对齐</li>
	 * </ul>
	 *
	 * @return 结构使用的方向属性
	 */
	public Property<Direction> getFacingPropertyForStructure() {
		return switch (useFacingType()) {
			case FACING -> BlockStateProperties.FACING;
			case HORIZONTAL -> BlockStateProperties.HORIZONTAL_FACING;
			default -> null;
		};
	}

	/**
	 * 从 Block 实例中提取方向属性(用于结构系统)
	 *
	 * <p>
	 * 若 Block 为 {@link MultiblockControllerBlock}, 则返回其方向属性
	 * </p>
	 *
	 * @param block 方块实例
	 * @return 对应方向属性, 否则为 {@code null}
	 */
	public static Property<Direction> getFacingProperty(Block block) {
		if (block instanceof MultiblockControllerBlock<?> multiblockControllerBlock) {
			return multiblockControllerBlock.getFacingPropertyForStructure();
		}
		return null;
	}

	protected abstract MultiblockControllerBlockFacing useuseFacingType();
}