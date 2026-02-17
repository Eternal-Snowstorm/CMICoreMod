package dev.celestiacraft.cmi.client.block.resource;

import com.jozufozu.flywheel.core.PartialModel;
import dev.celestiacraft.cmi.Cmi;

public class CmiBlockPartialModel {
	public static final PartialModel STEAM_HAMMER;
	public static final PartialModel SPOUT_TOP;
	public static final PartialModel SPOUT_MIDDLE;
	public static final PartialModel SPOUT_BOTTOM;
	public static final PartialModel GRINDER_BELT;

	static {
		STEAM_HAMMER = addPartial("steam_hammer/head");

		SPOUT_TOP = addPartial("advanced_spout/top");
		SPOUT_MIDDLE = addPartial("advanced_spout/middle");
		SPOUT_BOTTOM = addPartial("advanced_spout/bottom");

		GRINDER_BELT = addPartial("mechanical_belt_grinder/belt");
	}

	private static PartialModel addPartial(String path) {
		return new PartialModel(Cmi.loadResource("block/" + path));
	}

	public static void init() {
	}
}