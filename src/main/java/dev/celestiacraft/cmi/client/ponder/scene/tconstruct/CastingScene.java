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
		builder.title("casting", "Casting");

		CmiPonderPlugin.init5x5(builder, util);

		BlockPos table0 = util.grid().at(2, 1, 1);

		BlockPos table1 = util.grid().at(3, 1, 3);
		BlockPos table2 = util.grid().at(1, 1, 3);

		BlockPos basin1 = util.grid().at(3, 1, 1);
		BlockPos basin2 = util.grid().at(1, 1, 1);

		BlockPos center = util.grid().at(2, 2, 2);

		builder.idle(5);
		builder.world().showSection(util.select().fromTo(table0, center), Direction.NORTH);

		builder.idle(40);
		builder.addLazyKeyframe();
		builder.overlay().showControls(util.vector().centerOf(table0.above()), Pointing.RIGHT, 20).rightClick();
		builder.idle(5);

		for (int i = 0; i < 4; i++) {
			builder.world().modifyBlockEntity(table0.above(), FaucetBlockEntity.class, (entity) -> {
				entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
			});
			builder.world().modifyBlockEntity(table0, CastingBlockEntity.Table.class, (table) -> {
				table.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.idle(5);
		}
		builder.idle(10);
		builder.world().modifyBlockEntity(table0.above(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		builder.idle(60);

		builder.world().destroyBlock(table0);
		builder.world().destroyBlock(table0.above());

		builder.idle(15);

		builder.world().showSection(util.select().fromTo(table1.above(), basin2), Direction.NORTH);

		builder.idle(5);

		CmiPonderPlugin.rotate(builder, 15, 90);
		builder.idle(15);
		builder.addLazyKeyframe();

		builder.overlay().showControls(util.vector().centerOf(center.east()), Pointing.RIGHT, 20).rightClick();
		builder.idle(15);
		builder.world().modifyBlockEntity(center.east(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
		});
		builder.idle(1);
		builder.world().modifyBlockEntity(center.east().below(), ChannelBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(TinkerFluids.moltenIron.get(), 30));
			entity.setFlow(Direction.NORTH, true);
			entity.setFlow(Direction.SOUTH, true);
		});
		boolean flag1 = true;
		boolean flag2 = true;
		for (int i = 0; i < 27; i++) {
			if (flag1) {
				builder.world().modifyBlockEntity(table1, CastingBlockEntity.Table.class, (table) -> {
					table.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 2) {
					flag1 = false;
					builder.world().modifyBlockEntity(center.east().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.SOUTH, false);
					});
				}
			}
			if (flag2) {
				builder.world().modifyBlockEntity(basin1, CastingBlockEntity.Basin.class, (basin) -> {
					basin.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 26) {
					flag2 = false;
					builder.world().modifyBlockEntity(center.east().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.NORTH, false);
					});
				}
			}
			builder.idle(5);
		}
		builder.world().modifyBlockEntity(center.east(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		builder.idle(200);
		builder.addLazyKeyframe();

		CmiPonderPlugin.rotate(builder, 15, -180);

		builder.idle(15);
		builder.overlay().showText(240)
				.colored(PonderPalette.MEDIUM)
				.text("Scorched casting containers must be used with casts")
				.attachKeyFrame();
		builder.idle(45);

		builder.overlay().showControls(util.vector().topOf(table2), Pointing.RIGHT, 20)
				.withItem(new ItemStack(TinkerSmeltery.ingotCast)).rightClick();
		builder.overlay().showControls(util.vector().topOf(basin2), Pointing.RIGHT, 20)
				.withItem(new ItemStack(TinkerCommons.goldPlatform)).rightClick();
		builder.world().modifyBlockEntity(table2, CastingBlockEntity.Table.class, (table) -> {
			table.setItem(0, new ItemStack(TinkerSmeltery.ingotCast));
		});
		builder.world().modifyBlock(table2, (state) -> {
			return state.setValue(AbstractCastingBlock.HAS_ITEM, true);
		}, false);
		builder.world().modifyBlockEntity(basin2, CastingBlockEntity.Basin.class, (basin) -> {
			basin.setItem(0, new ItemStack(TinkerCommons.goldPlatform));
		});
		builder.world().modifyBlock(basin2, (state) -> {
			return state.setValue(AbstractCastingBlock.HAS_ITEM, true);
		}, false);
		builder.idle(45);

		builder.overlay().showControls(util.vector().centerOf(center.west()), Pointing.RIGHT, 20).rightClick();
		builder.idle(15);
		builder.world().modifyBlockEntity(center.west(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
		});
		builder.idle(1);
		builder.world().modifyBlockEntity(center.west().below(), ChannelBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(TinkerFluids.moltenIron.get(), 30));
			entity.setFlow(Direction.NORTH, true);
			entity.setFlow(Direction.SOUTH, true);
		});
		flag1 = true;
		flag2 = true;
		for (int i = 0; i < 27; i++) {
			if (flag1) {
				builder.world().modifyBlockEntity(table2, CastingBlockEntity.Table.class, (table) -> {
					table.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 2) {
					flag1 = false;
					builder.world().modifyBlockEntity(center.west().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.SOUTH, false);
					});
				}
			}
			if (flag2) {
				builder.world().modifyBlockEntity(basin2, CastingBlockEntity.Basin.class, (basin) -> {
					basin.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
						handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
					});
				});
				if (i == 26) {
					flag2 = false;
					builder.world().modifyBlockEntity(center.west().below(), ChannelBlockEntity.class, (entity) -> {
						entity.setFlow(Direction.NORTH, false);
					});
				}
			}
			builder.idle(5);
		}
		builder.world().modifyBlockEntity(center.west(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		builder.idle(200);
		builder.addLazyKeyframe();

		builder.idle(60);
		builder.markAsFinished();
	}

	public static void sand(SceneBuilder builder, SceneBuildingUtil util) {
		builder.title("sand_casting", "Use Sand Cast");

		CmiPonderPlugin.init5x5(builder, util);

		BlockPos table = util.grid().at(2, 1, 1);
		BlockPos center = util.grid().at(2, 2, 2);

		Selection cast = util.select().fromTo(table, center);

		builder.idle(5);
		builder.world().showSection(cast, Direction.NORTH);

		builder.idle(15);
		builder.overlay().showText(55)
				.colored(PonderPalette.GREEN)
				.text("Sand Cast can only be used once")
				.pointAt(util.vector().topOf(table))
				.attachKeyFrame();
		builder.idle(60);
		builder.overlay().showText(35)
				.colored(PonderPalette.GREEN)
				.text("Right Click with item to make shape")
				.pointAt(util.vector().topOf(table));
		builder.idle(55);

		builder.overlay().showControls(util.vector().topOf(table), Pointing.DOWN, 20)
				.rightClick().withItem(new ItemStack(Items.BRICK));
		builder.idle(5);
		builder.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
			entity.setItem(0, new ItemStack(TinkerSmeltery.ingotCast.getSand()));
			entity.setItem(1, new ItemStack(Items.BRICK));
		});
		builder.idle(25);

		builder.overlay().showControls(util.vector().topOf(table), Pointing.DOWN, 20)
				.rightClick();
		builder.idle(5);
		builder.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
			entity.setItem(1, ItemStack.EMPTY);
		});

		builder.idle(25);
		builder.addLazyKeyframe();
		builder.overlay().showControls(util.vector().centerOf(table.above()), Pointing.RIGHT, 20)
				.rightClick();
		builder.idle(5);
		builder.world().modifyBlockEntity(table.above(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(new FluidStack(TinkerFluids.moltenIron.get(), 30), true);
		});
		builder.idle(5);
		for (int i = 0; i < 3; i++) {
			builder.world().modifyBlockEntity(table, CastingBlockEntity.Table.class, (entity) -> {
				entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent((handler) -> {
					handler.fill(new FluidStack(TinkerFluids.moltenIron.get(), 30), IFluidHandler.FluidAction.EXECUTE);
				});
			});
			builder.idle(5);
		}
		builder.world().modifyBlockEntity(table.above(), FaucetBlockEntity.class, (entity) -> {
			entity.onActivationPacket(FluidStack.EMPTY, false);
		});
		builder.idle(60);
		builder.markAsFinished();
	}
}