package dev.celestiacraft.cmi.client.ponder.scene.cmi;

import dev.celestiacraft.cmi.utils.ModResources;
import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class CrucibleScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);
		scene.showBasePlate();
		scene.idle(20);

		scene.world().showSection(
				util.select().fromTo(1, 1, 2, 3, 4, 4),
				Direction.DOWN
		);
		scene.idle(20);

		scene.overlay().showText(40)
				.text("Install Preheater here")
				.pointAt(util.vector().of(2.5, 1, 1.5))
				.placeNearTarget();
		scene.idle(20);

		scene.world().showSection(
				util.select().fromTo(2, 1, 1, 2, 4, 1),
				Direction.SOUTH
		);
		scene.world().showSection(
				util.select().fromTo(5, 1, 0, 5, 2, 0),
				Direction.SOUTH
		);
		scene.idle(20);

		scene.overlay().showText(40)
				.text("Power must be provided here")
				.pointAt(util.vector().of(2.5, 4.5, 1.5))
				.placeNearTarget();
		scene.idle(40);

		scene.addKeyframe();

		scene.overlay().showText(40)
				.text("You can either use GUI or automation input")
				.placeNearTarget();
		scene.idle(40);

		scene.addKeyframe();

		scene.overlay().showText(40)
				.text("Ingredients can also insert into crucible on the top")
				.pointAt(util.vector().of(2.5, 5.5, 2.5))
				.placeNearTarget();

		scene.world().showSection(
				util.select().position(2, 5, 2),
				Direction.DOWN
		);
		scene.idle(50);

		scene.addKeyframe();

		scene.rotateCameraY(90);
		scene.idle(20);

		scene.world().showSection(
				util.select().position(4, 1, 3),
				Direction.WEST
		);

		scene.overlay().showText(40)
				.text("Coke coal can be inserted into the burners")
				.pointAt(util.vector().of(4.5, 1.5, 3.5))
				.placeNearTarget();

		Vec3 motion = util.vector().of(0, -0.1, 0);

		ElementLink<EntityElement> coalCokeItem = scene.world().createItemEntity(
				util.vector().of(4.5, 3, 3.5),
				motion,
				ModResources.COAL_COKE.getItemStack()
		);

		scene.idle(9);

		scene.world().removeEntity(coalCokeItem);
		scene.idle(40);

		scene.addKeyframe();

		scene.world().showSection(
				util.select().fromTo(5, 3, 4, 4, 4, 3),
				Direction.WEST
		);

		scene.overlay().showText(40)
				.text("Products can be extracted from the sides of the crucible")
				.pointAt(util.vector().of(4.5, 4.5, 3.5))
				.placeNearTarget();

		scene.idle(20);

		scene.world().createItemOnBelt(
				util.grid().at(4, 3, 3),
				Direction.UP,
				ModResources.STEEL_INGOT.getItemStack()
		);

		scene.idle(40);
		scene.markAsFinished();
	}
}