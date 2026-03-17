package dev.celestiacraft.cmi.api.register.multiblock;

import dev.celestiacraft.libs.compat.patchouli.multiblock.IMultiblockProvider;
import dev.celestiacraft.libs.compat.patchouli.multiblock.MultiblockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.patchouli.api.IMultiblock;

import java.util.function.Supplier;

/**
 * 多方块控制器 BlockEntity 基类
 *
 * <p>
 * 该类用于统一管理多方块结构的: 
 * </p>
 *
 * <ul>
 * <li>结构构建与缓存({@link MultiblockHandler})</li>
 * <li>结构渲染偏移</li>
 * <li>结构验证缓存周期</li>
 * <li>结构显示的生命周期管理</li>
 * </ul>
 *
 * <p>
 * 子类只需提供结构定义及基础配置, 
 * 无需重复编写 {@link MultiblockHandler} 的初始化逻辑
 * </p>
 *
 * <p>
 * 一般情况下, 你只需要重写以下方法: 
 * </p>
 *
 * <ul>
 * <li>{@link #getMultiblockKey()}</li>
 * <li>{@link #getRenderOffsetY()}(如需要)</li>
 * </ul>
 *
 * <pre>{@code
 * public class ExampleBlockEntity extends MultiblockCtrlerBlockEntity {
 *     public ExampleBlockEntity(...) {
 *         super(type, pos, state, CmiMultiblock.EXAMPLE);
 *     }
 *
 *     @Override
 *     protected String getMultiblockKey() {
 *         return "multiblock.building.xxx.example";
 *     }
 *
 *     @Override
 *     protected int getRenderOffsetY() {
 *         return -1;
 *     }
 * }
 * }</pre>
 */
public abstract class MultiblockControllerBlockEntity extends BlockEntity implements IMultiblockProvider {
	/**
	 * 多方块处理器
	 *
	 * <p>
	 * 负责: 
	 * </p>
	 *
	 * <ul>
	 * <li>结构匹配</li>
	 * <li>缓存验证结果</li>
	 * <li>客户端结构显示</li>
	 * </ul>
	 */
	protected final MultiblockHandler multiblock;

	/**
	 * 构造方法
	 *
	 * <p>
	 * 内部会自动创建 {@link MultiblockHandler}, 
	 * 并应用以下配置: 
	 * </p>
	 * <ul>
	 * <li>{@link #getMultiblockKey()}</li>
	 * <li>{@link #getRenderOffsetX()}</li>
	 * <li>{@link #getRenderOffsetY()}</li>
	 * <li>{@link #getRenderOffsetZ()}</li>
	 * <li>{@link #getCacheTicks()}</li>
	 * </ul>
	 *
	 * @param type      BlockEntity 类型
	 * @param pos       方块位置
	 * @param state     方块状态
	 * @param structure 多方块结构提供器
	 */
	protected MultiblockControllerBlockEntity(
			BlockEntityType<?> type,
			BlockPos pos,
			BlockState state,
			Supplier<IMultiblock> structure
	) {
		super(type, pos, state);

		this.multiblock = MultiblockHandler.builder(this, structure)
				.translationKey(getMultiblockKey())
				.renderOffset(getRenderOffsetX(), getRenderOffsetY(), getRenderOffsetZ())
				.cacheTicks(getCacheTicks())
				.build();
	}

	/**
	 * 获取多方块处理器
	 *
	 * <p>
	 * 一般不需要重写该方法
	 * </p>
	 */
	@Override
	public MultiblockHandler getMultiblockHandler() {
		return multiblock;
	}

	/**
	 * BlockEntity 移除时调用
	 *
	 * <p>
	 * 自动取消多方块结构显示, 
	 * 防止客户端残留渲染
	 * </p>
	 *
	 * <p>
	 * 子类通常不需要重写该方法
	 * </p>
	 */
	@Override
	public void setRemoved() {
		cancelShowMultiblock();
		super.setRemoved();
	}

	/**
	 * 获取多方块翻译键
	 *
	 * <p>
	 * 用于 Patchouli 显示结构名称
	 * </p>
	 *
	 * <p>
	 * 子类必须实现该方法
	 * </p>
	 *
	 * <pre>{@code
	 * @Override
	 * protected String getMultiblockKey() {
	 *     return "multiblock.building.modid.example";
	 * }
	 * }</pre>
	 */
	protected abstract String getMultiblockKey();

	/**
	 * 渲染偏移 X
	 *
	 * <p>
	 * 默认返回 {@code 0}
	 * </p>
	 */
	protected int getRenderOffsetX() {
		return 0;
	}

	/**
	 * 渲染偏移 Y
	 *
	 * <p>
	 * 默认返回 {@code 0}
	 * </p>
	 *
	 * <p>
	 * 常用于结构整体上移/下移
	 * </p>
	 *
	 * <pre>{@code
	 * @Override
	 * protected int getRenderOffsetY() {
	 *     return -1;
	 * }
	 * }</pre>
	 */
	protected int getRenderOffsetY() {
		return 0;
	}

	/**
	 * 渲染偏移 Z
	 *
	 * <p>
	 * 默认返回 {@code 0}
	 * </p>
	 */
	protected int getRenderOffsetZ() {
		return 0;
	}

	/**
	 * 结构缓存 tick 数
	 *
	 * <p>
	 * 控制结构验证结果的缓存时间
	 * </p>
	 *
	 * <p>
	 * 默认返回 {@code 20}(1秒)
	 * </p>
	 *
	 * <p>
	 * 数值越大性能越好, 但结构响应越慢
	 * </p>
	 *
	 * <pre>{@code
	 * @Override
	 * protected int getCacheTicks() {
	 *     return 10;
	 * }
	 * }</pre>
	 */
	protected int getCacheTicks() {
		return 20;
	}
}