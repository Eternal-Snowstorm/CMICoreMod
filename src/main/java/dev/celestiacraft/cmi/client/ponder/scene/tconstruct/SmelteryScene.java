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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.DuctBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;

import java.util.List;

public class SmelteryScene {
	public static void building(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);
		scene.title("smeltery_building", "搭建冶炼炉");

		INebulaSceneBuilder.init9x9(scene, util);

		BlockPos bottomCenter = util.grid().at(4, 1, 4);
		BlockPos controllerPos = util.grid().at(4, 2, 2);

		Selection bottom = util.select().fromTo(5, 1, 3, 3, 1, 5);
		Selection second = util.select().fromTo(6, 1, 2, 2, 2, 6);
		Selection controllers = util.select().fromTo(5, 2, 2, 3, 2, 2);
		Selection fluidFuelTank = util.select().position(5, 2, 2);
		Selection controller = util.select().position(4, 2, 2);
		Selection drain = util.select().position(3, 2, 2);

		scene.idle(5);
		scene.world().showSection(bottom, Direction.DOWN);

		scene.idle(20);
		scene.overlay().showOutline(PonderPalette.GREEN, bottom, bottom, 100);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("冶炼炉的底部是由焦黑方块组成的矩形结构")
				.pointAt(util.vector().topOf(bottomCenter))
				.attachKeyFrame();
		scene.idle(50);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("矩形的最大尺寸为14×14")
				.pointAt(util.vector().topOf(bottomCenter));
		scene.idle(100);

		scene.world().showSection(controllers, Direction.SOUTH);
		scene.idle(20);

		scene.overlay().showOutline(PonderPalette.BLUE, controller, controller, 30);
		scene.overlay().showText(35)
				.colored(PonderPalette.BLUE)
				.text("你需要在第二层外部放置一个冶炼炉控制器\n它是冶炼炉的核心设备")
				.pointAt(util.vector().topOf(controllerPos))
				.attachKeyFrame();
		scene.idle(40);

		scene.overlay().showOutline(PonderPalette.BLUE, fluidFuelTank, fluidFuelTank, 30);
		scene.overlay().showText(35)
				.colored(PonderPalette.BLUE)
				.text("至少需要放置一个燃料储罐")
				.pointAt(util.vector().topOf(controllerPos.east()))
				.attachKeyFrame();
		scene.idle(40);

		scene.overlay().showOutline(PonderPalette.BLUE, drain, drain, 30);
		scene.overlay().showText(35)
				.colored(PonderPalette.BLUE)
				.text("若要进行浇注, 你至少需要放置一个排液口")
				.pointAt(util.vector().topOf(controllerPos.west()))
				.attachKeyFrame();
		scene.idle(40);

		scene.idle(20);
		List<BlockPos> bricks0 = List.of(
				util.grid().at(2, 2, 3), util.grid().at(2, 2, 4), util.grid().at(2, 2, 5),
				util.grid().at(3, 2, 6), util.grid().at(4, 2, 6), util.grid().at(5, 2, 6),
				util.grid().at(6, 2, 5), util.grid().at(6, 2, 4), util.grid().at(6, 2, 3)
		);
		for (BlockPos brick : bricks0) {
			scene.world().showSection(util.select().position(brick), Direction.DOWN);
			scene.idle(1);
		}
		scene.idle(20);

		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("第二层应该是封闭的")
				.pointAt(util.vector().topOf(util.grid().at(4, 2, 6)));
		scene.idle(45);

		scene.overlay().showOutline(PonderPalette.GREEN, second, second, 40);
		scene.overlay().showText(45)
				.colored(PonderPalette.GREEN)
				.text("形成一个顶部开放的空心长方体")
				.pointAt(util.vector().topOf(bottomCenter.above()))
				.attachKeyFrame();
		scene.idle(60);

		INebulaSceneBuilder.rotateAround(scene, 60, 90);

		scene.idle(30);

