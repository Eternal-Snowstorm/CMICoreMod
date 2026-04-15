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
	public static void seaWater(SceneBuilder scene, SceneBuildingUtil util) {
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

		scene.overlay().showText(40)
				.text("As you can see, this is a water pump")
				.pointAt(util.vector().topOf(2, 4, 2))
				.placeNearTarget();

		scene.idle(55);

		scene.world().hideIndependentSection(inde1, Direction.UP);
		scene.world().hideIndependentSection(inde2, Direction.UP);
		scene.world().hideIndependentSection(inde3, Direction.UP);
		scene.world().hideIndependentSection(inde4, Direction.UP);

		scene.idle(20);

		scene.world().showSection(
				util.select().fromTo(0, 0, 0, 6, 6, 6),
				Direction.DOWN
		);

		scene.idle(20);

		scene.addKeyframe();

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().fromTo(1, 3, 3, 3, 6, 1),
				50
		);

		scene.overlay().showText(50)
				.text("When placed at sea level in an ocean biome... (Y=62)")
				.pointAt(util.vector().of(2, 4.5, 2))
				.placeNearTarget();

		scene.idle(65);

		scene.addKeyframe();

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().fromTo(4, 4, 4, 6, 6, 6),
				50
		);

		scene.overlay().showText(50)
				.text("It will pump seawater")
				.pointAt(util.vector().centerOf(5, 5, 5))
				.placeNearTarget();

		scene.idle(65);
		scene.markAsFinished();
	}
}