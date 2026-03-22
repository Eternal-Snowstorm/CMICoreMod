package dev.celestiacraft.cmi.common.enchantment;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IndestructibleEnchantment extends Enchantment {
	public IndestructibleEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
		super(rarity, category, slots);
	}

	@Override
	public boolean isTradeable() {
		return super.isTradeable();
	}

	@Override
	public boolean isDiscoverable() {
		return super.isDiscoverable();
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		Player player = event.getPlayer();
		LevelAccessor level = event.getLevel();
		BlockPos pos = event.getPos();

		if (level.isClientSide()) {
			return;
		}

		ItemStack held = player.getMainHandItem();
		if (held.isEmpty()) return;

		// 判断工具是否有“不朽附魔”
		if (!hasIndestructible(held)) {
			return;
		}

		// 获取方块掉落
		BlockState state = level.getBlockState(pos);
		List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos), player, held);

		if (drops.isEmpty()) return;

		// 取消原版掉落
		event.setCanceled(true);
		level.destroyBlock(pos, false);

		// 手动生成掉落，并复制附魔
		for (ItemStack drop : drops) {
			ItemStack newStack = drop.copy();

			// 把工具上的附魔复制过去
			newStack.enchant(
					CmiEnchantment.INDESTRUCTIBLE.get(),
					1
			);

			Block.popResource((Level) level, pos, newStack);
		}
	}

	public static boolean hasIndestructible(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}

		IndestructibleEnchantment enchantment = CmiEnchantment.INDESTRUCTIBLE.get();

		return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0;
	}
}