		BlockPos basin = util.grid().at(2, 1, 2);
		BlockPos table = util.grid().at(3, 1, 1);

		scene.world().showSection(util.select().position(basin.above()), Direction.EAST);
		scene.world().showSection(util.select().position(table.above()), Direction.SOUTH);
		scene.idle(10);
		scene.world().showSection(util.select().position(basin), Direction.DOWN);
		scene.world().showSection(util.select().position(table), Direction.DOWN);
		scene.idle(40);

		scene.overlay().showText(100)
				.colored(PonderPalette.GREEN)
				.text("外墙也可以向上延伸, 最多可达63个方块高")
				.attachKeyFrame();

		scene.idle(10);

		List<BlockPos> bricks1 = List.of(
				util.grid().at(2, 3, 3), util.grid().at(2, 3, 4), util.grid().at(2, 3, 5),
				util.grid().at(3, 3, 6), util.grid().at(4, 3, 6), util.grid().at(5, 3, 6),
				util.grid().at(6, 3, 5), util.grid().at(6, 3, 4), util.grid().at(6, 3, 3),
				util.grid().at(5, 3, 2), util.grid().at(4, 3, 2), util.grid().at(3, 3, 2),

				util.grid().at(2, 4, 3), util.grid().at(2, 4, 4), util.grid().at(2, 4, 5),
				util.grid().at(3, 4, 6), util.grid().at(4, 4, 6), util.grid().at(5, 4, 6),
				util.grid().at(6, 4, 5), util.grid().at(6, 4, 4), util.grid().at(6, 4, 3),
				util.grid().at(5, 4, 2), util.grid().at(4, 4, 2), util.grid().at(3, 4, 2),

				util.grid().at(2, 5, 3), util.grid().at(2, 5, 4), util.grid().at(2, 5, 5),
				util.grid().at(3, 5, 6), util.grid().at(4, 5, 6), util.grid().at(5, 5, 6),
				util.grid().at(6, 5, 5), util.grid().at(6, 5, 4), util.grid().at(6, 5, 3),
				util.grid().at(5, 5, 2), util.grid().at(4, 5, 2), util.grid().at(3, 5, 2),

				util.grid().at(2, 6, 3), util.grid().at(2, 6, 4), util.grid().at(2, 6, 5),
				util.grid().at(3, 6, 6), util.grid().at(4, 6, 6), util.grid().at(5, 6, 6),
				util.grid().at(6, 6, 5), util.grid().at(6, 6, 4), util.grid().at(6, 6, 3),
				util.grid().at(5, 6, 2), util.grid().at(4, 6, 2), util.grid().at(3, 6, 2),

				util.grid().at(2, 7, 3), util.grid().at(2, 7, 4), util.grid().at(2, 7, 5),
				util.grid().at(3, 7, 6), util.grid().at(4, 7, 6), util.grid().at(5, 7, 6),
				util.grid().at(6, 7, 5), util.grid().at(6, 7, 4), util.grid().at(6, 7, 3),
				util.grid().at(5, 7, 2), util.grid().at(4, 7, 2), util.grid().at(3, 7, 2),

				util.grid().at(2, 8, 3), util.grid().at(2, 8, 4), util.grid().at(2, 8, 5),
				util.grid().at(3, 8, 6), util.grid().at(4, 8, 6), util.grid().at(5, 8, 6),
				util.grid().at(6, 8, 5), util.grid().at(6, 8, 4), util.grid().at(6, 8, 3),
				util.grid().at(5, 8, 2), util.grid().at(4, 8, 2), util.grid().at(3, 8, 2),

				util.grid().at(2, 9, 3), util.grid().at(2, 9, 4), util.grid().at(2, 9, 5),
				util.grid().at(3, 9, 6), util.grid().at(4, 9, 6), util.grid().at(5, 9, 6),
				util.grid().at(6, 9, 5), util.grid().at(6, 9, 4), util.grid().at(6, 9, 3),
				util.grid().at(5, 9, 2), util.grid().at(4, 9, 2), util.grid().at(3, 9, 2)
		);
		for (BlockPos brick : bricks1) {
			scene.world().showSection(util.select().position(brick), Direction.DOWN);
			scene.idle(1);
		}

