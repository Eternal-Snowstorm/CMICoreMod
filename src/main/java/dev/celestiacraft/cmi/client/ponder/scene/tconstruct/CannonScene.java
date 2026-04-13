package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.cmi.client.ponder.CmiPonderPlugin;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.block.entity.FluidCannonBlockEntity;

public class CannonScene {
	public static void using(SceneBuilder builder, SceneBuildingUtil util) {
		builder.title("fluid_cannon_using", "Using Fluid Cannons");

		CmiPonderPlugin.init5x5(builder, util);

		BlockPos copper = util.grid().at(1, 1, 2);
		BlockPos cobalt = util.grid().at(3, 1, 2);

		builder.idle(15);
		builder.world().showSection(util.select().fromTo(cobalt, cobalt.above()), Direction.NORTH);
		builder.world().showSection(util.select().fromTo(copper, copper.above()), Direction.NORTH);

		builder.idle(30);

		builder.world().modifyBlockEntity(copper, FluidCannonBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(Fluids.LAVA, 2000));
		});

		builder.world().modifyBlockEntity(cobalt, FluidCannonBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(Fluids.WATER, 2000));
		});

		builder.idle(20);
		builder.overlay().showText(55)
				.text("Fluid Cannon can shoot fluid and different fluids have different effects")
				.colored(PonderPalette.MEDIUM)
				.attachKeyFrame();
		builder.idle(60);

		builder.world().modifyBlock(copper.above(), s -> s.setValue(LeverBlock.POWERED, true), false);
		builder.world().modifyBlock(cobalt.above(), s -> s.setValue(LeverBlock.POWERED, true), false);
		builder.idle(25);

		builder.idle(60);
		builder.markAsFinished();
	}
}