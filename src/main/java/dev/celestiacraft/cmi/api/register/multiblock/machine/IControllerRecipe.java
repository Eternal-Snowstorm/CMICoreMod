package dev.celestiacraft.cmi.api.register.multiblock.machine;

import com.simibubi.create.foundation.block.IBE;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface IControllerRecipe<T extends ControllerBlockEntity> {
	/**
	 * 执行多方块配方逻辑
	 *
	 * <p>
	 * 当多方块结构和IO验证通过后, 由系统在服务端周期性调用,
	 * 用于驱动机器的核心运行逻辑
	 * </p>
	 *
	 * <p>
	 * 调用特性:
	 * </p>
	 * <ul>
	 *     <li>通常每 tick 调用一次(取决于外部调度)</li>
	 *     <li>仅在结构有效时执行</li>
	 *     <li>客户端不应执行任何逻辑</li>
	 * </ul>
	 *
	 * <p>
	 * 上下文内容:
	 * </p>
	 * <ul>
	 *     <li>{@link MultiblockContext#getEntity()} 当前控制器实例</li>
	 *     <li>{@link MultiblockContext#getLevel()} 所在世界</li>
	 *     <li>{@link MultiblockContext#getStack()} 当前处理的物品(可能为空)</li>
	 *     <li>{@link MultiblockContext#getPos()} 当前处理位置(通常为控制器或核心位置)</li>
	 *     <li>{@link MultiblockContext#getState()} 对应方块状态</li>
	 * </ul>
	 *
	 * <p>
	 * 常见用途:
	 * </p>
	 * <ul>
	 *     <li>检测是否满足配方条件</li>
	 * </ul>
	 *
	 * <p>
	 * 实现建议:
	 * </p>
	 * <ul>
	 *     <li>始终首先判断 {@link MultiblockContext#isClient()}</li>
	 * </ul>
	 *
	 * <p>
	 * 注意事项:
	 * </p>
	 * <ul>
	 *     <li>在重写该方法后还需要在本类下重写 {@link #tick(MultiblockContext)} 方法, 详见 {@link #tick(MultiblockContext)}</p></li>
	 *     <li>在重写该方法后还需要在 Block 类下重写 {@link IBE#getTicker(Level, BlockState, BlockEntityType)} 方法调用本类下的 {@link #tick(MultiblockContext)} 方法</li>
	 * </ul>
	 *
	 * <pre>{@code
	 * @Override
	 * protected void recipe(MultiblockContext<T> context) {
	 * }
	 * }</pre>
	 *
	 * @param context 多方块运行上下文, 提供当前 tick 的所有执行信息
	 */
	void recipe(MultiblockContext<T> context);

	/**
	 *
	 * @param context 多方块运行上下文, 提供当前 tick 的所有执行信
	 * @return
	 */
	MultiblockContext<T> tick(MultiblockContext<T> context);
}
