package dev.celestiacraft.cmi.compat;

import dev.celestiacraft.cmi.compat.adastra.AdAstraOxygenCompat;
import net.minecraftforge.fml.ModList;

public class ModCompat {
	private static final String AD_ASTRA_MODID = "ad_astra";

	private ModCompat() {
	}

	public static boolean isAdAstraLoaded() {
		return ModList.get().isLoaded(AD_ASTRA_MODID);
	}

	public static void register() {
		if (isAdAstraLoaded()) {
			AdAstraOxygenCompat.register();
		}
	}
}
