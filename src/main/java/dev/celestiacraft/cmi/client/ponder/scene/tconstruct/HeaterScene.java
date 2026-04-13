package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.cmi.client.ponder.CmiPonderPlugin;
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
		builder.title("heater_using", "Use the Heater");

		CmiPonderPlugin.init5x5(builder, util);

		BlockPos center = util.grid().at(2, 1, 2);

		BlockPos alloyer = center.above().east();
		BlockPos melter = center.above().west();

		Selection all = util.select().fromTo(melter.below(), alloyer);

		builder.idle(5);
		builder.world().showSection(util.select().position(center), Direction.NORTH);
		builder.idle(15);
		builder.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("The Heater can burn solid fuel to provide a temperature of 800℃")
				.pointAt(util.vector().topOf(center))
				.attachKeyFrame();
		builder.idle(45);

		builder.idle(15);
		builder.world().destroyBlock(center);
		builder.idle(15);
		builder.world().showSection(all, Direction.NORTH);

		builder.idle(20);

		builder.addLazyKeyframe();
		builder.overlay().showControls(util.vector().blockSurface(alloyer.below(), Direction.NORTH), Pointing.RIGHT, 20)
				.withItem(new ItemStack(Items.COAL));
		builder.overlay().showControls(util.vector().blockSurface(melter.below(), Direction.NORTH), Pointing.RIGHT, 20)
				.withItem(new ItemStack(Items.COAL));

		builder.idle(60);
		builder.markAsFinished();
	}
}