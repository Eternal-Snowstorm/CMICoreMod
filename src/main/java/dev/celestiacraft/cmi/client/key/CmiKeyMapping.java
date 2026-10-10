package dev.celestiacraft.cmi.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CmiKeyMapping {
	public static final List<KeyMapping> MAPPINGS = new ArrayList<>();

	public static final KeyMapping OPEN_RADIAL;

	static {
		OPEN_RADIAL = addKeyMapping("open_radial", GLFW.GLFW_KEY_TAB);
	}

	private static KeyMapping addKeyMapping(String name, int key) {
		KeyMapping mapping = new KeyMapping(
				"key.cmi.%s".formatted(name),
				InputConstants.Type.KEYSYM,
				key,
				"key.cmi.categories"
		);
		MAPPINGS.add(mapping);
		return mapping;
	}
}