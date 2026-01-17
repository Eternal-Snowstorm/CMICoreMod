package top.nebula.cmi.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerDrownInNether {

	@SubscribeEvent
	public static void playerDrownInNether(LivingBreatheEvent event) {
		if (!(event.getEntity() instanceof Player pPlayer)) {
			return;
		}
		if (pPlayer.level().dimension() == Level.NETHER) {
			// event.setCanBreathe(false);
			// event.setCanRefillAir(false);
		}
	}
}
