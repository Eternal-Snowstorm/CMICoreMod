package dev.celestiacraft.cmi.common.recipe.fan;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.freezing.FreezingType;

public class CmiFanProcessingTypes extends AllFanProcessingTypes {
	public static final FreezingType FREEZING = register("freezing", new FreezingType());

	private static <T extends FanProcessingType> T register(String id, T type) {
		FanProcessingTypeRegistry.register(Cmi.loadResource(id), type);
		return type;
	}

	public static void register() {
	}
}