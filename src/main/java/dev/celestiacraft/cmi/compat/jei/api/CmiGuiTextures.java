package dev.celestiacraft.cmi.compat.jei.api;

import dev.celestiacraft.cmi.api.client.assets.Guis;

public class CmiGuiTextures {
	public static final Guis STRUCTURE;
	public static final Guis HEIGHT;
	public static final Guis OCEAN;
	public static final Guis NETHER;
	public static final Guis FORTRESS;

	static {
		STRUCTURE = addGuiTexture("jei/arrow_attachments", 0, 0, 16, 16);
		HEIGHT = addGuiTexture("jei/arrow_attachments", 16, 0, 16, 16);
		OCEAN = addGuiTexture("jei/arrow_attachments", 32, 0, 16, 16);
		NETHER = addGuiTexture("jei/arrow_attachments", 48, 0, 16, 16);
		FORTRESS = addGuiTexture("jei/arrow_attachments", 64, 0, 16, 16);
	}

	public static Guis addGuiTexture(String path, int startX, int startY, int width, int height) {
		return new Guis(path, startX, startY, width, height);
	}

	public static Guis addGuiTexture(String path, int width, int height) {
		return new Guis(path, 0, 0, width, height);
	}
}