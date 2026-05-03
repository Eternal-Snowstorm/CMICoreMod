package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import java.util.HashMap;
import java.util.Map;

public class MetalCogWheelPartial {
	public static final Map<String, PartialModel> SMALL = new HashMap<>();
	public static final Map<String, PartialModel> LARGE = new HashMap<>();

	public static void register() {
		MetalCogWheelRegister.MATERIAL_LIST.forEach((material) -> {
			SMALL.put(material, add(material, "small"));
			LARGE.put(material, add(material, "large"));
		});
	}

	private static PartialModel add(String material, String type) {
		String path = String.format("block/cogwheel/%s/%s_gear", material, type);
		return PartialModel.of(Cmi.loadResource(path));
	}

	public static void init() {
	}
}