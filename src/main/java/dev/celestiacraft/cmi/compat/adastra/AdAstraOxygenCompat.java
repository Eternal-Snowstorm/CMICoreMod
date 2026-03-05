package dev.celestiacraft.cmi.compat.adastra;

import dev.celestiacraft.cmi.compat.create.CreateOxygenSupport;
import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.api.systems.OxygenApi;
import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import net.minecraft.core.BlockPos;
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

		if (!(entity instanceof LivingEntity livingEntity)) {
			return false;
		}

		if (livingEntity instanceof Player player && (player.isCreative() || player.isSpectator())) {
			return true;
		}

		if (livingEntity.level().dimension() == Level.NETHER && livingEntity instanceof Player) {
			return true;
		}

		return CreateOxygenSupport.hasBacktankSupport(livingEntity);
	}

	private static boolean onOxygenTick(ServerLevel level, LivingEntity entity) {
		if (!(entity instanceof Player player)) {
			return true;
		}

		if (player.isCreative() || player.isSpectator()) {
			return true;
		}

		if (level.dimension() == Level.NETHER) {
			return false;
		}

		if (!CreateOxygenSupport.hasBacktankSupport(player)) {
			return true;
		}

		BlockPos eyePos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
		if (OxygenApi.API.hasOxygen(level, eyePos)) {
			return true;
		}

		CreateOxygenSupport.consumeBacktankAir(player, 1);

		return true;
	}

	public static boolean hasAdAstraSpaceSuitSupport(LivingEntity entity) {
		if (!SpaceSuitItem.hasFullSet(entity)) {
			return false;
		}

		ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if (!(chestStack.getItem() instanceof SpaceSuitItem)) {
			return false;
		}

		return SpaceSuitItem.hasOxygen(entity);
	}

	public static void consumeAdAstraSuitOxygen(LivingEntity entity, long amount) {
		ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if (chestStack.getItem() instanceof SpaceSuitItem spaceSuitItem) {
			spaceSuitItem.consumeOxygen(chestStack, amount);
		}
	}
}
