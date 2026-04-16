package dev.celestiacraft.cmi.client.ponder.scene.cmi;

import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;

public class WaterPumpScene {
	public static void seaWater(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);

		scene.title("sea_water", "海水井");
		scene.scaleSceneView(0.5f);
		scene.idle(20);

		Selection indeSel1 = util.select().fromTo(1, 3, 3, 3, 3, 1);
		Selection indeSel2 = util.select().fromTo(1, 6, 3, 3, 6, 1);
		Selection indeSel3 = util.select().fromTo(1, 3, 3, 1, 6, 1);
		Selection indeSel4 = util.select().fromTo(1, 3, 1, 3, 6, 1);

		ElementLink<WorldSectionElement> inde1 = scene.world().showIndependentSection(
				indeSel1,
				Direction.DOWN
		);
		ElementLink<WorldSectionElement> inde2 = scene.world().showIndependentSection(
				indeSel2,
				Direction.DOWN
		);
		ElementLink<WorldSectionElement> inde3 = scene.world().showIndependentSection(
				indeSel3,
				Direction.DOWN
		);
		ElementLink<WorldSectionElement> inde4 = scene.world().showIndependentSection(
				indeSel4,
				Direction.DOWN
		);

		scene.idle(20);
		scene.addKeyframe();

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().fromTo(1, 3, 3, 3, 6, 1),
				40
		);

		scene.text(
				40,
				"如你所示, 这是一个水井",
				util.vector().topOf(2, 4, 2)
		);

		scene.idle(55);

		scene.world().hideIndependentSection(inde1, Direction.UP);
		scene.world().hideIndependentSection(inde2, Direction.UP);
		scene.world().hideIndependentSection(inde3, Direction.UP);
		scene.world().hideIndependentSection(inde4, Direction.UP);

		scene.idle(20);

		scene.world().showSection(util.select().fromTo(0, 0, 0, 6, 6, 6), Direction.DOWN);
		scene.idle(20);

		scene.addKeyframe();

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().fromTo(1, 3, 3, 3, 6, 1),
				50
		);

		scene.text(
				50,
				"当放置在海平面(Y=62)的海洋生物群系中时...",
				util.vector().of(2, 4.5, 2)
		);

		scene.idle(65);
		scene.addKeyframe();

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().fromTo(4, 4, 4, 6, 6, 6),
				50
		);

		scene.text(
				50,
				"它便会抽出海水",
				util.vector().centerOf(5, 5, 5)
		);

		scene.idle(65);
		scene.markAsFinished();
	}
}