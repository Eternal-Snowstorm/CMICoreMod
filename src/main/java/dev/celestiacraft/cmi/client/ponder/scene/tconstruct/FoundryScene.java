package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.List;

public class FoundryScene {
	public static void building(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);
		scene.title("foundry_building", "Building the Foundry");

		NebulaSceneBuilder.init9x9(scene, util);

		BlockPos bottomCenter = util.grid().at(4, 1, 4);

		Selection second = util.select().fromTo(2, 2, 2, 6, 2, 6);
		Selection bottom = util.select().fromTo(2, 1, 2, 6, 1, 6);
		Selection foundry = second.copy().add(bottom);
		Selection cast = util.select().fromTo(1, 1, 4, 1, 2, 5);

		scene.idle(5);
		scene.world().showSection(foundry, Direction.DOWN);

		scene.idle(20);
		scene.overlay().showOutline(PonderPalette.GREEN, foundry, foundry, 130);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("The Foundry is very similar to The Smeltery")
				.pointAt(util.vector().topOf(bottomCenter.above()))
				.attachKeyFrame();
		scene.idle(45);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("But it uses scorched blocks and its edges must be filled")
				.pointAt(util.vector().topOf(bottomCenter.above()))
				.attachKeyFrame();
		scene.idle(60);

		NebulaSceneBuilder.rotateAround(scene, 60, 90);

		scene.idle(20);

		scene.overlay().showText(100)
				.colored(PonderPalette.GREEN)
				.text("The outer wall can also be extended upwards by up to 63 blocks")
				.attachKeyFrame();

		scene.idle(10);

		List<BlockPos> bricks1 = List.of(
				util.grid().at(2, 3, 3), util.grid().at(2, 3, 4), util.grid().at(2, 3, 5), util.grid().at(2, 3, 6),
				util.grid().at(3, 3, 6), util.grid().at(4, 3, 6), util.grid().at(5, 3, 6), util.grid().at(6, 3, 6),
				util.grid().at(6, 3, 5), util.grid().at(6, 3, 4), util.grid().at(6, 3, 3), util.grid().at(6, 3, 2),
				util.grid().at(5, 3, 2), util.grid().at(4, 3, 2), util.grid().at(3, 3, 2), util.grid().at(2, 3, 2),

				util.grid().at(2, 4, 3), util.grid().at(2, 4, 4), util.grid().at(2, 4, 5), util.grid().at(2, 4, 6),
				util.grid().at(3, 4, 6), util.grid().at(4, 4, 6), util.grid().at(5, 4, 6), util.grid().at(6, 4, 6),
				util.grid().at(6, 4, 5), util.grid().at(6, 4, 4), util.grid().at(6, 4, 3), util.grid().at(6, 4, 2),
				util.grid().at(5, 4, 2), util.grid().at(4, 4, 2), util.grid().at(3, 4, 2), util.grid().at(2, 4, 2),

				util.grid().at(2, 5, 3), util.grid().at(2, 5, 4), util.grid().at(2, 5, 5), util.grid().at(2, 5, 6),
				util.grid().at(3, 5, 6), util.grid().at(4, 5, 6), util.grid().at(5, 5, 6), util.grid().at(6, 5, 6),
				util.grid().at(6, 5, 5), util.grid().at(6, 5, 4), util.grid().at(6, 5, 3), util.grid().at(6, 5, 2),
				util.grid().at(5, 5, 2), util.grid().at(4, 5, 2), util.grid().at(3, 5, 2), util.grid().at(2, 5, 2),

				util.grid().at(2, 6, 3), util.grid().at(2, 6, 4), util.grid().at(2, 6, 5), util.grid().at(2, 6, 6),
				util.grid().at(3, 6, 6), util.grid().at(4, 6, 6), util.grid().at(5, 6, 6), util.grid().at(6, 6, 6),
				util.grid().at(6, 6, 5), util.grid().at(6, 6, 4), util.grid().at(6, 6, 3), util.grid().at(6, 6, 2),
				util.grid().at(5, 6, 2), util.grid().at(4, 6, 2), util.grid().at(3, 6, 2), util.grid().at(2, 6, 2),

				util.grid().at(2, 7, 3), util.grid().at(2, 7, 4), util.grid().at(2, 7, 5), util.grid().at(2, 7, 6),
				util.grid().at(3, 7, 6), util.grid().at(4, 7, 6), util.grid().at(5, 7, 6), util.grid().at(6, 7, 6),
				util.grid().at(6, 7, 5), util.grid().at(6, 7, 4), util.grid().at(6, 7, 3), util.grid().at(6, 7, 2),
				util.grid().at(5, 7, 2), util.grid().at(4, 7, 2), util.grid().at(3, 7, 2), util.grid().at(2, 7, 2),

				util.grid().at(2, 8, 3), util.grid().at(2, 8, 4), util.grid().at(2, 8, 5), util.grid().at(2, 8, 6),
				util.grid().at(3, 8, 6), util.grid().at(4, 8, 6), util.grid().at(5, 8, 6), util.grid().at(6, 8, 6),
				util.grid().at(6, 8, 5), util.grid().at(6, 8, 4), util.grid().at(6, 8, 3), util.grid().at(6, 8, 2),
				util.grid().at(5, 8, 2), util.grid().at(4, 8, 2), util.grid().at(3, 8, 2), util.grid().at(2, 8, 2),

				util.grid().at(2, 9, 3), util.grid().at(2, 9, 4), util.grid().at(2, 9, 5), util.grid().at(2, 9, 6),
				util.grid().at(3, 9, 6), util.grid().at(4, 9, 6), util.grid().at(5, 9, 6), util.grid().at(6, 9, 6),
				util.grid().at(6, 9, 5), util.grid().at(6, 9, 4), util.grid().at(6, 9, 3), util.grid().at(6, 9, 2),
				util.grid().at(5, 9, 2), util.grid().at(4, 9, 2), util.grid().at(3, 9, 2), util.grid().at(2, 9, 2)
		);
		for (BlockPos brick : bricks1) {
			scene.world().showSection(util.select().position(brick), Direction.DOWN);
			scene.idle(1);
		}

		scene.idle(20);
		scene.addLazyKeyframe();
		scene.world().showSection(cast, Direction.NORTH);

		scene.idle(30);

		scene.overlay().showText(100)
				.text("The Foundry has unique features:\nIt will produce some by-products when smelting\nAnd it can't be used to make alloys")
				.colored(PonderPalette.MEDIUM);
		scene.idle(120);

		scene.idle(60);
		scene.markAsFinished();
	}
}