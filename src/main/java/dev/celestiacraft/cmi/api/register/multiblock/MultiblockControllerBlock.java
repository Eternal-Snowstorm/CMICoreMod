package dev.celestiacraft.cmi.api.register.multiblock;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.block.IBE;
import dev.celestiacraft.cmi.api.register.block.BaseBlock;
import dev.celestiacraft.cmi.api.interaction.UseContext;
import dev.celestiacraft.libs.compat.patchouli.multiblock.IMultiblockProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;

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
	}

	/**
	 * 右键交互
	 *
	 * <p>
	 * 默认行为:
	 * 使用触发器右键 => 显示多方块结构
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
}