		scene.idle(30);
		scene.overlay().showText(30)
				.colored(PonderPalette.MEDIUM)
				.text("别忘了添加燃料, 例如熔岩")
				.pointAt(util.vector().blockSurface(controllerPos.east(), Direction.NORTH))
				.attachKeyFrame();

		scene.idle(45);
		scene.overlay().showControls(util.vector().blockSurface(controllerPos.east(), Direction.NORTH), Pointing.RIGHT, 20).rightClick()
				.withItem(new ItemStack(Items.LAVA_BUCKET));
		scene.world().modifyBlockEntity(controllerPos.east(), TankBlockEntity.class, (entity) -> {
			entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
				handler.fill(new FluidStack(Fluids.LAVA, 4000), IFluidHandler.FluidAction.EXECUTE);
			});
		});

		scene.idle(60);
		scene.markAsFinished();
	}

	public static void using(SceneBuilder builder, SceneBuildingUtil util) {
		builder.title("smeltery_using", "使用冶炼炉");

		INebulaSceneBuilder.init9x9(builder, util);

		BlockPos controllerPos = util.grid().at(4, 2, 2);
		BlockPos tankPos = util.grid().at(3, 2, 2);

		BlockPos chutePos = util.grid().at(2, 2, 3);
		BlockPos ductPos = util.grid().at(2, 2, 4);
		BlockPos drainPos = util.grid().at(2, 2, 5);

		Selection smeltery = util.select().fromTo(2, 1, 2, 6, 4, 6);
		Selection chute = util.select().position(chutePos);
		Selection duct = util.select().position(ductPos);
		Selection drain = util.select().position(drainPos);

		builder.idle(5);
		builder.world().showSection(smeltery, Direction.NORTH);

		builder.idle(30);
		builder.overlay().showText(30)
				.text("右键冶炼炉控制器打开冶炼炉的GUI")
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(controllerPos, Direction.NORTH));
		builder.idle(45);
		builder.overlay().showText(30)
				.text("你可以将物品放入GUI内开始熔化")
				.colored(PonderPalette.MEDIUM)
				.pointAt(util.vector().blockSurface(controllerPos, Direction.NORTH))
				.attachKeyFrame();
		builder.idle(45);
		builder.overlay().showText(45)
				.text("接下来将介绍一些功能性方块")
				.colored(PonderPalette.MEDIUM);
		builder.idle(15);

		INebulaSceneBuilder.rotate(builder, 15, -90);

		builder.idle(30);
		builder.overlay().showOutline(PonderPalette.GREEN, chute, chute, 30);
		builder.idle(5);
		builder.overlay().showText(45)
				.text("滑槽可以向冶炼炉输入和输出物品")
				.colored(PonderPalette.GREEN)
				.pointAt(util.vector().blockSurface(chutePos, Direction.WEST))
				.attachKeyFrame();
		builder.idle(60);

		builder.idle(20);
		builder.world().showSection(util.select().position(chutePos.west()), Direction.NORTH);
		builder.idle(15);
		ElementLink<EntityElement> itemLink = builder.world().createItemEntity(
				chutePos.west().above().getCenter(),
				util.vector().of(0, 0.1, 0),
				new ItemStack(Items.RAW_GOLD)
		);
		builder.idle(10);
		builder.world().modifyEntity(itemLink, Entity::discard);

		builder.idle(30);
		builder.overlay().showOutline(PonderPalette.GREEN, drain, drain.add(duct), 30);
		builder.idle(5);
		builder.overlay().showText(45)
				.text("排液口和输导孔可以向冶炼炉输入和输出流体")
				.colored(PonderPalette.GREEN)
				.pointAt(util.vector().blockSurface(ductPos, Direction.WEST))
				.attachKeyFrame();

		builder.idle(80);
		builder.overlay().showOutline(PonderPalette.GREEN, duct, duct, 30);
		builder.idle(5);
		builder.overlay().showText(45)
				.text("输导孔有一个带槽位的GUI, 用于过滤流体")
				.colored(PonderPalette.GREEN)
				.pointAt(util.vector().blockSurface(ductPos, Direction.WEST))
				.attachKeyFrame();
		builder.idle(60);
		builder.overlay().showText(45)
				.text("试着放入一个装有熔融金的铜罐")
				.colored(PonderPalette.GREEN)
				.pointAt(util.vector().blockSurface(ductPos, Direction.WEST));
		builder.idle(60);
		ItemStack can = new ItemStack(TinkerSmeltery.copperCan);
		CopperCanItem.setFluid(can, new FluidStack(TinkerFluids.moltenGold.get(), 90));
		builder.overlay().showControls(util.vector().blockSurface(ductPos, Direction.WEST), Pointing.RIGHT, 20)
				.withItem(can.copy());
		builder.world().modifyBlockEntityNBT(util.select().position(ductPos), DuctBlockEntity.class, (tag) -> {
			tag.put("item", can.save(new CompoundTag()));
		}, true);

		builder.idle(35);
		builder.world().showSection(util.select().position(ductPos.west()), Direction.NORTH);
		builder.world().showSection(util.select().position(ductPos.west().below()), Direction.NORTH);
		builder.world().showSection(util.select().position(drainPos.west()), Direction.NORTH);
		builder.world().showSection(util.select().position(drainPos.west().below()), Direction.NORTH);

		builder.idle(35);

		builder.addLazyKeyframe();
		builder.overlay().showControls(util.vector().blockSurface(drainPos.west(), Direction.SOUTH), Pointing.LEFT, 20).rightClick();
		builder.overlay().showControls(util.vector().blockSurface(ductPos.west(), Direction.NORTH), Pointing.RIGHT, 20).rightClick();

		builder.idle(5);

		for (int i = 0; i < 4; i++) {
			builder.world().modifyBlockEntity(drainPos.west(), FaucetBlockEntity.class, (entity) -> {
				entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
			});
			builder.world().modifyBlockEntity(ductPos.west(), FaucetBlockEntity.class, (entity) -> {
				entity.onActivationPacket(new FluidStack(TinkerFluids.moltenGold.get(), 30), true);
			});
			builder.world().modifyBlockEntity(drainPos.west().below(), CastingBlockEntity.Table.class, (entity) -> {
				entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.world().modifyBlockEntity(ductPos.west().below(), CastingBlockEntity.Table.class, (entity) -> {
				entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenGold.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.idle(5);
		}
		builder.idle(10);
		builder.world().modifyBlockEntity(drainPos.west(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		builder.world().modifyBlockEntity(ductPos.west(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		builder.idle(40);
		builder.world().modifyBlockEntity(drainPos.west().below(), CastingBlockEntity.Table.class, (entity) -> {
			entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
				handler.insertItem(1, new ItemStack(Items.IRON_BARS), false);
			});
		});
		builder.world().modifyBlockEntity(ductPos.west().below(), CastingBlockEntity.Table.class, (entity) -> {
			entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
				handler.insertItem(1, new ItemStack(TinkerCommons.goldBars), false);
			});
		});

		builder.idle(60);
		builder.markAsFinished();
	}

	public static void mini(SceneBuilder builder, SceneBuildingUtil util) {
		builder.title("smeltery_mini", "迷你冶炼炉");

		INebulaSceneBuilder.init7x7(builder, util);

		builder.idle(5);
		builder.world().showSection(util.select().layers(1, 2), Direction.UP);

		builder.idle(25);

		INebulaSceneBuilder.rotateAround(builder, 100, 90);

		builder.idle(60);
		builder.markAsFinished();
	}
}