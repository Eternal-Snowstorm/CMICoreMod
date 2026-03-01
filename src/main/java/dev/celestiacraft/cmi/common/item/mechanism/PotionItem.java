package dev.celestiacraft.cmi.common.item.mechanism;

import com.simibubi.create.AllSoundEvents;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PotionItem extends MechanismItem {
	public PotionItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();
		BlockPos pos = player.blockPosition();

		if (level.isClientSide()) {
			return;
		}

		List<MobEffect> effects = List.of(
				MobEffects.SATURATION,
				MobEffects.FIRE_RESISTANCE,
				MobEffects.DIG_SPEED,
				MobEffects.DAMAGE_RESISTANCE,
				MobEffects.ABSORPTION,
				MobEffects.REGENERATION
		);
		effects.forEach((effect) -> {
			MobEffectInstance instance = new MobEffectInstance(effect, 300, 8);
			player.addEffect(instance);
		});
		player.playNotifySound(AllSoundEvents.CRAFTER_CLICK.getMainEvent(), SoundSource.VOICE, 2, 1);
	}
}