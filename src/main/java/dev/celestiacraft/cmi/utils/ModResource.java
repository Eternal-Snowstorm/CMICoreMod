package dev.celestiacraft.cmi.utils;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;

public class ModResource {
	public static final ResourceLocation TREATED_WOOD_STAIRS;
	public static final ResourceLocation SEA_WATER;

	static {
		TREATED_WOOD_STAIRS = ImmersiveEngineering.rl("stairs_treated_wood_horizontal");
		SEA_WATER = Cmi.loadResource("sea_water");
	}
}