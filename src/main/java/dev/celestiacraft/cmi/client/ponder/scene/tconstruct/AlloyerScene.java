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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;

import java.util.Objects;

public class AlloyerScene {
	public static void building(SceneBuilder builder, SceneBuildingUtil util) {
		builder.title("alloyer_building", "Building the Alloyer");

		CmiPonderPlugin.init5x5(builder, util);

		BlockPos fuelTank = util.grid().at(2, 1, 2);
		BlockPos alloyer = util.grid().at(2, 2, 2);

		BlockPos materialTank1 = alloyer.east();
		BlockPos materialTank2 = alloyer.west();

		Selection main = util.select().fromTo(fuelTank, alloyer);
		Selection tank1 = util.select().position(materialTank1);
		Selection tank2 = util.select().position(materialTank2);

		builder.idle(5);
		builder.world().showSection(main, Direction.NORTH);

		builder.idle(20);
		builder.overlay().showOutline(PonderPalette.GREEN, main, main, 130);
		builder.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("The Alloyer is very similar to The Melter")
				.pointAt(util.vector().topOf(alloyer))
				.attachKeyFrame();
		builder.idle(45);
		builder.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("This is a specialized device for making alloys")
				.pointAt(util.vector().topOf(alloyer))
				.attachKeyFrame();
		builder.idle(70);

		builder.idle(25);
		builder.world().showSection(tank1, Direction.NORTH);
		builder.world().showSection(tank2, Direction.NORTH);

		Selection s = util.select().fromTo(materialTank1, materialTank2);
		builder.idle(35);
		builder.overlay().showOutline(PonderPalette.GREEN, s, s, 50);
		builder.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("Place containers on sides of Alloyer")
				.pointAt(util.vector().topOf(alloyer))
				.attachKeyFrame();
		builder.idle(70);

		builder.overlay().showText(25)
				.colored(PonderPalette.GREEN)
				.text("Add fuel with proper temperature")
				.pointAt(util.vector().blockSurface(fuelTank, Direction.NORTH))
				.attachKeyFrame();

		builder.idle(30);
		builder.overlay().showControls(util.vector().blockSurface(fuelTank, Direction.NORTH), Pointing.RIGHT, 20).rightClick()
				.withItem(new ItemStack(Objects.requireNonNull(TinkerFluids.blazingBlood.getBucket())));
		builder.world().modifyBlockEntity(fuelTank, TankBlockEntity.class, (tank) -> {
			tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
				handler.fill(new FluidStack(TinkerFluids.blazingBlood.get(), 4000), IFluidHandler.FluidAction.EXECUTE);
			});
		});
		builder.idle(5);

		for (int i = 0; i < 12; i++) {
			builder.world().modifyBlockEntity(materialTank1, TankBlockEntity.class, (tank) -> {
				tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.drain(270, IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.world().modifyBlockEntity(materialTank2, TankBlockEntity.class, (tank) -> {
				tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.drain(90, IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.world().modifyBlockEntity(alloyer, AlloyerBlockEntity.class, (tank) -> {
				tank.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenManyullyn.get(), 360), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.idle(5);
		}
		builder.idle(25);

		builder.addLazyKeyframe();

		builder.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("Then it will automatically make alloys")
				.pointAt(util.vector().topOf(alloyer));
		builder.idle(45);

		builder.idle(60);
		builder.markAsFinished();
	}
}