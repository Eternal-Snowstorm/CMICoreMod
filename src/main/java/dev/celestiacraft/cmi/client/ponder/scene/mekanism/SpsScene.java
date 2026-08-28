package dev.celestiacraft.cmi.client.ponder.scene.mekanism;

import dev.celestiacraft.libs.client.ponder.CreateNebulaSceneBuilder;
import dev.celestiacraft.libs.server.block.state.StateHelper;
import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.multiblock.TileEntitySPSPort;
import mekanism.common.tile.multiblock.TileEntitySuperchargedCoil;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

public class SpsScene {
	private static final BlockRegistryObject<
			BlockBasicMultiblock<TileEntitySPSPort>,
			ItemBlockTooltip<BlockBasicMultiblock<TileEntitySPSPort>>
			> SPS_PORT = MekanismBlocks.SPS_PORT;

	private static final BlockRegistryObject<
			BlockTile.BlockTileModel<TileEntitySuperchargedCoil, BlockTypeTile<
					TileEntitySuperchargedCoil>>, ItemBlockTooltip<
			BlockTile.BlockTileModel<TileEntitySuperchargedCoil,
					BlockTypeTile<TileEntitySuperchargedCoil>>>
			> SUPERCHARGED_COIL = MekanismBlocks.SUPERCHARGED_COIL;

	public static void building(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);

		scene.title("sps_building", "超临界移相器搭建");

		scene.scaleSceneView(0.3f);
		scene.showStructure(0);
		scene.idle(25);

		scene.addKeyframe();
		scene.idle(5);

		scene.world().showSection(util.select().fromTo(0, 2, 0, 8, 2, 8), Direction.DOWN);
		scene.idle(15);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().fromTo(1, 2, 1, 7, 2, 7),
				60
		);
		scene.text(60, "使用超临界移相器外壳与结构玻璃搭出这样的形状", util.vector().centerOf(1, 3, 1));
		scene.idle(75);

		scene.addKeyframe();
		scene.text(40, "再以棱对棱的形式把同样的这个图形搭满6个面");
		scene.idle(55);
		scene.rotateCameraY(180);

		scene.addKeyframe();
		scene.idle(30);
		scene.world().showSection(util.select().fromTo(1, 3, 1, 7, 8, 1), Direction.DOWN);
		scene.idle(30);
		scene.world().showSection(util.select().fromTo(1, 3, 2, 1, 8, 7), Direction.DOWN);
		scene.idle(30);

		ElementLink<WorldSectionElement> rem1 = scene.world().showIndependentSection(
				util.select().fromTo(7, 3, 2, 7, 8, 7),
				Direction.DOWN
		);
		scene.idle(30);
		ElementLink<WorldSectionElement> rem2 = scene.world().showIndependentSection(
				util.select().fromTo(2, 3, 7, 6, 8, 7),
				Direction.DOWN
		);
		scene.idle(30);
		ElementLink<WorldSectionElement> rem3 = scene.world().showIndependentSection(
				util.select().fromTo(2, 8, 6, 6, 8, 2),
				Direction.DOWN
		);
		scene.idle(30);

		scene.addKeyframe();
		scene.text(40, "但是，我们稍微移开一下它");
		scene.idle(55);
		scene.world().hideIndependentSection(rem1, Direction.UP);
		scene.world().hideIndependentSection(rem2, Direction.UP);
		scene.world().hideIndependentSection(rem3, Direction.UP);

		scene.idle(30);
		scene.world().setBlock(util.grid().at(4, 2, 4), SPS_PORT.getBlock().defaultBlockState(), false);
		scene.idle(20);

		scene.addKeyframe();
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().fromTo(4, 2, 4, 4, 2, 4),
				60
		);
		scene.text(60, "在这个地方放置超临界移相器端口并通上电", util.vector().centerOf(4, 2, 4));
		scene.idle(70);
		scene.world().setBlock(util.grid().at(4, 3, 4), SUPERCHARGED_COIL.getBlock().defaultBlockState(), false);
		scene.world().showSection(util.select().position(4, 3, 4), Direction.DOWN);
		scene.world().modifyBlock(util.grid().at(7, 5, 3), (state) -> {
			return StateHelper.with(state, "active", "true");
		}, false);
		scene.idle(20);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().fromTo(7, 5, 5, 7, 5, 5),
				60
		);
		scene.showControls(60, util.vector().centerOf(6, 5, 5), Pointing.RIGHT)
				.builder()
				.rightClick()
				.withItem(MekanismItems.CONFIGURATOR.getItemStack())
				.whileSneaking();

		scene.addKeyframe();
		scene.text(60, "潜行右键将端口调整为输出模式");
		scene.idle(70);
		scene.text(60, "现在向输入端口使用加压管道通入钋，制造反物质吧！=D", util.vector().centerOf(7, 3, 5));
		scene.idle(70);
	}
}