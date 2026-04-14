package dev.celestiacraft.cmi.utils;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.simibubi.create.Create;
import dev.celestiacraft.cmi.Cmi;
import earth.terrarium.adastra.AdAstra;
import mekanism.common.Mekanism;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;

public class ModResources {
	/**
	 * 防腐木台阶
	 */
	public static final ResourceLocation TREATED_WOOD_SLAB;
	/**
	 * 海水
	 */
	public static final ResourceLocation SEA_WATER;
	/**
	 * 纳瓦特尔台阶
	 */
	public static final ResourceLocation NAHUATL_SLAB;
	/**
	 * 纳瓦特尔栅栏
	 */
	public static final ResourceLocation NAHUATL_FENCE;
	/**
	 * 烈焰木台阶
	 */
	public static final ResourceLocation BLAZEWOOD_SLAB;
	/**
	 * 烈焰木栅栏
	 */
	public static final ResourceLocation BLAZEWOOD_FENCE;

	static {
		TREATED_WOOD_SLAB = loadIE("slab_treated_wood_horizontal");
		SEA_WATER = loadCmi("sea_water");
		NAHUATL_SLAB = loadTCon("nahuatl_slab");
		NAHUATL_FENCE = loadTCon("nahuatl_fence");
		BLAZEWOOD_SLAB = loadTCon("blazewood_slab");
		BLAZEWOOD_FENCE = loadTCon("blazewood_fence");
	}

	public static ResourceLocation loadCmi(String path) {
		return Cmi.loadResource(path);
	}

	public static ResourceLocation loadMek(String path) {
		return Mekanism.rl(path);
	}

	public static ResourceLocation loadCreate(String path) {
		return Create.asResource(path);
	}

	public static ResourceLocation loadTCon(String path) {
		return TConstruct.getResource(path);
	}

	public static ResourceLocation loadIE(String path) {
		return ImmersiveEngineering.rl(path);
	}

	public static ResourceLocation loadAd(String path) {
		return ResourceLocation.fromNamespaceAndPath(AdAstra.MOD_ID, path);
	}
}