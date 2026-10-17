package dev.celestiacraft.cmi.common.register;

import com.lowdragmc.mbd2.api.registry.MBDRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.mbd2.BlockExistCondition;
import net.minecraftforge.fml.ModList;

public class CmiRecipeCondition {
	public static void register() {
		if (!ModList.get().isLoaded("mbd2")) {
			return;
		}

		// RECIPE_CONDITIONS 是 MBDRegistry.String<Class<? extends RecipeCondition>>
		// key 必须是 String, 且要与 BlockExistCondition#getType() 返回的 "cmi:block_exist" 完全一致
		MBDRegistries.RECIPE_CONDITIONS.register(
				Cmi.loadResource("block_exist").toString(),
				BlockExistCondition.class
		);
	}
}