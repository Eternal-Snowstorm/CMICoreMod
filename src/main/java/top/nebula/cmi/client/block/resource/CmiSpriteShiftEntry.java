package top.nebula.cmi.client.block.resource;

import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import top.nebula.cmi.Cmi;

@SuppressWarnings("unused")
public class CmiSpriteShiftEntry {
	public static final SpriteShiftEntry SAND_PAPER_BELT;
	public static final SpriteShiftEntry RED_SAND_PAPER_BELT;

	static {
		SAND_PAPER_BELT = addShift(
				"mechanical_belt_grinder/sand_paper",
				"mechanical_belt_grinder/sand_paper_scroll"
		);

		RED_SAND_PAPER_BELT = addShift(
				"mechanical_belt_grinder/red_sand_paper",
				"mechanical_belt_grinder/red_sand_paper_scroll"
		);
	}

	private static SpriteShiftEntry addShift(String originalLocation, String targetLocation) {
		return SpriteShifter.get(
				Cmi.loadResource("block/" + originalLocation),
				Cmi.loadResource("block/" + targetLocation)
		);
	}

	public static void init() {
	}
}