package dev.celestiacraft.cmi.utils;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.simibubi.create.Create;
import dev.celestiacraft.cmi.Cmi;
import earth.terrarium.adastra.AdAstra;
import mekanism.common.Mekanism;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;

public class ModResources {
	public static final ResourceLocation TREATED_WOOD_SLAB;
	public static final ResourceLocation SEA_WATER;

	static {
		TREATED_WOOD_SLAB = loadIE("slab_treated_wood_horizontal");
		SEA_WATER = loadCmi("sea_water");
	}

	private static ResourceLocation loadCmi(String path) {
		return Cmi.loadResource(path);
	}

	private static ResourceLocation loadMek(String path) {
		return Mekanism.rl(path);
	}

	private static ResourceLocation loadCreate(String path) {
		return Create.asResource(path);
	}

	private static ResourceLocation loadTCon(String path) {
		return TConstruct.getResource(path);
	}

	private static ResourceLocation loadIE(String path) {
		return ImmersiveEngineering.rl(path);
	}

	private static ResourceLocation loadAd(String path) {
		return ResourceLocation.fromNamespaceAndPath(AdAstra.MOD_ID, path);
	}
}