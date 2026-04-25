package dev.celestiacraft.cmi.event.radial;

import cc.sighs.auratip.api.client.RadialMenuClientApi;
import com.mojang.blaze3d.platform.InputConstants;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.key.CmiKeyMapping;
import dev.celestiacraft.cmi.client.menu.CmiRadialMenu;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

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
		int selfKey = key.getKey().getValue();
		long window = minecraft.getWindow().getWindow();

		boolean hasOtherModifier = (selfKey != GLFW.GLFW_KEY_LEFT_ALT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_ALT))
				|| (selfKey != GLFW.GLFW_KEY_RIGHT_ALT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_ALT))
				|| (selfKey != GLFW.GLFW_KEY_LEFT_CONTROL && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL))
				|| (selfKey != GLFW.GLFW_KEY_RIGHT_CONTROL && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL))
				|| (selfKey != GLFW.GLFW_KEY_LEFT_SHIFT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT))
				|| (selfKey != GLFW.GLFW_KEY_RIGHT_SHIFT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT));


		while (key.consumeClick()) {
			if (!isMenuActive && !hasOtherModifier) {
				RadialMenuClientApi.open(CmiRadialMenu.MENU);
			}
			isMenuActive = !isMenuActive;
		}
	}
}