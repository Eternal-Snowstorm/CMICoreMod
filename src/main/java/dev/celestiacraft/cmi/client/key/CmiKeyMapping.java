package dev.celestiacraft.cmi.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class CmiKeyMapping {
	public static final KeyMapping OPEN_RADIAL = new KeyMapping(
			"key.cmi.open_radial",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_ALT,
			"key.cmi.categories"
	);
}