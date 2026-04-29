package dev.celestiacraft.cmi.common.item;

import dev.celestiacraft.libs.api.register.item.BasicItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class MechanismItem extends BasicItem {
	protected static final Random RANDOM = new Random();

	public MechanismItem(Properties properties) {
		super(properties);
	}

	/**
	 * 是否在使用后消耗该物品
	 * <p>
	 * 默认返回 {@code true}
	 *
	 * </p>
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
	protected boolean useAfterConsume() {
		return true;
	}

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
	protected InteractionResult onMechanismUse(UseOnContext context) {
		return InteractionResult.PASS;
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
	 * 该方法会调用 {@link #onMechanismUse(UseOnContext)} 执行构件行为,
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
	 * 只需重写 {@link #onMechanismUse(UseOnContext)} 即可实现新的构件行为
	 * </p>
	 */
	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		InteractionResult result = onMechanismUse(context);

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
}