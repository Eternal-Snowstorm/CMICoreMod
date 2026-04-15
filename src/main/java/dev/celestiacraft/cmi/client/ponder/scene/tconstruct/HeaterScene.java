package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HeaterScene {
	public static void using(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);
		scene.title("heater_using", "Use the Heater");

		NebulaSceneBuilder.init5x5(scene, util);

		BlockPos center = util.grid().at(2, 1, 2);

		BlockPos alloyer = center.above().east();
		BlockPos melter = center.above().west();

		Selection all = util.select().fromTo(melter.below(), alloyer);

		scene.idle(5);
		scene.world().showSection(util.select().position(center), Direction.NORTH);
		scene.idle(15);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("The Heater can burn solid fuel to provide a temperature of 800℃")
				.pointAt(util.vector().topOf(center))
				.attachKeyFrame();
		scene.idle(45);

		scene.idle(15);
		scene.world().destroyBlock(center);
		scene.idle(15);
		scene.world().showSection(all, Direction.NORTH);

		scene.idle(20);

		scene.addLazyKeyframe();
		scene.overlay().showControls(util.vector().blockSurface(alloyer.below(), Direction.NORTH), Pointing.RIGHT, 20)
				.withItem(new ItemStack(Items.COAL));
		scene.overlay().showControls(util.vector().blockSurface(melter.below(), Direction.NORTH), Pointing.RIGHT, 20)
				.withItem(new ItemStack(Items.COAL));

		scene.idle(60);
		scene.markAsFinished();
	}
}