package dev.celestiacraft.cmi.common.item;

import dev.celestiacraft.libs.api.register.item.BasicItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 构件物品基类
 *
 * <p>
 * 该类封装了构件类物品的通用行为, 大致包括:
 * </p>
 *
 * <ul>
 *     <li>右键空气使用逻辑</li>
 *     <li>右键方块使用逻辑</li>
 *     <li>自动挥手动画</li>
 *     <li>自动冷却处理</li>
 *     <li>自动物品消耗</li>
 *     <li>食物类物品使用后的保留逻辑</li>
 * </ul>
 *
 * <p>
 * 子类通常只需要重写以下方法即可实现新的构件:
 * </p>
 *
 * <ul>
 *     <li>{@link #onMechanismUseOn(UseOnContext)} —— 对方块使用时触发</li>
 *     <li>{@link #onMechanismUse(Level, Player, InteractionHand)} —— 右键使用时触发</li>
 *     <li>{@link #useAfterConsume()} —— 是否在使用后消耗物品</li>
 *     <li>{@link #getCooldownTicks()} —— 使用后的冷却时间</li>
 * </ul>
 *
 * <p>
 * 当上述行为返回成功结果（即
 * {@link InteractionResult#consumesAction()} 返回 {@code true}）
 * 时, 本类会自动执行挥手动画, 冷却以及物品消耗逻辑
 * </p>
 *
 * <h2>示例</h2>
 *
 * <pre>{@code
 * public class FireItem extends MechanismItem {
 *     public FireItem(Properties properties) {
 *         super(properties);
 *     }
 *
 *     @Override
 *     protected boolean useAfterConsume() {
 *         return true;
 *     }
 *
 *     @Override
 *     protected int getCooldownTicks() {
 *         return 20;
 *     }
 *
 *     @Override
 *     protected InteractionResult onMechanismUseOn(UseOnContext context) {
 *         context.getLevel().setBlock(
 *                 context.getClickedPos().above(),
 *                 Blocks.FIRE.defaultBlockState(),
 *                 3
 *         );
 *
 *         return InteractionResult.SUCCESS;
 *     }
 * }
 * }</pre>
 */
public abstract class MechanismItem extends BasicItem {
	protected static final Random RANDOM = new SecureRandom();

	public MechanismItem(Properties properties) {
		super(properties);
	}

	/**
	 * 是否在使用后消耗该物品
	 *
	 * <p>
	 * 如果不想要使用后消耗该物品, 重写该方法并直接返回 {@code false}
	 *
	 * </p>
	 * <pre>{@code
	 * @Override
	 * protected boolean useAfterConsume() {
	 *     return false;
	 * }
	 * }</pre>
	 */
	protected abstract boolean useAfterConsume();

	/**
	 * 当构件对方块使用时调用
	 *
	 * <p>
	 * 子类可以重写该方法实现具体的构件行为
	 * </p>
	 *
	 * <p>
	 * 默认返回 {@link InteractionResult#PASS}, 表示不执行任何操作,
	 * 并允许其他逻辑继续处理该交互
	 * </p>
	 *
	 * <pre>{@code
	 * @Override
	 * protected InteractionResult onMechanismUse(UseOnContext context) {
	 *     // 在这里实现构件的具体逻辑
	 *     return InteractionResult.SUCCESS;
	 * }
	 * }</pre>
	 */
	protected InteractionResult onMechanismUseOn(UseOnContext context) {
		return InteractionResult.PASS;
	}

	/**
	 * 当构件使用时调用
	 *
	 * <p>
	 * 子类可以重写该方法实现具体的构件行为
	 * </p>
	 *
	 * <p>
	 * 默认返回 {@link InteractionResult#PASS}, 表示不执行任何操作,
	 * 并允许其他逻辑继续处理该交互
	 * </p>
	 *
	 * <pre>{@code
	 * @Override
	 * protected InteractionResult onMechanismUseOn(UseContext context) {
	 *     // 在这里实现构件的具体逻辑
	 *     return InteractionResult.SUCCESS;
	 * }
	 * }</pre>
	 */
	protected InteractionResultHolder<ItemStack> onMechanismUse(Level level, Player player, InteractionHand hand) {
		return InteractionResultHolder.pass(player.getMainHandItem());
	}

	/**
	 * 挥手动画
	 *
	 * @param context 交互上下文
	 * @param player  玩家
	 */
	private void handleSwing(UseOnContext context, Player player) {
		player.swing(context.getHand(), true);
	}

	private void handleItemSwing(InteractionHand hand, Player player) {
		player.swing(hand, true);
	}

	/**
	 * 处理物品冷却
	 *
	 * @param player 玩家
	 * @param item   物品
	 */
	private void handleCooldown(Player player, ItemStack item) {
		if (!player.isCreative()) {
			int cooldown = getCooldownTicks();

			if (cooldown > 0) {
				player.getCooldowns().addCooldown(item.getItem(), cooldown);
			}
		}
	}

	/**
	 * 处理消耗物品
	 *
	 * @param player 玩家
	 * @param item   物品
	 */
	private void applyConsume(Player player, ItemStack item) {
		if (!player.isCreative() && useAfterConsume()) {
			item.shrink(1);
		}
	}

	/**
	 * 获取使用后的冷却 tick 数
	 *
	 * <p>
	 * 返回 {@code 0} 表示不添加冷却
	 * </p>
	 *
	 * <p>
	 * 可以返回固定值, 也可以使用随机范围
	 * </p>
	 *
	 * <pre>{@code
	 * // 固定冷却
	 * @Override
	 * protected int getCooldownTicks() {
	 *     return 10;
	 * }
	 *
	 * // 随机冷却 (5-10 tick)
	 * @Override
	 * protected int getCooldownTicks() {
	 *     return 5 + RANDOM.nextInt(6);
	 * }
	 * }</pre>
	 */
	protected int getCooldownTicks() {
		return 0;
	}

	protected InteractionResult useOtherItem(@NotNull Item item, @NotNull UseOnContext context) {
		ItemStack stack = item.getDefaultInstance();

		BlockHitResult result = new BlockHitResult(
				context.getClickLocation(),
				context.getClickedFace(),
				context.getClickedPos(),
				false
		);

		UseOnContext newContext = new UseOnContext(
				context.getLevel(),
				context.getPlayer(),
				context.getHand(),
				stack,
				result
		);

		return item.useOn(newContext);
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
		if (useAfterConsume()) {
			return super.finishUsingItem(stack, level, entity);
		}

		if (!(entity instanceof Player player)) {
			return super.finishUsingItem(stack, level, entity);
		}

		if (player.isCreative()) {
			return super.finishUsingItem(stack, level, entity);
		}

		super.finishUsingItem(stack.copy(), level, entity);
		return stack;
	}

	/**
	 * 处理玩家对方块使用该物品时的逻辑
	 *
	 * <p>
	 * 该方法会调用 {@link #onMechanismUseOn(UseOnContext)} 执行构件行为,
	 * 并在行为成功时自动处理以下逻辑:
	 * </p>
	 *
	 * <ul>
	 * <li>播放玩家挥手动画</li>
	 * <li>根据 {@link #useAfterConsume()} 决定是否消耗物品</li>
	 * <li>根据 {@link #getCooldownTicks()} 添加物品冷却</li>
	 * </ul>
	 *
	 * <p>
	 * 一般情况下不需要重写该方法,
	 * 只需重写 {@link #onMechanismUseOn(UseOnContext)} 即可实现新的构件行为
	 * </p>
	 */
	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		InteractionResult result = onMechanismUseOn(context);

		if (result.consumesAction()) {
			Player player = context.getPlayer();
			ItemStack stack = context.getItemInHand();

			if (player != null) {
				handleSwing(context, player);
				handleCooldown(player, stack);
				applyConsume(player, stack);
			}
		}

		return result;
	}

	/**
	 * 处理玩家使用该物品时的逻辑
	 *
	 * <p>
	 * 该方法会调用 {@link #onMechanismUse(Level, Player, InteractionHand)} 执行构件行为,
	 * 并在行为成功时自动处理以下逻辑:
	 * </p>
	 *
	 * <ul>
	 * <li>播放玩家挥手动画</li>
	 * <li>根据 {@link #useAfterConsume()} 决定是否消耗物品</li>
	 * <li>根据 {@link #getCooldownTicks()} 添加物品冷却</li>
	 * </ul>
	 *
	 * <p>
	 * 一般情况下不需要重写该方法,
	 * 只需重写 {@link #onMechanismUse(Level, Player, InteractionHand)} 即可实现新的构件行为
	 * </p>
	 */
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		InteractionResultHolder<ItemStack> holder = onMechanismUse(level, player, hand);
		InteractionResult result = holder.getResult();

		if (result.consumesAction()) {
			ItemStack stack = player.getItemInHand(hand);

			handleItemSwing(hand, player);
			handleCooldown(player, stack);
			applyConsume(player, stack);
		}

		return holder;
	}
}