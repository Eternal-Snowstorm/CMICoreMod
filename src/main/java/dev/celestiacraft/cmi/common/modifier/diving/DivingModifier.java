package dev.celestiacraft.cmi.common.modifier.diving;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.create.CreateOxygenSupport;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DivingModifier extends NoLevelsModifier {
	public static final String ID = "diving";
	private static final ModifierId MODIFIER_ID = new ModifierId(Cmi.MODID, ID);


	public static boolean isWearing(LivingEntity entity) {
		ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
		return ModifierUtil.getModifierLevel(helmet, MODIFIER_ID) > 0;
	}
	public static boolean hasBacktankAir(LivingEntity entity) {
		return !BacktankUtil.getAllWithAir(entity).isEmpty();
	}
	public static boolean isActive(LivingEntity entity) {
		return isWearing(entity) && hasBacktankAir(entity);
	}

	@SubscribeEvent
	public static void onLivingBreathe(LivingBreatheEvent event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}

		Level level = player.level();
		if (level.dimension().equals(Level.NETHER)) {
			return;
		}

		if (!player.isEyeInFluid(FluidTags.WATER) || event.canBreathe()) {
			return;
		}
		if (player.isCreative() || player.isSpectator()) {
			return;
		}
		if (!isActive(player)) {
			return;
		}

		event.setCanBreathe(true);
		event.setCanRefillAir(true);
		event.setConsumeAirAmount(0);

		if (!level.isClientSide() && level.getGameTime() % 20 == 0) {
			CreateOxygenSupport.consumeBacktankAir(player, 1);
		}
	}
}