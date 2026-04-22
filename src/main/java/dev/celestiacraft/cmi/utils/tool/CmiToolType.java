package dev.celestiacraft.cmi.utils.tool;

import net.minecraft.resources.ResourceLocation;

public enum CmiToolType {
	SWORD("forge:mineable/sword"),
	PICKAXE("minecraft:mineable/pickaxe"),
	AXE("minecraft:mineable/axe"),
	SHOVEL("minecraft:mineable/shovel"),
	HOE("minecraft:mineable/hoe");

	private final ResourceLocation tag;

	CmiToolType(String location) {
		tag = ResourceLocation.parse(location);
	}

	public ResourceLocation tag() {
		return tag;
	}

	public static CmiToolType from(String key) {
		try {
			return CmiToolType.valueOf(key.toUpperCase());
		} catch (Exception exception) {
			throw new IllegalArgumentException("Unknown ToolType: " + key);
		}
	}
}