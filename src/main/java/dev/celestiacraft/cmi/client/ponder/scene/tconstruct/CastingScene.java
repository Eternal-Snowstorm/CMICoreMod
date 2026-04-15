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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.AbstractCastingBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.FaucetBlockEntity;

public class CastingScene {
	public static void cast(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);
		scene.title("casting", "Casting");

		NebulaSceneBuilder.init5x5(scene, util);

		BlockPos table0 = util.grid().at(2, 1, 1);

		BlockPos table1 = util.grid().at(3, 1, 3);
		BlockPos table2 = util.grid().at(1, 1, 3);

		BlockPos basin1 = util.grid().at(3, 1, 1);
		BlockPos basin2 = util.grid().at(1, 1, 1);

		BlockPos center = util.grid().at(2, 2, 2);

		scene.idle(5);
		scene.world().showSection(util.select().fromTo(table0, center), Direction.NORTH);

		scene.idle(40);
		scene.addLazyKeyframe();
		scene.overlay().showControls(util.vector().centerOf(table0.above()), Pointing.RIGHT, 20).rightClick();
		scene.idle(5);

		for (int i = 0; i < 4; i++) {
			scene.world().modifyBlockEntity(table0.above(), FaucetBlockEntity.class, (entity) -> {
				entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
			});
			scene.world().modifyBlockEntity(table0, CastingBlockEntity.Table.class, (table) -> {
				table.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			scene.idle(5);
		}
		scene.idle(10);
		scene.world().modifyBlockEntity(table0.above(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		scene.idle(60);

		scene.world().destroyBlock(table0);
		scene.world().destroyBlock(table0.above());

		scene.idle(15);

		scene.world().showSection(util.select().fromTo(table1.above(), basin2), Direction.NORTH);

		scene.idle(5);

		NebulaSceneBuilder.rotate(scene, 15, 90);
		scene.idle(15);
		scene.addLazyKeyframe();

		scene.overlay().showControls(util.vector().centerOf(center.east()), Pointing.RIGHT, 20).rightClick();
		scene.idle(15);
		scene.world().modifyBlockEntity(center.east(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
		});
		scene.idle(1);
		scene.world().modifyBlockEntity(center.east().below(), ChannelBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(TinkerFluids.moltenIron.get(), 30));
			entity.setFlow(Direction.NORTH, true);
			entity.setFlow(Direction.SOUTH, true);
		});
		boolean flag1 = true;
		boolean flag2 = true;
		for (int i = 0; i < 27; i++) {
			if (flag1) {
				scene.world().modifyBlockEntity(table1, CastingBlockEntity.Table.class, (table) -> {
					table.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 2) {
					flag1 = false;
					scene.world().modifyBlockEntity(center.east().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.SOUTH, false);
					});
				}
			}
			if (flag2) {
				scene.world().modifyBlockEntity(basin1, CastingBlockEntity.Basin.class, (basin) -> {
					basin.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 26) {
					flag2 = false;
					scene.world().modifyBlockEntity(center.east().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.NORTH, false);
					});
				}
			}
			scene.idle(5);
		}
		scene.world().modifyBlockEntity(center.east(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		scene.idle(200);
		scene.addLazyKeyframe();

		NebulaSceneBuilder.rotate(scene, 15, -180);

		scene.idle(15);
		scene.overlay().showText(240)
				.colored(PonderPalette.MEDIUM)
				.text("Scorched casting containers must be used with casts")
				.attachKeyFrame();
		scene.idle(45);

		scene.overlay().showControls(util.vector().topOf(table2), Pointing.RIGHT, 20)
				.withItem(new ItemStack(TinkerSmeltery.ingotCast)).rightClick();
		scene.overlay().showControls(util.vector().topOf(basin2), Pointing.RIGHT, 20)
				.withItem(new ItemStack(TinkerCommons.goldPlatform)).rightClick();
		scene.world().modifyBlockEntity(table2, CastingBlockEntity.Table.class, (table) -> {
			table.setItem(0, new ItemStack(TinkerSmeltery.ingotCast));
		});
		scene.world().modifyBlock(table2, (state) -> {
			return state.setValue(AbstractCastingBlock.HAS_ITEM, true);
		}, false);
		scene.world().modifyBlockEntity(basin2, CastingBlockEntity.Basin.class, (basin) -> {
			basin.setItem(0, new ItemStack(TinkerCommons.goldPlatform));
		});
		scene.world().modifyBlock(basin2, (state) -> {
			return state.setValue(AbstractCastingBlock.HAS_ITEM, true);
		}, false);
		scene.idle(45);

		scene.overlay().showControls(util.vector().centerOf(center.west()), Pointing.RIGHT, 20).rightClick();
		scene.idle(15);
		scene.world().modifyBlockEntity(center.west(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
		});
		scene.idle(1);
		scene.world().modifyBlockEntity(center.west().below(), ChannelBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(TinkerFluids.moltenIron.get(), 30));
			entity.setFlow(Direction.NORTH, true);
			entity.setFlow(Direction.SOUTH, true);
		});
		flag1 = true;
		flag2 = true;
		for (int i = 0; i < 27; i++) {
			if (flag1) {
				scene.world().modifyBlockEntity(table2, CastingBlockEntity.Table.class, (table) -> {
					table.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 2) {
					flag1 = false;
					scene.world().modifyBlockEntity(center.west().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.SOUTH, false);
					});
				}
			}
			if (flag2) {
				scene.world().modifyBlockEntity(basin2, CastingBlockEntity.Basin.class, (basin) -> {
					basin.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 26) {
					flag2 = false;
					scene.world().modifyBlockEntity(center.west().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.NORTH, false);
					});
				}
			}
			scene.idle(5);
		}
		scene.world().modifyBlockEntity(center.west(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		scene.idle(200);
		scene.addLazyKeyframe();

		scene.idle(60);
		scene.markAsFinished();
	}

	public static void sand(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);
		scene.title("sand_casting", "Use Sand Cast");

		NebulaSceneBuilder.init5x5(scene, util);

		BlockPos table = util.grid().at(2, 1, 1);
		BlockPos center = util.grid().at(2, 2, 2);

		Selection cast = util.select().fromTo(table, center);

		scene.idle(5);
		scene.world().showSection(cast, Direction.NORTH);

		scene.idle(15);
		scene.overlay().showText(55)
				.colored(PonderPalette.GREEN)
				.text("Sand Cast can only be used once")
				.pointAt(util.vector().topOf(table))
				.attachKeyFrame();
		scene.idle(60);
		scene.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("Right Click with item to make shape")
				.pointAt(util.vector().topOf(table));
		scene.idle(55);

		scene.overlay().showControls(util.vector().topOf(table), Pointing.DOWN, 20)
				.rightClick().withItem(new ItemStack(Items.BRICK));
		scene.idle(5);
		scene.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
			entity.setItem(0, new ItemStack(TinkerSmeltery.ingotCast.getSand()));
			entity.setItem(1, new ItemStack(Items.BRICK));
		});
		scene.idle(25);

		scene.overlay().showControls(util.vector().topOf(table), Pointing.DOWN, 20)
				.rightClick();
		scene.idle(5);
		scene.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
			entity.setItem(1, ItemStack.EMPTY);
		});

		scene.idle(25);
		scene.addLazyKeyframe();
		scene.overlay().showControls(util.vector().centerOf(table.above()), Pointing.RIGHT, 20)
				.rightClick();
		scene.idle(5);
		scene.world().modifyBlockEntity(table.above(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
		});
		scene.idle(5);
		for (int i = 0; i < 3; i++) {
			scene.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
				entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			scene.idle(5);
		}
		scene.world().modifyBlockEntity(table.above(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		scene.idle(60);
		scene.markAsFinished();
	}
}