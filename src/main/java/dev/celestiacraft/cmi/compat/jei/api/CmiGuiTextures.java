package dev.celestiacraft.cmi.compat.jei.api;

import dev.celestiacraft.cmi.api.client.assets.Guis;

public class CmiGuiTextures {
	public static final Guis WATER_WELL_SEA_WATER_ARROW;
	public static final Guis WATER_WELL_ARROW;
	public static final Guis WELL_NETHER_ARROW;

	static {
		WATER_WELL_SEA_WATER_ARROW = addGuiTexture("jei/arrows", 1, 1, 63, 24);
		WATER_WELL_ARROW = addGuiTexture("jei/arrows", 1, 25, 63, 48);
		WELL_NETHER_ARROW = addGuiTexture("jei/arrows", 1, 49, 63, 72);
	}

	public static Guis addGuiTexture(String path, int startX, int startY, int width, int height) {
		return new Guis(path, startX, startY, width, height);
	}

	public static Guis addGuiTexture(String path, int width, int height) {
		return new Guis(path, 0, 0, width, height);
	}
}