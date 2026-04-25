package dev.celestiacraft.cmi.event.radial;

import cc.sighs.auratip.api.client.RadialMenuClientApi;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.key.CmiKeyMapping;
import dev.celestiacraft.cmi.client.menu.CmiRadialMenu;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class OpenRadial {
	private static boolean isMenuActive = false;
	private static boolean wasDown = false;
	private static long lastPressTime = 0;
	private static final long MIN_PRESS_DELAY = 500;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.screen != null) {
			return;
		}

		KeyMapping key = CmiKeyMapping.OPEN_RADIAL;
		boolean isKeyDown = key.isDown();
		long currentTime = System.currentTimeMillis();

		if (isKeyDown && !wasDown && (currentTime - lastPressTime > MIN_PRESS_DELAY)) {
			wasDown = true;
			lastPressTime = currentTime;

			if (!isMenuActive) {
				RadialMenuClientApi.open(CmiRadialMenu.MENU);
			}
			isMenuActive = !isMenuActive;
		}

		wasDown = isKeyDown;
	}
}