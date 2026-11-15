package dev.celestiacraft.cmi.client.ponder.scene.ae2;

import dev.celestiacraft.libs.client.ponder.CreateNebulaSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 迁移自 KubeJS 的 {@code client_scripts/ponder/ae2/GUI.js}。
 * <p>
 * 场景使用的贴图方块来自 {@link dev.celestiacraft.cmi.common.register.block.IoDebugBlocks}，
 * 其余为原版 / Create / Mekanism 方块。
 */
public class Ae2GuiScene {
	private static final BlockState PATTEN_SLOT = state("patten_slot");
	private static final BlockState PATTEN_BLANK = state("patten_blank");
	private static final BlockState PICKAXE = state("pickaxe");
	private static final BlockState COOKED = state("cooked");
	private static final BlockState DIAMOND = state("diamond");
	private static final BlockState STICK = state("stick");
	private static final BlockState SLOT1 = state("slot1");
	private static final BlockState SLOT2 = state("slot2");
	private static final BlockState COAL = state("coal");

	private static BlockState state(String name) {
		return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("cmi", name)).defaultBlockState();
	}

	private static BlockState vanilla(String name) {
		return BuiltInRegistries.BLOCK.get(ResourceLocation.withDefaultNamespace(name)).defaultBlockState();
	}

	private static BlockState of(String namespace, String name) {
		return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(namespace, name)).defaultBlockState();
	}

	public static void scene1(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);

		scene.title("ae2_gui", "样板配置");
		scene.showStructure(0);
		scene.idle(20);
		scene.scaleSceneView(0.5f);

		// 样板编码终端
		scene.world().showSection(util.select().fromTo(10, 2, 6, 5, 1, 6), Direction.UP);
		scene.idle(20);

		scene.world().setBlock(util.grid().at(4, 4, 10), PATTEN_SLOT, false);
		scene.world().setBlock(util.grid().at(10, 4, 8), PICKAXE, false);
		scene.world().setBlock(util.grid().at(10, 2, 8), COOKED, false);
		scene.addKeyframe();

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(8, 2, 6),
				40
		);
		scene.text(40, "这是一个样板编码终端", util.vector().of(8, 2.5, 6));
		scene.idle(60);

		scene.text(40, "右键打开界面", util.vector().of(8, 2.5, 6));
		scene.idle(60);
		scene.world().showSection(util.select().fromTo(14, 1, 10, 0, 7, 10), Direction.UP);
		scene.idle(60);

		scene.addKeyframe();

		scene.text(40, "在此处放入空白样板", util.vector().of(4, 4.5, 10));
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(4, 4, 10),
				40
		);
		scene.world().setBlock(util.grid().at(4, 4, 10), PATTEN_BLANK, false);
		scene.idle(60);
		scene.addKeyframe();

		scene.text(40, "假如你要自动化合成钻石镐");
		scene.idle(60);

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(2, 4, 10),
				40
		);
		scene.text(60, "选择此处“合成样板”", util.vector().of(2, 4.5, 10));
		scene.idle(80);

		scene.text(60, "摆出物品的合成", util.vector().of(6, 3.5, 10));
		scene.world().setBlock(util.grid().at(11, 4, 10), DIAMOND, false);
		scene.idle(5);
		scene.world().setBlock(util.grid().at(10, 4, 10), DIAMOND, false);
		scene.idle(5);
		scene.world().setBlock(util.grid().at(9, 4, 10), DIAMOND, false);
		scene.idle(5);
		scene.world().setBlock(util.grid().at(10, 3, 10), STICK, false);
		scene.idle(5);
		scene.world().setBlock(util.grid().at(10, 2, 10), STICK, false);
		scene.idle(10);
		scene.world().setBlock(util.grid().at(6, 3, 10), PICKAXE, false);
		scene.idle(40);

		scene.addKeyframe();

		scene.text(40, "点击编写", util.vector().of(4, 3.5, 10));
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(4, 3, 10),
				40
		);
		scene.idle(30);
		scene.world().setBlock(util.grid().at(4, 4, 10), PATTEN_SLOT, false);
		scene.world().setBlock(util.grid().at(4, 2, 10), PICKAXE, false);

		scene.addKeyframe();
		scene.idle(30);
		scene.showControls(40, util.vector().of(4, 2, 10), Pointing.LEFT)
				.builder()
				.withItem(new ItemStack(Items.PAPER));

		scene.idle(60);
		scene.text(60, "我们将其取走, 放入紧贴“分子装配室”的“ME样板供应器”", util.vector().of(6, 1.5, 6));

		scene.world().setBlock(util.grid().at(4, 2, 10), SLOT2, false);
		scene.idle(10);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(6, 1, 6),
				40
		);

		scene.world().setBlock(util.grid().at(12, 6, 10), PICKAXE, false);
		scene.idle(40);

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(12, 6, 10),
				40
		);
		scene.text(40, "然后我们可以找到钻石镐的图标", util.vector().of(12, 6.5, 10));
		scene.idle(50);

		scene.text(100, "如果你的网络中没有钻石镐, 则会在右下角显示“合成”, 按“鼠标中键”或“鼠标左键”可以合成一些", util.vector().of(12, 6.5, 10));
		scene.idle(110);
		scene.text(100, "如果你的网络中已有钻石镐, 则会在右下角显示数字, 按“鼠标中键”可以再合成一些", util.vector().of(12, 6.5, 10));
		scene.idle(110);

		scene.addKeyframe();

		scene.world().showSection(util.select().fromTo(6, 1, 5, 5, 1, 5), Direction.UP);
		scene.text(40, "若你的网络中有很多复杂的ME样板供应器");
		scene.idle(60);

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(9, 2, 6),
				40
		);
		scene.world().showSection(util.select().fromTo(14, 1, 8, 0, 7, 8), Direction.UP);
		scene.text(60, "你可以使用“ME样板管理终端”来管理网络中的所有样板", util.vector().of(9, 2.5, 6));
		scene.idle(60);
	}

	public static void scene2(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);

		scene.title("ae2_gui2", "自动化配置案例——“燃料”类配方");
		scene.showStructure(0);
		scene.idle(20);
		scene.scaleSceneView(0.5f);

		scene.world().showSection(util.select().fromTo(14, 1, 6, 0, 7, 6), Direction.UP);
		scene.world().showSection(util.select().fromTo(14, 1, 10, 0, 7, 10), Direction.UP);
		scene.idle(20);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().fromTo(12, 4, 10, 4, 2, 10),
				60
		);
		scene.text(40, "这是一个配置好的用于熔炉将生鸡肉制作熟鸡肉的样板");
		scene.addKeyframe();

		scene.idle(50);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(11, 2, 6),
				40
		);
		scene.text(40, "这是一个熔炉", util.vector().of(11, 2.5, 6));
		scene.idle(50);
		scene.text(40, "熔炉工作需要燃料", util.vector().of(11, 2.5, 6));
		scene.idle(50);
		scene.text(40, "且有着相对严格的输入输出分配", util.vector().of(11, 2.5, 6));
		scene.idle(50);
		scene.text(60, "即只能侧面输入燃料, 顶部输入物品, 底部输出物品", util.vector().of(11, 2.5, 6));
		scene.idle(70);
		scene.addKeyframe();

		scene.text(60, "我们可以将其拆解成两部分：", util.vector().of(11, 2.5, 6));
		scene.idle(70);

		scene.text(60, "提供原料和提供燃料", util.vector().of(11, 2.5, 6));
		scene.idle(70);

		scene.overlay().showOutline(
				PonderPalette.OUTPUT,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(11, 4, 6),
				40
		);
		scene.text(60, "由样板供应器来提供原料(生鸡肉)", util.vector().of(11, 4, 6));
		scene.idle(70);
		scene.overlay().showOutline(
				PonderPalette.INPUT,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(10, 3, 6),
				40
		);
		scene.text(60, "由ME接口来提供燃料(煤炭)", util.vector().of(10, 3, 6));
		scene.idle(70);
		scene.addKeyframe();

		scene.text(30, "打开ME接口", util.vector().of(10, 3, 6));
		scene.idle(40);

		scene.world().showSection(util.select().fromTo(14, 1, 7, 0, 7, 7), Direction.UP);
		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().fromTo(9, 4, 7, 2, 5, 7),
				40
		);
		scene.text(40, "正常的ME接口有2排9列物品栏", util.vector().of(2, 5, 7));
		scene.idle(50);
		scene.text(50, "但是“俺寻思”里面并不能放不下这么多, 此处省略()", util.vector().of(2, 5, 7));
		scene.idle(60);

		scene.addKeyframe();
		scene.world().setBlock(util.grid().at(9, 5, 7), COAL, false);
		scene.text(50, "在此处标记 1 个煤炭", util.vector().of(9, 5.5, 7));
		scene.idle(60);

		scene.text(60, "ME接口会自动从网络抽取物品, 使其与设定的数量保持一致", util.vector().of(10, 3, 6));
		scene.idle(70);
		scene.text(50, "假如此时网络共有 8 组(64x8=512)个煤炭");
		scene.idle(60);

		scene.text(40, "ME接口将抽取 1 个煤炭", util.vector().of(10, 3, 6));
		scene.world().setBlock(util.grid().at(9, 4, 7), COAL, false);

		scene.idle(50);
		scene.text(60, "随后, 漏斗会将煤炭抽取走, ME接口内煤炭耗尽", util.vector().of(10, 3, 6));
		scene.world().setBlock(util.grid().at(9, 4, 7), SLOT1, false);
		scene.idle(70);
		scene.text(40, "ME接口将重新抽取 1 个煤炭", util.vector().of(10, 3, 6));
		scene.world().setBlock(util.grid().at(9, 4, 7), COAL, false);
		scene.idle(50);

		scene.text(60, "如此循环往复, 直到熔炉、漏斗、ME接口中都被填满, ME接口将停止抽取新的物品", util.vector().of(10, 3, 6));
		scene.idle(70);

		scene.text(80, "最后, 网络中剩余512-5x64-1x64-1=127个煤炭(填充熔炉一组, 填充漏斗5组, 填充ME接口1个)");
		scene.idle(90);

		scene.addKeyframe();

		scene.text(40, "此时, 在终端中合成一个熟鸡肉");
		scene.idle(50);

		scene.text(60, "样板供应器向网络请求了一个生鸡肉", util.vector().of(11, 4.5, 6));
		scene.idle(70);

		scene.text(60, "生鸡肉通过漏斗来到熔炉", util.vector().of(11, 3.5, 6));
		scene.idle(70);

		scene.text(60, "熔炉开始工作并消耗 1 个煤炭", util.vector().of(11, 2.5, 6));
		scene.idle(70);
		scene.world().setBlock(util.grid().at(11, 2, 6), vanilla("magma_block"), false);
		scene.text(40, "没错我是燃烧的熔炉(", util.vector().of(11, 2.5, 6));
		scene.idle(40);

		scene.world().setBlock(util.grid().at(9, 4, 7), SLOT1, false);
		scene.text(60, "煤炭被消耗, ME接口重新请求 1 个煤炭", util.vector().of(10, 3, 6));
		scene.idle(70);
		scene.world().setBlock(util.grid().at(9, 4, 7), COAL, false);
		scene.idle(20);
		scene.text(60, "此时网络剩余127-1=126个煤炭");
		scene.idle(70);

		scene.addKeyframe();
		scene.text(60, "当合成完成后", util.vector().of(11, 2.5, 6));
		scene.world().setBlock(util.grid().at(11, 2, 6), vanilla("furnace"), false);
		scene.idle(70);
		scene.text(60, "熟鸡肉通过漏斗回到下面的ME接口, 回到网络, 结束合成", util.vector().of(11, 1.5, 6));
		scene.idle(100);
		scene.addKeyframe();
		scene.text(100, "这种把“消耗品”与“原料”分开的思路仍可在其他地方使用, 比如...");
		scene.idle(110);

		scene.world().setBlock(util.grid().at(14, 1, 2), of("create", "depot"), false);
		scene.world().setBlock(util.grid().at(14, 3, 2), of("create", "spout"), false);

		scene.world().setBlock(util.grid().at(12, 1, 2), of("create", "blaze_burner"), false);

		scene.world().setBlock(util.grid().at(10, 1, 2), of("mekanism", "metallurgic_infuser"), false);

		scene.world().setBlock(util.grid().at(8, 1, 2), of("mekanism", "purification_chamber"), false);
		scene.world().setBlock(util.grid().at(7, 1, 2), of("mekanism", "chemical_injection_chamber"), false);
		scene.world().setBlock(util.grid().at(6, 1, 2), of("mekanism", "pressurized_reaction_chamber"), false);

		scene.world().setBlock(util.grid().at(4, 1, 2), of("cmi", "crucible_tuyere"), false);
		scene.world().setBlock(util.grid().at(3, 1, 2), of("cmi", "crucible_tuyere"), false);
		scene.world().setBlock(util.grid().at(2, 1, 2), of("cmi", "crucible_tuyere"), false);

		scene.world().showSection(util.select().fromTo(14, 1, 2, 14, 3, 2), Direction.DOWN);
		scene.text(40, "注液器(熔岩蛋糕自动化)", util.vector().of(14, 2.5, 2));
		scene.idle(50);

		scene.world().showSection(util.select().position(12, 1, 2), Direction.DOWN);
		scene.text(40, "烈焰人燃烧室(自动加热但是超级费燃料!)", util.vector().of(12, 1.5, 2));
		scene.idle(50);

		scene.world().showSection(util.select().position(10, 1, 2), Direction.DOWN);
		scene.text(40, "冶金灌注机(自动填充灌注类型)", util.vector().of(10, 1.5, 2));
		scene.idle(50);

		scene.world().showSection(util.select().fromTo(8, 1, 2, 6, 1, 2), Direction.DOWN);
		scene.text(40, "化学机器(自动填充气体或液体)", util.vector().of(7, 1.5, 2));
		scene.idle(50);

		scene.world().showSection(util.select().fromTo(4, 1, 2, 2, 1, 2), Direction.DOWN);
		scene.text(40, "等等...", util.vector().of(3, 1.5, 2));
		scene.idle(50);

		scene.text(40, "祝你自动化愉快!");
	}
}
