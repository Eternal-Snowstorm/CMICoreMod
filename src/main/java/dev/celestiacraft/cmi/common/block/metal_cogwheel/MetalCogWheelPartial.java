package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import java.util.HashMap;
import java.util.Map;

public class MetalCogWheelPartial {
	public static final Map<String, PartialModel> SMALL = new HashMap<>();
	public static final Map<String, PartialModel> LARGE = new HashMap<>();
	public static final Map<String, PartialModel> SMALL_WITH_SHAFT = new HashMap<>();
	public static final Map<String, PartialModel> LARGE_WITH_SHAFT = new HashMap<>();

	public static void register() {
		MetalCogWheelRegister.MATERIAL_LIST.forEach((material) -> {
			SMALL.put(material, partial(material, "small_gear"));
			LARGE.put(material, partial(material, "large_gear"));
			SMALL_WITH_SHAFT.put(material, partial(material, "small"));
			LARGE_WITH_SHAFT.put(material, partial(material, "large"));
		});
	}

	private static PartialModel partial(String material, String type) {
		String path = String.format("block/cogwheel/%s/%s", material, type);
		return PartialModel.of(Cmi.loadResource(path));
	}

	public static void init() {
	}
}