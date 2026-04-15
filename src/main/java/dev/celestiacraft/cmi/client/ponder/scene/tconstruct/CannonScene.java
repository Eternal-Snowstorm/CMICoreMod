package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
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
	public static void using(SceneBuilder scene, SceneBuildingUtil util) {
		NebulaSceneBuilder builder = new NebulaSceneBuilder(scene);

		scene.title("fluid_cannon_using", "Using Fluid Cannons");

		NebulaSceneBuilder.init5x5(scene, util);

		BlockPos copper = util.grid().at(1, 1, 2);
		BlockPos cobalt = util.grid().at(3, 1, 2);

		scene.idle(15);
		scene.world().showSection(util.select().fromTo(cobalt, cobalt.above()), Direction.NORTH);
		scene.world().showSection(util.select().fromTo(copper, copper.above()), Direction.NORTH);

		scene.idle(30);

		scene.world().modifyBlockEntity(copper, FluidCannonBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(Fluids.LAVA, 2000));
		});

		scene.world().modifyBlockEntity(cobalt, FluidCannonBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(Fluids.WATER, 2000));
		});

		scene.idle(20);
		scene.overlay().showText(55)
				.text("Fluid Cannon can shoot fluid and different fluids have different effects")
				.colored(PonderPalette.MEDIUM)
				.attachKeyFrame();
		scene.idle(60);

		scene.world().modifyBlock(copper.above(), (state) -> {
			return state.setValue(LeverBlock.POWERED, true);
		}, false);
		scene.world().modifyBlock(cobalt.above(), (state) -> {
			return state.setValue(LeverBlock.POWERED, true);
		}, false);
		scene.idle(25);

		scene.idle(60);
		scene.markAsFinished();
	}
}