package dev.celestiacraft.cmi.common.recipe.fan_processig;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.fan_processig.freezing.FreezingType;

public class CmiFanProcessingTypes {
	public static final FreezingType FREEZING;

	static {
		FREEZING = register("freezing", new FreezingType());
	}

	private static <T extends FanProcessingType> T register(String id, T type) {
		FanProcessingTypeRegistry.register(Cmi.loadResource(id), type);
		return type;
	}

	public static void register() {
	}
}