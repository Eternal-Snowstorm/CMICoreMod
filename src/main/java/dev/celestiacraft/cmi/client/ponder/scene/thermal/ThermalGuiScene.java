package dev.celestiacraft.cmi.client.ponder.scene.thermal;

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
 * 迁移自 KubeJS 的 {@code client_scripts/ponder/thermal/GUI.js}。
 * <p>
 * 场景里的界面贴图方块来自 {@link dev.celestiacraft.cmi.common.register.block.IoDebugBlocks}，
 * 机器与容器来自 {@code thermal} / {@code create} / 原版。
 */
public class ThermalGuiScene {
	private static BlockState block(String namespace, String name) {
		return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(namespace, name)).defaultBlockState();
	}

	private static BlockState cmi(String name) {
		return block("cmi", name);
	}

	public static void basicMachineGuidance(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);

		scene.title("basic_machine_guidance", "Machine UI");
		scene.showBasePlate();
		scene.idle(20);
		scene.scaleSceneView(0.6f);

		scene.world().setBlock(util.grid().at(7, 1, 7), block("thermal", "machine_furnace"), false);
		scene.world().showSection(util.select().position(7, 1, 7), Direction.DOWN);
		scene.text(40, "This is an ordinary thermal machine", util.vector().of(7.5, 1.5, 7));

		scene.idle(50);
		scene.addKeyframe();
		scene.text(30, "When we R-Click it, an interface ui up", util.vector().of(7.5, 1.5, 7));
		scene.rotateCameraY(0);
		scene.idle(50);
		scene.showControls(30, util.vector().of(7, 1, 7), Pointing.LEFT)
				.builder()
				.rightClick();

		scene.world().setBlocks(util.select().fromTo(13, 1, 13, 7, 4, 13), cmi("ponder_thermal_background"), false);
		scene.world().showSection(util.select().fromTo(13, 1, 13, 7, 4, 13), Direction.DOWN);
		scene.world().setBlock(util.grid().at(12, 1, 13), cmi("ponder_thermal_energy_slot"), false);
		scene.world().setBlock(util.grid().at(10, 2, 13), cmi("ponder_thermal_empty_slot"), false);
		scene.world().setBlock(util.grid().at(8, 2, 13), cmi("ponder_thermal_empty_slot"), false);
		scene.world().setBlock(util.grid().at(10, 1, 13), cmi("ponder_thermal_flame_empty"), false);

		scene.world().setBlock(util.grid().at(12, 2, 13), cmi("ponder_thermal_energy_full"), false);
		scene.world().setBlock(util.grid().at(12, 3, 13), cmi("ponder_thermal_energy_full"), false);

		scene.world().setBlock(util.grid().at(9, 2, 13), cmi("ponder_thermal_arrow_empty"), false);

		scene.world().setBlock(util.grid().at(6, 4, 13), cmi("ponder_thermal_augment_button"), false);
		scene.world().setBlock(util.grid().at(6, 3, 13), cmi("ponder_thermal_redstone_button"), false);
		scene.world().setBlock(util.grid().at(6, 2, 13), cmi("ponder_thermal_config_button"), false);
		scene.world().showSection(util.select().fromTo(6, 1, 13, 6, 4, 13), Direction.EAST);

		scene.world().setBlock(util.grid().at(14, 4, 13), cmi("ponder_thermal_information_button"), false);
		scene.world().setBlock(util.grid().at(14, 3, 13), cmi("ponder_thermal_energy_button"), false);
		scene.world().showSection(util.select().fromTo(14, 1, 13, 13, 4, 13), Direction.WEST);

		scene.addKeyframe();

		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.RED,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().fromTo(12, 2, 13, 12, 3, 13),
				30
		);
		scene.text(30, "Here is an energy tank for storing redstone flux RF (electrical energy)", util.vector().of(12, 2.5, 13));
		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.RED,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(12, 1, 13),
				30
		);
		scene.text(30, "Put the battery in here can also supply power", util.vector().of(12, 1, 13));
		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.BLACK,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(14, 4, 13),
				30
		);
		scene.text(30, "View machine introduction here", util.vector().of(14, 4, 13));
		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(14, 3, 13),
				30
		);
		scene.text(30, "View machine power here", util.vector().of(14, 3, 13));
		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.GREEN,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(6, 4, 13),
				30
		);
		scene.text(30, "Install upgrade here", util.vector().of(6, 4, 13));
		scene.addKeyframe();

		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(10, 2, 13),
				30
		);
		scene.text(30, "Put items here", util.vector().of(10, 2, 13));
		scene.idle(40);
		scene.world().setBlock(util.grid().at(10, 2, 13), cmi("ponder_thermal_sand_slot"), false);
		scene.world().setBlock(util.grid().at(10, 1, 13), cmi("ponder_thermal_flame_full"), false);
		scene.idle(40);

		scene.overlay().showOutline(
				PonderPalette.WHITE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(9, 2, 13),
				30
		);
		scene.text(30, "When the progress bar is completed, the input items will be consumed and then output", util.vector().of(9, 2, 13));
		scene.idle(40);
		scene.world().setBlock(util.grid().at(9, 2, 13), cmi("ponder_thermal_arrow_full"), false);

		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(8, 2, 13),
				30
		);
		scene.world().setBlock(util.grid().at(8, 2, 13), cmi("ponder_thermal_glass_slot"), false);
		scene.world().setBlock(util.grid().at(10, 2, 13), cmi("ponder_thermal_empty_slot"), false);
		scene.world().setBlock(util.grid().at(10, 1, 13), cmi("ponder_thermal_flame_empty"), false);
		scene.world().setBlock(util.grid().at(12, 3, 13), cmi("ponder_thermal_energy_empty"), false);
		scene.world().setBlock(util.grid().at(9, 2, 13), cmi("ponder_thermal_arrow_empty"), false);
		scene.addKeyframe();

		scene.idle(40);
		scene.overlay().showOutline(
				PonderPalette.GREEN,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(9, 2, 13),
				40
		);
		scene.text(40, "Click here to view all recipes", util.vector().of(9, 2, 13));
		scene.idle(50);
	}

	public static void machineAutomation(SceneBuilder builder, SceneBuildingUtil util) {
		CreateNebulaSceneBuilder scene = new CreateNebulaSceneBuilder(builder);

		scene.title("machine_automation", "Machine Automation");
		scene.showBasePlate();
		scene.idle(20);
		scene.scaleSceneView(0.5f);

		scene.world().setBlock(util.grid().at(7, 1, 7), block("thermal", "machine_furnace"), false);
		scene.world().showSection(util.select().position(7, 1, 7), Direction.DOWN);
		scene.text(40, "This is a thermal machine with a trough of items only.", util.vector().of(7.5, 1.5, 7));

		scene.idle(40);
		scene.world().setBlocks(util.select().fromTo(13, 4, 13, 7, 7, 13), cmi("ponder_thermal_background"), false);
		scene.world().showSection(util.select().fromTo(13, 4, 13, 7, 7, 13), Direction.DOWN);
		scene.world().setBlock(util.grid().at(12, 4, 13), cmi("ponder_thermal_energy_slot"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_empty_slot"), false);
		scene.world().setBlock(util.grid().at(10, 4, 13), cmi("ponder_thermal_flame_empty"), false);
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_empty_slot"), false);

		scene.world().setBlock(util.grid().at(12, 5, 13), cmi("ponder_thermal_energy_empty"), false);
		scene.world().setBlock(util.grid().at(12, 6, 13), cmi("ponder_thermal_energy_empty"), false);

		scene.world().setBlock(util.grid().at(9, 5, 13), cmi("ponder_thermal_arrow_empty"), false);

		scene.world().setBlock(util.grid().at(6, 7, 13), cmi("ponder_thermal_augment_button"), false);
		scene.world().setBlock(util.grid().at(6, 6, 13), cmi("ponder_thermal_redstone_button"), false);
		scene.world().setBlock(util.grid().at(6, 5, 13), cmi("ponder_thermal_config_button"), false);
		scene.world().showSection(util.select().fromTo(6, 4, 13, 6, 7, 13), Direction.EAST);

		scene.world().setBlock(util.grid().at(14, 7, 13), cmi("ponder_thermal_information_button"), false);
		scene.world().setBlock(util.grid().at(14, 6, 13), cmi("ponder_thermal_energy_button"), false);
		scene.world().showSection(util.select().fromTo(14, 4, 13, 13, 7, 13), Direction.WEST);
		scene.addKeyframe();
		scene.idle(40);

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().position(6, 5, 13),
				30
		);
		scene.text(30, "Click here to open the configuration page", util.vector().of(6, 5, 13));
		scene.showControls(30, util.vector().of(6, 5, 13), Pointing.RIGHT)
				.builder()
				.leftClick();
		scene.addKeyframe();
		scene.idle(40);
		scene.world().hideSection(util.select().position(6, 5, 13), Direction.WEST);
		scene.idle(20);
		scene.world().setBlocks(util.select().fromTo(6, 5, 13, 1, 2, 13), cmi("ponder_thermal_config_panel"), false);
		scene.world().showSection(util.select().fromTo(6, 5, 13, 1, 2, 13), Direction.UP);
		scene.world().setBlock(util.grid().at(6, 5, 13), cmi("ponder_thermal_config_button"), false);
		scene.world().setBlock(util.grid().at(6, 4, 13), cmi("ponder_thermal_auto_input_button_off"), false);
		scene.world().setBlock(util.grid().at(6, 3, 13), cmi("ponder_thermal_auto_output_button_off"), false);
		scene.world().setBlock(util.grid().at(3, 4, 13), cmi("ponder_thermal_machine_top"), false);
		scene.world().setBlock(util.grid().at(3, 2, 13), cmi("ponder_thermal_machine_bottom"), false);
		scene.world().setBlock(util.grid().at(4, 3, 13), cmi("ponder_thermal_machine_side"), false);
		scene.world().setBlock(util.grid().at(2, 3, 13), cmi("ponder_thermal_machine_side"), false);
		scene.world().setBlock(util.grid().at(2, 2, 13), cmi("ponder_thermal_machine_side"), false);
		scene.world().setBlock(util.grid().at(3, 3, 13), cmi("ponder_thermal_machine_front"), false);

		scene.addKeyframe();

		scene.idle(50);
		scene.text(40, "Power the machine from any side");
		scene.world().showSection(util.select().position(8, 1, 7), Direction.UP);
		scene.world().showSection(util.select().position(8, 2, 7), Direction.UP);
		scene.world().showSection(util.select().position(7, 2, 7), Direction.UP);
		scene.world().showSection(util.select().position(6, 2, 7), Direction.UP);
		scene.world().showSection(util.select().position(6, 1, 7), Direction.UP);
		scene.idle(40);
		scene.world().setBlock(util.grid().at(12, 5, 13), cmi("ponder_thermal_energy_full"), false);
		scene.idle(20);
		scene.world().setBlock(util.grid().at(12, 6, 13), cmi("ponder_thermal_energy_full"), false);
		scene.idle(20);
		scene.world().hideSection(util.select().position(8, 1, 7), Direction.UP);
		scene.world().hideSection(util.select().position(8, 2, 7), Direction.UP);
		scene.world().hideSection(util.select().position(7, 2, 7), Direction.UP);
		scene.world().hideSection(util.select().position(6, 2, 7), Direction.UP);
		scene.world().hideSection(util.select().position(6, 1, 7), Direction.UP);
		scene.idle(40);
		scene.addKeyframe();

		scene.overlay().showOutline(
				PonderPalette.BLUE,
				CreateNebulaSceneBuilder.OBJECT,
				util.select().fromTo(6, 4, 13, 6, 3, 13),
				40
		);
		scene.text(40, "Turn on automatic input and output here.", util.vector().of(6, 3.5, 13));
		scene.idle(40);
		scene.world().setBlock(util.grid().at(6, 4, 13), cmi("ponder_thermal_auto_input_button_on"), false);
		scene.world().setBlock(util.grid().at(6, 3, 13), cmi("ponder_thermal_auto_output_button_on"), false);

		scene.idle(40);
		scene.text(40, "and set the input and output of each face");
		scene.idle(50);

		scene.world().setBlock(util.grid().at(4, 3, 13), cmi("ponder_thermal_machine_side_input"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_input_slot"), false);
		scene.text(40, "Normally, blue represents input", util.vector().of(4, 3, 13));
		scene.idle(50);

		scene.world().setBlock(util.grid().at(4, 3, 13), cmi("ponder_thermal_machine_side_output"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_empty_slot"), false);
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_output_slot"), false);
		scene.text(40, "Orange represents output", util.vector().of(4, 3, 13));
		scene.idle(50);

		scene.world().setBlock(util.grid().at(4, 3, 13), cmi("ponder_thermal_machine_side_io"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_input_slot"), false);
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_output_slot"), false);
		scene.text(40, "The hybrid represents both input and output", util.vector().of(4, 3, 13));
		scene.idle(50);

		scene.world().setBlock(util.grid().at(4, 3, 13), cmi("ponder_thermal_machine_side_none"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_empty_slot"), false);
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_empty_slot"), false);
		scene.text(40, "Black represents disabled", util.vector().of(4, 3, 13));
		scene.idle(50);

		scene.text(60, "It is worth mentioning that this setting is applicable in most Mods");

		scene.idle(70);
		scene.addKeyframe();
		scene.world().setBlock(util.grid().at(4, 3, 13), cmi("ponder_thermal_machine_side_input"), false);
		scene.world().setBlock(util.grid().at(2, 3, 13), cmi("ponder_thermal_machine_side_output"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_input_slot"), false);
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_output_slot"), false);
		scene.world().setBlock(util.grid().at(8, 1, 7), block("minecraft", "chest"), false);
		scene.world().setBlock(util.grid().at(6, 1, 7), block("create", "depot"), false);
		scene.world().showSection(util.select().position(8, 1, 7), Direction.DOWN);
		scene.world().showSection(util.select().position(6, 1, 7), Direction.DOWN);

		scene.idle(40);
		scene.text(40, "Put items in the chest", util.vector().of(8, 1, 7));
		scene.idle(60);

		scene.text(40, "The machine will automatically extract items", util.vector().of(10, 5, 13));
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_sand_slot"), false);
		scene.world().setBlock(util.grid().at(10, 4, 13), cmi("ponder_thermal_flame_full"), false);

		scene.idle(40);
		scene.world().setBlock(util.grid().at(9, 5, 13), cmi("ponder_thermal_arrow_full"), false);
		scene.idle(40);
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_glass_slot"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_input_slot"), false);
		scene.world().setBlock(util.grid().at(9, 5, 13), cmi("ponder_thermal_arrow_empty"), false);
		scene.world().setBlock(util.grid().at(12, 6, 13), cmi("ponder_thermal_energy_empty"), false);
		scene.idle(40);

		scene.text(40, "Automatically output after completion", util.vector().of(8, 5, 13));
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_output_slot"), false);
		scene.world().setBlock(util.grid().at(10, 4, 13), cmi("ponder_thermal_flame_empty"), false);
		scene.showControls(40, util.vector().of(6.5, 2, 7.5), Pointing.DOWN)
				.builder()
				.withItem(new ItemStack(Items.GLASS));
		scene.idle(70);

		scene.addKeyframe();
		scene.text(40, "Fluid is the same");
		scene.world().setBlock(util.grid().at(8, 1, 7), block("create", "fluid_tank"), false);
		scene.world().setBlock(util.grid().at(6, 1, 7), block("create", "fluid_tank"), false);
		scene.world().setBlock(util.grid().at(10, 4, 13), cmi("ponder_thermal_background"), false);
		scene.world().setBlock(util.grid().at(10, 5, 13), cmi("ponder_thermal_fluid_lava"), false);
		scene.world().setBlock(util.grid().at(10, 6, 13), cmi("ponder_thermal_fluid_empty"), false);
		scene.world().setBlock(util.grid().at(8, 5, 13), cmi("ponder_thermal_fluid_water"), false);
		scene.world().setBlock(util.grid().at(8, 6, 13), cmi("ponder_thermal_fluid_empty"), false);

		scene.addKeyframe();
		scene.idle(50);

		scene.text(40, "Press Shift to click on the front to reset the settings");
		scene.idle(50);

		scene.text(40, "The front face of the thermal machine cannot interact");
		scene.idle(50);

		scene.text(40, "Use the redprint to copy the configuration to expand the grand plan for your machinery.");
		scene.idle(50);
	}
}