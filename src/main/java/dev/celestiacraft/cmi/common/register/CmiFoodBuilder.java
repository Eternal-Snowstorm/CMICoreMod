package dev.celestiacraft.cmi.common.register;

import net.minecraft.world.food.FoodProperties;

public class CmiFoodBuilder {
	public static final FoodProperties PIG_IRON;

	static {
		PIG_IRON = new FoodProperties.Builder()
				.nutrition(10)
				.saturationMod(1.0f)
				.alwaysEat()
				.fast()
				.build()
		;
	}
}