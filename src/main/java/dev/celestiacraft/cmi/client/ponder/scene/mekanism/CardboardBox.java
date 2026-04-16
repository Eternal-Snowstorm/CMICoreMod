package dev.celestiacraft.cmi.client.ponder.scene.mekanism;

import dev.celestiacraft.cmi.utils.ModResources;
import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import dev.celestiacraft.libs.server.block.state.StateHelper;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.item.block.ItemBlockCardboardBox;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;

public class CardboardBox {
	private static final BlockRegistryObject<BlockCardboardBox, ItemBlockCardboardBox> CARDBOARD_BOX =
			MekanismBlocks.CARDBOARD_BOX;

	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);

		NebulaSceneBuilder.init5x5(scene, util);

		scene.title("cardboard_box", "集装箱用法");
		scene.idle(25);

		scene.addKeyframe();

		scene.world().setBlock(util.grid().at(2, 1, 2), CARDBOARD_BOX.getBlock().defaultBlockState(), false);
		scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
		scene.idle(20);
		scene.text(40, "集装箱可以用于方块的转移", util.vector().centerOf(2, 1, 2));
		scene.idle(50);

		scene.text(40, "接下来我们演示一个例子", util.vector().centerOf(2, 1, 2));
		scene.idle(50);
		scene.world().hideSection(util.select().position(2, 1, 2), Direction.UP);
		scene.idle(30);

		scene.addKeyframe();
		scene.world().setBlock(util.grid().at(2, 1, 2), Blocks.SPAWNER.defaultBlockState(), false);
		scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
		scene.idle(20);

		scene.text(40, "这是一个刷怪笼", util.vector().centerOf(2, 1, 2));
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().position(2, 1, 2),
				40
		);
		scene.idle(50);

		scene.addKeyframe();
		scene.text(40, "拿集装箱右键一下...");
		scene.idle(50);
		scene.showControls(20, util.vector().of(2.5, 2, 2.5), Pointing.DOWN)
				.builder()
				.rightClick()
				.withItem(CARDBOARD_BOX.getItemStack());
		scene.idle(20);

		scene.text(40, "潜行+右键取掉集装箱", util.vector().centerOf(2, 1, 2));
		scene.world().setBlock(util.grid().at(2, 1, 2), CARDBOARD_BOX.getBlock().defaultBlockState(), false);
		scene.world().modifyBlock(util.grid().at(2, 1, 2), (state) -> {
			return StateHelper.with(state, "storage", "true");
		}, false);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().fromTo(2, 1, 2, 2, 1, 2),
				40
		);
		scene.idle(50);
		scene.showControls(20, util.vector().of(2.5, 2, 2.5), Pointing.DOWN)
				.builder()
				.rightClick()
				.whileSneaking();
		scene.idle(20);

		scene.world().setBlock(util.grid().at(2, 1, 2), Blocks.SPAWNER.defaultBlockState(), false);
		ElementLink<EntityElement> containerItem2 = scene.world().createItemEntity(
				util.vector().centerOf(2, 2, 2),
				util.vector().of(0, -0.1, 0),
				CARDBOARD_BOX.getItemStack()
		);
		scene.idle(20);
		scene.world().removeEntity(containerItem2);
		scene.world().hideSection(util.select().position(2, 1, 2), Direction.UP);
		scene.idle(20);

		scene.addKeyframe();
		scene.text(40, "但是...");
		scene.world().setBlock(util.grid().at(2, 1, 2), ModResources.IRON_DEPOSIT_BLOCK.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(1, 1, 2), Blocks.IRON_ORE.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(3, 1, 1), Blocks.IRON_ORE.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(2, 1, 3), Blocks.RAW_IRON_BLOCK.defaultBlockState(), false);
		scene.world().showIndependentSection(util.select().fromTo(3, 1, 1, 1, 1, 3), Direction.DOWN);
		scene.idle(50);
		scene.text(60, "会有部分的方块无法被包裹在其中");
		scene.idle(70);
		scene.showControls(20, util.vector().of(2.5, 2, 2.5), Pointing.DOWN)
				.builder()
				.rightClick()
				.withItem(Blocks.BARRIER.asItem().getDefaultInstance());

		scene.idle(20);
		scene.markAsFinished();
	}
}