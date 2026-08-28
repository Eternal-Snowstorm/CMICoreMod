package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.libs.client.ponder.CreateNebulaSceneBuilder;
import dev.celestiacraft.libs.client.ponder.INebulaSceneBuilder;
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
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);
		scene.title("heater_using", "使用加热器");

		INebulaSceneBuilder.init5x5(scene, util);

		BlockPos center = util.grid().at(2, 1, 2);

		BlockPos alloyer = center.above().east();
		BlockPos melter = center.above().west();

		Selection all = util.select().fromTo(melter.below(), alloyer);

		scene.idle(5);
		scene.world().showSection(util.select().position(center), Direction.NORTH);
		scene.idle(15);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("加热器可以燃烧固体燃料，最高可提供937°C的温度")
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