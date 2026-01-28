package top.nebula.cmi.client.block.resource;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.resources.ResourceLocation;
import top.nebula.cmi.Cmi;

import java.util.HashMap;
import java.util.Map;

public class CmiBlockPartialModel {
	public static final Map<ResourceLocation, Couple<PartialModel>> FOLDING_DOORS = new HashMap<>();

	public static final PartialModel HYDRAULIC_PRESS_HEAD;

	static {
		HYDRAULIC_PRESS_HEAD = addPartial("hydraulic_press/head");
	}

	private static PartialModel addPartial(String path) {
		return new PartialModel(Cmi.loadResource("block/" + path));
	}

	public static void init() {
	}
}