package dev.celestiacraft.cmi.utils;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.simibubi.create.Create;
import dev.celestiacraft.cmi.Cmi;
import earth.terrarium.adastra.AdAstra;
import mekanism.common.Mekanism;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;

public class ModResources {
	public static final ResourcesEntry TREATED_WOOD_SLAB;
	public static final ResourcesEntry SEA_WATER;
	public static final ResourcesEntry NAHUATL_SLAB;
	public static final ResourcesEntry NAHUATL_FENCE;
	public static final ResourcesEntry BLAZEWOOD_SLAB;
	public static final ResourcesEntry BLAZEWOOD_FENCE;
	public static final ResourcesEntry COAL_COKE;
	public static final ResourcesEntry STEEL_INGOT;
	public static final ResourcesEntry CRUCIBLE_BASE;
	public static final ResourcesEntry CRUCIBLE_TUYERE;
	public static final ResourcesEntry IRON_DEPOSIT_BLOCK;
	public static final ResourcesEntry NETWORK_TOOL;

	static {
		STEEL_INGOT = loadCmi("steel_ingot");
		SEA_WATER = loadCmi("sea_water");
		CRUCIBLE_BASE = loadCmi("crucible_base");
		CRUCIBLE_TUYERE = loadCmi("crucible_tuyere");

		NAHUATL_SLAB = loadTCon("nahuatl_slab");
		NAHUATL_FENCE = loadTCon("nahuatl_fence");
		BLAZEWOOD_SLAB = loadTCon("blazewood_slab");
		BLAZEWOOD_FENCE = loadTCon("blazewood_fence");

		TREATED_WOOD_SLAB = loadIE("slab_treated_wood_horizontal");

		COAL_COKE = loadThermal("coal_coke");

		IRON_DEPOSIT_BLOCK = loadResource("create_rns:iron_deposit_block");

		NETWORK_TOOL = loadResource(ResourceLocation.parse("ae:network_tool"));
	}

	public static ResourcesEntry loadResource(ResourceLocation location) {
		return new ResourcesEntry(location);
	}

	public static ResourcesEntry loadResource(String path) {
		return loadResource(ResourceLocation.parse(path));
	}

	public static ResourcesEntry loadCmi(String path) {
		return loadResource(Cmi.loadResource(path));
	}

	public static ResourcesEntry loadMek(String path) {
		return loadResource(Mekanism.rl(path));
	}

	public static ResourcesEntry loadCreate(String path) {
		return loadResource(Create.asResource(path));
	}

	public static ResourcesEntry loadTCon(String path) {
		return loadResource(TConstruct.getResource(path));
	}

	public static ResourcesEntry loadIE(String path) {
		return loadResource(ImmersiveEngineering.rl(path));
	}

	public static ResourcesEntry loadAd(String path) {
		return loadResource(ResourceLocation.fromNamespaceAndPath(AdAstra.MOD_ID, path));
	}

	public static ResourcesEntry loadThermal(String path) {
		return loadResource(ResourceLocation.fromNamespaceAndPath("thermal", path));
	}
}