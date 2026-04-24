package dev.celestiacraft.cmi.event;

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
	private static boolean wasDown = false;
	private static boolean validPress = false;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.screen != null || !minecraft.isWindowActive()) {
			return;
		}

		KeyMapping key = CmiKeyMapping.OPEN_RADIAL;
		boolean isDown = key.isDown();

		long window = minecraft.getWindow().getWindow();

		int selfKey = key.getKey().getValue();

		boolean hasOtherModifier = (selfKey != GLFW.GLFW_KEY_LEFT_ALT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_ALT))
				|| (selfKey != GLFW.GLFW_KEY_RIGHT_ALT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_ALT))
				|| (selfKey != GLFW.GLFW_KEY_LEFT_CONTROL && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL))
				|| (selfKey != GLFW.GLFW_KEY_RIGHT_CONTROL && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL))
				|| (selfKey != GLFW.GLFW_KEY_LEFT_SHIFT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT))
				|| (selfKey != GLFW.GLFW_KEY_RIGHT_SHIFT && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT));

		if (isDown && !wasDown) {
			validPress = !hasOtherModifier;

			if (validPress) {
				RadialMenuClientApi.open(CmiRadialMenu.MENU);
			}
		}

		if (!isDown && wasDown) {
			if (validPress) {
				RadialMenuClientApi.open(CmiRadialMenu.MENU);
			}
			validPress = false;
		}

		wasDown = isDown;
	}
}