package dev.celestiacraft.cmi.utils.tool;

import net.minecraft.resources.ResourceLocation;

public enum ToolType {
	SWORD("forge:mineable/sword"),
	PICKAXE("minecraft:mineable/pickaxe"),
	AXE("minecraft:mineable/axe"),
	SHOVEL("minecraft:mineable/shovel"),
	HOE("minecraft:mineable/hoe");

	private final ResourceLocation tag;

	ToolType(String location) {
		tag = ResourceLocation.parse(location);
	}

	public ResourceLocation tag() {
		return tag;
	}

	public static ToolType from(String key) {
		try {
			return ToolType.valueOf(key.toUpperCase());
		} catch (Exception exception) {
			throw new IllegalArgumentException("Unknown ToolType: " + key);
		}
	}
}