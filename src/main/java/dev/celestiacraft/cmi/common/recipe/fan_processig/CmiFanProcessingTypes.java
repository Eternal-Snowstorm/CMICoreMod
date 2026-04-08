package dev.celestiacraft.cmi.common.recipe.fan_processig;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.fan_processig.freezing.FreezingType;
import net.minecraft.core.Registry;

public class CmiFanProcessingTypes {
	public static FreezingType FREEZING;

	public static void register() {
		if (FREEZING != null) {
			return;
		}
		FREEZING = registerType(new FreezingType());
	}

	private static <T extends FanProcessingType> T registerType(T type) {
		return Registry.register(CreateBuiltInRegistries.FAN_PROCESSING_TYPE, Cmi.loadResource("freezing"), type);
	}
}
