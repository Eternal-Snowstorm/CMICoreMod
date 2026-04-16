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

		scene.title("crucible_usage", "坩埚使用");
		scene.showBasePlate();
		scene.idle(20);

		scene.world().showSection(
				util.select().fromTo(1, 1, 2, 3, 4, 4),
				Direction.DOWN
		);
		scene.idle(20);

		scene.text(40, "预热器安装在这里", util.vector().of(2.5, 1, 1.5));
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

		scene.text(40, "需要在这里提供电", util.vector().of(2.5, 4.5, 1.5));
		scene.idle(40);

		scene.addKeyframe();

		scene.text(40, "你可以使用GUI输入原料, 也可以使用自动化输入");
		scene.idle(40);

		scene.addKeyframe();

		scene.text(
				40,
				"原料也可以从顶部加入到坩埚中",
				util.vector().of(2.5, 5.5, 2.5)
		);

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

		scene.text(
				40,
				"焦炭可以在这里放入燃烧器中",
				util.vector().of(4.5, 1.5, 3.5)
		);

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

		scene.text(
				40,
				"产物可以从坩埚的侧面提取",
				util.vector().of(4.5, 4.5, 3.5)
		);

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