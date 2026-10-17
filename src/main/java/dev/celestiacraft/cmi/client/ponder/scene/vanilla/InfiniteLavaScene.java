package dev.celestiacraft.cmi.client.ponder.scene.cmi;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class InfiniteLavaScene {
	public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);

		scene.title("infinite_lava", "无限熔岩");
		INebulaSceneBuilder.init5x5(scene, util);
		scene.idle(20);

		BlockPos corner = util.grid().at(2, 1, 2);
		BlockPos opposite = util.grid().at(3, 1, 3);
		Selection pool = util.select().fromTo(2, 1, 2, 3, 1, 3);

		scene.world().setBlocks(pool, Blocks.LAVA.defaultBlockState(), false);
		scene.world().showSection(pool, Direction.DOWN);
		scene.idle(25);

		scene.overlay().showOutline(PonderPalette.BLUE, CreateNebulaSceneBuilder.OBJECT, pool, 70);
		scene.overlay().showText(70)
				.text("将四桶熔岩摆成 2×2 的池子")
				.pointAt(util.vector().of(3, 1.5, 3))
				.placeNearTarget();
		scene.idle(80);

		scene.addKeyframe();

		scene.overlay().showText(45)
				.text("像无限水一样，可以用桶从池中取走熔岩")
				.pointAt(util.vector().of(2.5, 2, 2.5))
				.placeNearTarget();
		scene.idle(15);

		scene.overlay().showControls(util.vector().topOf(2, 1, 2), Pointing.DOWN, 30)
				.rightClick()
				.withItem(new ItemStack(Items.BUCKET));
		scene.idle(15);

		scene.world().destroyBlock(corner);
		ElementLink<EntityElement> lavaBucket = scene.world().createItemEntity(
				util.vector().centerOf(2, 2, 2),
				util.vector().of(0, 0.1, 0),
				new ItemStack(Items.LAVA_BUCKET)
		);
		scene.idle(20);
		scene.world().removeEntity(lavaBucket);
		scene.idle(10);

		scene.overlay().showText(45)
				.text("取走的位置会立刻重新生成熔岩")
				.pointAt(util.vector().of(2.5, 2, 2.5))
				.placeNearTarget();
		scene.idle(10);

		scene.world().setBlock(corner, Blocks.LAVA.defaultBlockState(), true);
		scene.idle(55);

		scene.addKeyframe();

		scene.overlay().showText(45)
				.text("从任意一格取走，池子都会补满")
				.pointAt(util.vector().of(3.5, 2, 3.5))
				.placeNearTarget();
		scene.idle(15);

		scene.overlay().showControls(util.vector().topOf(3, 1, 3), Pointing.DOWN, 30)
				.rightClick()
				.withItem(new ItemStack(Items.BUCKET));
		scene.idle(15);

		scene.world().destroyBlock(opposite);
		scene.idle(25);
		scene.world().setBlock(opposite, Blocks.LAVA.defaultBlockState(), true);
		scene.idle(20);

		scene.overlay().showText(55)
				.text("只要保持 2×2，就能无限取用熔岩")
				.pointAt(util.vector().of(3, 1.5, 3))
				.placeNearTarget();
		scene.idle(70);

		scene.markAsFinished();
	}
}
