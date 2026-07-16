package dev.celestiacraft.cmi.common.advancement.academic_fraud;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiAdvanmentTrigger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemDetectHandler {
	private static final String HAS_BLAST_FURNACE = "has_blast_furnace";
	private static final String CHECKED = "checked_academic";

	@SubscribeEvent
	public static void onPickup(EntityItemPickupEvent event) {
		record(event.getEntity(), event.getItem().getItem());
	}

	@SubscribeEvent
	public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
		record(event.getEntity(), event.getCrafting());
	}

	@SubscribeEvent
	public static void onSmelt(PlayerEvent.ItemSmeltedEvent event) {
		record(event.getEntity(), event.getSmelting());
	}

	private static void record(Player player, ItemStack stack) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		CompoundTag data = serverPlayer.getPersistentData();
		if (stack.is(Items.BLAST_FURNACE)) {
			data.putBoolean(HAS_BLAST_FURNACE, true);
			data.putBoolean(CHECKED, true);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		if (!(event.player instanceof ServerPlayer player)) {
			return;
		}

		CompoundTag data = player.getPersistentData();

		// 已结束就直接不跑
		if (data.getBoolean(CHECKED)) {
			return;
		}

		// 如果背包里已经有高炉
		if (player.getInventory().contains(new ItemStack(Items.BLAST_FURNACE))) {
			data.putBoolean(HAS_BLAST_FURNACE, true);
			data.putBoolean(CHECKED, true);
			return;
		}

		// 检测铁锭
		boolean hasIronIngot = player.getInventory().items.stream().anyMatch((stack) -> {
			return stack.is(Tags.Items.INGOTS_IRON);
		});

		if (hasIronIngot) {
			// 满足条件触发一次
			CmiAdvanmentTrigger.ACADEMIC_FRAUD.trigger(player);
			// 触发后也停止tick(避免浪费性能)
			data.putBoolean(CHECKED, true);
		}
	}
}