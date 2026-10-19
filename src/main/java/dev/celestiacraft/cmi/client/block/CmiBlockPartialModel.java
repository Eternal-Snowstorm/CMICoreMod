package dev.celestiacraft.cmi.client.block;

import dev.celestiacraft.cmi.Cmi;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class CmiBlockPartialModel {
	public static final PartialModel GRINDER_BELT;

	static {
		GRINDER_BELT = addPartial("mechanical_belt_grinder/belt");
	}

	private static PartialModel addPartial(String path) {
		return PartialModel.of(Cmi.loadResource("block/" + path));
	}

	public static void init() {
	}
}