package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.libs.client.ponder.CreateNebulaSceneBuilder;
import dev.celestiacraft.libs.client.ponder.INebulaSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.MelterBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.MelterBlockEntity;

public class MelterScene {
	public static void building(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);
		scene.title("melter_building", "搭建熔化炉");

		INebulaSceneBuilder.init5x5(scene, util);

		Selection smeltery = util.select().fromTo(2, 1, 2, 2, 2, 2);
		Selection basin = util.select().fromTo(1, 1, 2, 1, 2, 2);
		Selection table = util.select().fromTo(3, 1, 2, 3, 2, 2);
		BlockPos melter = util.grid().at(2, 2, 2);

		scene.idle(5);
		scene.world().showSection(util.select().position(melter.below()), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(melter), Direction.DOWN);

		scene.idle(20);
		scene.overlay().showOutline(PonderPalette.GREEN, smeltery, util.select().fromTo(2, 1, 2, 2, 2, 2), 80);

		scene.idle(25);
		scene.overlay().showText(40)
				.text("熔化炉是你作为工匠的第一个冶炼设备")
				.attachKeyFrame()
				.colored(PonderPalette.GREEN)
				.pointAt(util.vector().topOf(melter));

		scene.idle(25);
		scene.overlay().showText(40)
				.text("它需要加热器来提供热量")
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(melter.below(), Direction.WEST));

		scene.idle(60);
		scene.overlay().showText(40)
				.text("而加热器需要燃料才能工作")
				.attachKeyFrame()
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(melter.below(), Direction.WEST));
		scene.idle(45);

		scene.overlay().showControls(util.vector().blockSurface(melter.below(), Direction.NORTH), Pointing.RIGHT, 20)
				.withItem(new ItemStack(Items.COAL));
		scene.world().modifyBlock(melter.below(), (state) -> {
			return state.setValue(ControllerBlock.ACTIVE, true);
		}, false);

		scene.idle(40);
		scene.overlay().showText(40)
				.text("或者你也可以使用燃料储罐")
				.attachKeyFrame()
				.colored(PonderPalette.GREEN)
				.pointAt(util.vector().blockSurface(melter.below(), Direction.WEST));

		scene.idle(25);
		scene.world().destroyBlock(melter.below());
		scene.idle(5);
		scene.world().setBlock(melter.below(), TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.FUEL_TANK).defaultBlockState(), false);

		scene.idle(30);
		scene.overlay().showControls(util.vector().blockSurface(melter.below(), Direction.NORTH), Pointing.RIGHT, 20).rightClick()
				.withItem(new ItemStack(Items.LAVA_BUCKET));
		scene.world().modifyBlockEntity(melter.below(), TankBlockEntity.class, (entity) -> {
			entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
				handler.fill(new FluidStack(Fluids.LAVA, 4000), IFluidHandler.FluidAction.EXECUTE);
			});
		});

		scene.overlay().showText(25)
				.text("当然, 不要忘记往里添加燃料, 例如熔岩")
				.attachKeyFrame()
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(melter.below(), Direction.WEST));
		scene.idle(60);

		scene.overlay().showText(20)
				.text("最后安装铸造部件")
				.attachKeyFrame()
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(melter, Direction.UP));

		scene.idle(30);
		scene.world().showSection(basin, Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(table, Direction.DOWN);
		scene.idle(40);

		scene.markAsFinished();
	}

	public static void using(SceneBuilder builder, SceneBuildingUtil util) {
		builder.title("melter_using", "熔化与铸造");

		INebulaSceneBuilder.init5x5(builder, util);

		BlockPos table = util.grid().at(1, 1, 2);
		BlockPos basin = util.grid().at(3, 1, 2);
		BlockPos melter = util.grid().at(2, 2, 2);
		Selection selection = util.select().fromTo(basin, table.above());

		builder.idle(5);
		builder.world().showSection(selection, Direction.DOWN);
		builder.idle(10);
		builder.overlay().showText(20)
				.text("通过熔化获得熔融材料")
				.attachKeyFrame()
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(melter, Direction.UP));
		builder.idle(30);

		ElementLink<EntityElement> itemLink = builder.world().createItemEntity(
				melter.above().getCenter(),
				util.vector().of(0, 0.1, 0),
				new ItemStack(Items.RAW_GOLD)
		);
		builder.idle(10);
		builder.world().modifyEntity(itemLink, Entity::discard);
		builder.world().modifyBlockEntity(melter, MelterBlockEntity.class, (entity) -> {
			entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
				handler.insertItem(0, new ItemStack(Items.RAW_GOLD), false);
			});
		});
		builder.world().modifyBlock(melter, (state) -> {
			return state.setValue(MelterBlock.ACTIVE, true);
		}, false);

		builder.idle(10);
		builder.overlay().showText(20)
				.text("等待熔化...")
				.attachKeyFrame()
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(melter, Direction.NORTH));
		builder.idle(80);

		builder.world().modifyBlockEntity(melter, MelterBlockEntity.class, (entity) -> {
			entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
				handler.extractItem(0, 1, false);
			});
			entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
				handler.fill(new FluidStack(TinkerFluids.moltenGold.get(), 120), IFluidHandler.FluidAction.EXECUTE);
			});
		});
		builder.world().modifyBlock(melter, (state) -> {
			return state.setValue(MelterBlock.ACTIVE, false);
		}, false);

		builder.idle(20);
		builder.overlay().showText(35)
				.text("右键点击浇筑口进行浇筑")
				.attachKeyFrame()
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(table.above(), Direction.EAST));

		builder.idle(55);

		builder.overlay().showControls(util.vector().blockSurface(table.above(), Direction.EAST), Pointing.RIGHT, 20)
				.rightClick();
		for (int i = 0; i < 4; i++) {
			builder.world().modifyBlockEntity(table.above(), FaucetBlockEntity.class, (entity) -> {
				entity.onActivationPacket(new FluidStack(TinkerFluids.moltenGold.get(), 30), true);
			});
			builder.world().modifyBlockEntity(melter, MelterBlockEntity.class, (entity) -> {
				entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.drain(new FluidStack(TinkerFluids.moltenGold.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
				entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenGold.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.idle(5);
		}
		builder.idle(10);
		builder.world().modifyBlockEntity(table.above(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		builder.idle(40);
		builder.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
			entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
				handler.getStackInSlot(0).setCount(0);
			});
		});

		builder.idle(60);
		builder.markAsFinished();
	}
}