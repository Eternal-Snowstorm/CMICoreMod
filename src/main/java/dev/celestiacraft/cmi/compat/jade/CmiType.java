package dev.celestiacraft.cmi.compat.jade;

import net.minecraft.resources.ResourceLocation;
import dev.celestiacraft.cmi.Cmi;

public class CmiType {
	public static final ResourceLocation
			SPACE_ELEVATOR_CARGO_FLUID,
			COMMON;

	static {
		COMMON = addType("common");
		SPACE_ELEVATOR_CARGO_FLUID = addType("space_elevator_cargo_fluid");
	}

	public static ResourceLocation addType(String path) {
		return Cmi.loadResource(path);
	}
}