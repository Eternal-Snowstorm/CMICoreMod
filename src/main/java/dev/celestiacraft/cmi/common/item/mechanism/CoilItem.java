package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.api.register.item.MechanismItem;
import dev.celestiacraft.cmi.common.entity.coin_projectile.CoinFireType;
import dev.celestiacraft.cmi.common.entity.coin_projectile.CoinProjectileEntity;
import dev.celestiacraft.cmi.common.entity.coin_projectile.CoinProjectileType;
import dev.celestiacraft.cmi.common.entity.coin_projectile.CoinProjectileTypes;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import dev.celestiacraft.cmi.tags.CmiItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * 线圈构件 —— 硬币发射器
 *
 * <p>
 * 右键立即消耗背包中一枚带 {@code forge:coins} 标签的物品, 并把它作为弹射物实体发射出去。
 * 发射参数(射击类型、速度、伤害、渲染姿态等)由数据包
 * {@code data/<namespace>/cmi/coin_projectile/<name>.json} 按 coin 物品逐个定义,
 * 见 {@link CoinProjectileType}
 * </p>
 *
 * <ul>
 *     <li>弹药查找顺序: 副手 → 快捷栏 → 主背包</li>
 *     <li>创造模式不消耗弹药</li>
 *     <li>命中生物时按"命中瞬间速度 × damage"结算伤害(沿用原版箭矢公式)</li>
 *     <li>射击类型: 击退 / 贯穿 / 散射, 各自的重力、击退、贯穿数量、连发数量与可拾取性见
 *     {@link CoinFireType}</li>
 *     <li>散射一次连发数颗, 但整次使用只消耗一枚硬币</li>
 *     <li>线圈自身不会被消耗</li>
 * </ul>
 */
public class CoilItem extends MechanismItem {
	/**
	 * 散射弹之间的角度间隔(度)
	 */
	private static final float SCATTER_ANGLE_STEP = 10.0F;

	public CoilItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack coil = player.getItemInHand(hand);

		if (hand == InteractionHand.OFF_HAND && !player.getMainHandItem().isEmpty()) {
			return InteractionResultHolder.pass(coil);
		}

		ItemStack ammo = find(player);

		if (ammo.isEmpty()) {
			return InteractionResultHolder.fail(coil);
		}

		if (!level.isClientSide) {
			CoinProjectileType type = CoinProjectileTypes.getOrFallback(ammo);
			int count = getBulletCount(type);
			float velocity = (float) type.getVelocity();

			for (int i = 0; i < count; i++) {
				CoinProjectileEntity projectile = new CoinProjectileEntity(CmiEntity.COIN_PROJECTILE.get(), level, player);
				projectile.setCoin(ammo, type);
				projectile.shootFromRotation(player, player.getXRot(), player.getYRot() + getYawOffset(i, count), 0.0F, velocity, type.getInaccuracy());
				level.addFreshEntity(projectile);
			}

			consume(player, ammo);

			level.playSound(null, player.getX(), player.getY(), player.getZ(), type.getShootSound(), SoundSource.PLAYERS, type.getShootVolume(), 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F));

			if (type.getCooldown() > 0) {
				player.getCooldowns().addCooldown(this, type.getCooldown());
			}

			player.awardStat(Stats.ITEM_USED.get(this));
		}

		player.swing(hand, true);

		return InteractionResultHolder.consume(coil);
	}

	/**
	 * 本次使用要射出多少颗子弹: 只有散射类型会连发
	 */
	private static int getBulletCount(CoinProjectileType type) {
		if (type.getFireType() != CoinFireType.SCATTER) {
			return 1;
		}

		return Mth.clamp(type.getBulletCount(), 1, CoinProjectileType.MAX_BULLET_COUNT);
	}

	/**
	 * 散射弹的水平偏角: 以准星方向为中心左右对称展开, 相邻两发相差 {@link #SCATTER_ANGLE_STEP} 度
	 */
	private static float getYawOffset(int index, int count) {
		if (count <= 1) {
			return 0.0F;
		}

		return (index - (count - 1) / 2.0F) * SCATTER_ANGLE_STEP;
	}

	/**
	 * 该物品是否可以作为弹药
	 */
	private static boolean isCoin(ItemStack stack) {
		return !stack.isEmpty() && stack.is(CmiItemTags.COINS);
	}

	/**
	 * 在玩家背包中找到第一组可用弹药, 找不到返回 {@link ItemStack#EMPTY}
	 */
	private static @NotNull ItemStack find(Player player) {
		Inventory inventory = player.getInventory();

		ItemStack offhand = inventory.offhand.get(0);
		if (isCoin(offhand)) {
			return offhand;
		}

		for (ItemStack stack : inventory.items) {
			if (isCoin(stack)) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * 消耗一枚弹药
	 *
	 * <p>
	 * 创造模式不消耗
	 * </p>
	 */
	private static void consume(Player player, ItemStack ammo) {
		if (player.isCreative() || ammo.isEmpty()) {
			return;
		}

		ammo.shrink(1);
		player.getInventory().setChanged();
	}
}