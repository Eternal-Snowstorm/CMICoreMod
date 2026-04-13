package dev.celestiacraft.cmi.compat.adastra;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AdAstraOxygenCompat {
	private AdAstraOxygenCompat() {
	}

	public static void register() {
		AdAstraEvents.EntityOxygenEvent.register(AdAstraOxygenCompat::onEntityOxygen);
		AdAstraEvents.OxygenTickEvent.register(AdAstraOxygenCompat::onOxygenTick);
	}

	private static boolean onEntityOxygen(Entity entity, boolean hasOxygen) {
		if (hasOxygen) {
			return true;
		}

		return entity instanceof Player player && player.level().dimension() == Level.NETHER;
	}

	private static boolean onOxygenTick(ServerLevel level, LivingEntity entity) {
		return level.dimension() != Level.NETHER || !(entity instanceof Player);
	}

	public static boolean hasSpaceSuitSupport(LivingEntity entity) {
		if (!SpaceSuitItem.hasFullSet(entity)) {
			return false;
		}

		ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if (!(chestStack.getItem() instanceof SpaceSuitItem)) {
			return false;
		}

		return SpaceSuitItem.hasOxygen(entity);
	}

	public static void consumeSuitOxygen(LivingEntity entity, long amount) {
		ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if (chestStack.getItem() instanceof SpaceSuitItem spaceSuitItem) {
			spaceSuitItem.consumeOxygen(chestStack, amount);
		}
	}
}
