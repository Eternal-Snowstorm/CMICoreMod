package dev.celestiacraft.cmi.event;

import cc.sighs.auratip.api.client.RadialMenuClientApi;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.key.CmiKeyMapping;
import dev.celestiacraft.cmi.client.menu.CmiRadialMenu;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class OpenRadial {
	private static boolean wasDown = false;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		KeyMapping key = CmiKeyMapping.OPEN_RADIAL;
		boolean isDown = key.isDown();

		if (isDown && !wasDown) {
			RadialMenuClientApi.open(CmiRadialMenu.MENU);
		}

		if (!isDown && wasDown) {
			RadialMenuClientApi.open(CmiRadialMenu.MENU);
		}

		wasDown = isDown;
	}
}