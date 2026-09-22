package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.tags.CmiItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * coin 弹药查找工具
 *
 * <p>
 * 拥有 {@code forge:coins} 标签的物品都可以被 {@link CoilItem} 当作弹药发射出去
 * </p>
 *
 * <p>
 * 查找顺序: 副手 → 快捷栏 → 主背包, 与"护符/硬币"这类杂项物品通常放在快捷栏的习惯一致
 * </p>
 */
public final class CoinAmmo {
	private CoinAmmo() {
	}

	/**
	 * 该物品是否可以作为弹药
	 */
	public static boolean isCoin(ItemStack stack) {
		return !stack.isEmpty() && stack.is(CmiItemTags.COINS);
	}

	/**
	 * 在玩家背包中找到第一组可用弹药, 找不到返回 {@link ItemStack#EMPTY}
	 */
	public static @NotNull ItemStack find(Player player) {
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
	public static void consume(Player player, ItemStack ammo) {
		if (player.isCreative() || ammo.isEmpty()) {
			return;
		}

		ammo.shrink(1);
		player.getInventory().setChanged();
	}
}
