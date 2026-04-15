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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;

import java.util.Objects;

public class AlloyerScene {
	public static void building(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);

		scene.title("alloyer_building", "Building the Alloyer");
		NebulaSceneBuilder.init5x5(scene, util);

		BlockPos fuelTank = util.grid().at(2, 1, 2);
		BlockPos alloyer = util.grid().at(2, 2, 2);

		BlockPos materialTank1 = alloyer.east();
		BlockPos materialTank2 = alloyer.west();

		Selection main = util.select().fromTo(fuelTank, alloyer);
		Selection tank1 = util.select().position(materialTank1);
		Selection tank2 = util.select().position(materialTank2);

		scene.idle(5);
		scene.world().showSection(main, Direction.NORTH);

		scene.idle(20);
		scene.overlay().showOutline(PonderPalette.GREEN, NebulaSceneBuilder.OBJECT, main, 130);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("The Alloyer is very similar to The Melter")
				.pointAt(util.vector().topOf(alloyer))
				.attachKeyFrame();
		scene.idle(45);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("This is a specialized device for making alloys")
				.pointAt(util.vector().topOf(alloyer))
				.attachKeyFrame();
		scene.idle(70);

		scene.idle(25);
		scene.world().showSection(tank1, Direction.NORTH);
		scene.world().showSection(tank2, Direction.NORTH);

		Selection s = util.select().fromTo(materialTank1, materialTank2);
		scene.idle(35);
		scene.overlay().showOutline(PonderPalette.GREEN, NebulaSceneBuilder.OBJECT, s, 50);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("Place containers on sides of Alloyer")
				.pointAt(util.vector().topOf(alloyer))
				.attachKeyFrame();
		scene.idle(70);

		scene.overlay().showText(25)
				.colored(PonderPalette.GREEN)
				.text("Add fuel with proper temperature")
				.pointAt(util.vector().blockSurface(fuelTank, Direction.NORTH))
				.attachKeyFrame();

		scene.idle(30);
		scene.overlay().showControls(util.vector().blockSurface(fuelTank, Direction.NORTH), Pointing.RIGHT, 20).rightClick()
				.withItem(new ItemStack(Objects.requireNonNull(TinkerFluids.blazingBlood.getBucket())));
		scene.world().modifyBlockEntity(fuelTank, TankBlockEntity.class, (tank) -> {
			tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
				handler.fill(new FluidStack(TinkerFluids.blazingBlood.get(), 4000), IFluidHandler.FluidAction.EXECUTE);
			});
		});
		scene.idle(5);

		for (int i = 0; i < 12; i++) {
			scene.world().modifyBlockEntity(materialTank1, TankBlockEntity.class, (tank) -> {
				tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.drain(270, IFluidHandler.FluidAction.EXECUTE);
				});
			});
			scene.world().modifyBlockEntity(materialTank2, TankBlockEntity.class, (tank) -> {
				tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.drain(90, IFluidHandler.FluidAction.EXECUTE);
				});
			});
			scene.world().modifyBlockEntity(alloyer, AlloyerBlockEntity.class, (tank) -> {
				tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenManyullyn.get(), 360), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			scene.idle(5);
		}
		scene.idle(25);

		scene.addLazyKeyframe();

		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("Then it will automatically make alloys")
				.pointAt(util.vector().topOf(alloyer));
		scene.idle(45);

		scene.idle(60);
		scene.markAsFinished();
	}
}