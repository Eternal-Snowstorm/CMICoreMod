package top.nebula.cmi.client.block.resource;

import com.jozufozu.flywheel.core.PartialModel;
import top.nebula.cmi.Cmi;

public class CmiBlockPartialModel {
	public static final PartialModel STEAM_HAMMER;
	public static final PartialModel SPOUT_TOP;
	public static final PartialModel SPOUT_MIDDLE;
	public static final PartialModel SPOUT_BOTTOM;

	static {
		STEAM_HAMMER = addPartial("steam_hammer/head");

		SPOUT_TOP = addPartial("advanced_spout/top");
		SPOUT_MIDDLE = addPartial("advanced_spout/middle");
		SPOUT_BOTTOM = addPartial("advanced_spout/bottom");
	}

	private static PartialModel addPartial(String path) {
		return new PartialModel(Cmi.loadResource("block/" + path));
	}

	public static void init() {
	}
}