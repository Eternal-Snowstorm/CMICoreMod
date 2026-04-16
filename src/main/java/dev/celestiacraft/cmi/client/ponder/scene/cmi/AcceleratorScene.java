package dev.celestiacraft.cmi.client.ponder.scene.cmi;

import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class AcceleratorScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);

		scene.title("accelerator", "如何使用催生器");
		scene.showBasePlate();
		scene.idle(20);

		scene.world().setBlock(util.grid().at(3, 1, 3), CmiBlock.ACCELERATOR.getDefaultState(), false);
		scene.world().showSection(
				util.select().position(3, 1, 3),
				Direction.DOWN
		);
		scene.idle(10);

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				NebulaSceneBuilder.OBJECT,
				util.select().position(3, 1, 3),
				35
		);

		scene.overlay().showText(30)
				.text("这是一个构件催生器")
				.pointAt(util.vector().of(3.5, 2, 3.5))
				.placeNearTarget();
		scene.idle(40);
		scene.world().hideSection(
				util.select().position(3, 1, 3),
				Direction.UP
		);
		scene.idle(40);

		scene.world().showSection(util.select().fromTo(1, 1, 1, 5, 1, 5), Direction.DOWN);
		scene.idle(30);

		scene.overlay().showText(30)
				.text("右键对它使用一个磁力构件...")
				.pointAt(util.vector().of(3.5, 2, 3.5))
				.placeNearTarget();
		scene.idle(20);

		scene.overlay().showControls(
						util.vector().of(3.5, 2, 3.5),
						Pointing.DOWN,
						40
				).rightClick()
				.withItem(CmiMechanism.IRON.get().getDefaultInstance());

		scene.idle(40);

		scene.world().setBlock(util.grid().at(3, 1, 4), Blocks.IRON_ORE.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(4, 1, 3), Blocks.IRON_ORE.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(3, 1, 5), Blocks.REDSTONE_ORE.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(3, 1, 2), Blocks.IRON_ORE.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(2, 1, 5), Blocks.IRON_ORE.defaultBlockState(), false);
		scene.world().setBlock(util.grid().at(1, 1, 1), Blocks.IRON_ORE.defaultBlockState(), false);

		scene.overlay().showText(30)
				.text("...随后它周围的石头便会变成矿石!")
				.pointAt(util.vector().of(3.5, 2, 3.5))
				.placeNearTarget();

		scene.idle(40);

		scene.overlay().showText(30)
				.text("催生器也支持其它构件")
				.placeNearTarget();

		Vec3 motion = util.vector().of(0, -0.08, 0);

		scene.world().createItemEntity(
				util.vector().centerOf(2, 2, 2),
				motion,
				CmiMechanism.STONE.get().getDefaultInstance()
		);
		scene.world().createItemEntity(
				util.vector().centerOf(3, 2, 2),
				motion,
				CmiMechanism.COPPER.get().getDefaultInstance()
		);
		scene.world().createItemEntity(
				util.vector().centerOf(4, 2, 2),
				motion,
				CmiMechanism.ANDESITE.get().getDefaultInstance()
		);
		scene.world().createItemEntity(
				util.vector().centerOf(5, 2, 2),
				motion,
				CmiMechanism.GOLD.get().getDefaultInstance()
		);

		scene.idle(40);

		scene.overlay().showOutline(
				PonderPalette.RED,
				NebulaSceneBuilder.OBJECT,
				util.select().fromTo(1, 1, 1, 5, 1, 5),
				45
		);

		scene.overlay().showText(45)
				.text("只有周围5x5范围内的方块才能被转换")
				.pointAt(util.vector().of(3.5, 2, 3.5))
				.placeNearTarget();

		scene.markAsFinished();
	}
}