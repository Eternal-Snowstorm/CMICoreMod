package dev.celestiacraft.cmi.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CmiKeyMapping {
	public static final List<KeyMapping> MAPPINGS = new ArrayList<>();

	public static final KeyMapping OPEN_RADIAL = addKeyMapping(
			"key.cmi.open_radial",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_TAB,
			"key.cmi.categories"
	);

	private static KeyMapping addKeyMapping(String name, InputConstants.Type type, int code, String category) {
		KeyMapping mapping = new KeyMapping(name, type, code, category);
		MAPPINGS.add(mapping);
		return mapping;
	}
}