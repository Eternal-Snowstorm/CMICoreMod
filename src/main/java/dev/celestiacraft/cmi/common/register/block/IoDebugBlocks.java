package dev.celestiacraft.cmi.common.register.block;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiCreativeTabs;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 从 KubeJS 的 MachineIOBlock.js 迁移而来的调试用 IO 方块。
 * <p>
 * 原脚本逻辑:
 * <pre>
 * event.create("cmi:{IOBlock}")
 *     .textureAll("cmi:block/io_debug/{IOBlock}")
 *     .soundType(SoundType.METAL)
 *     .tag("cmi:io_debug_block")
 * </pre>
 * 每个方块都是六面同贴图的 cube_all 模型，贴图位于
 * assets/cmi/textures/block/io_debug/&lt;name&gt;.png，
 * 统一归入 cmi:io_debug_block 方块标签，并收录到
 * {@link CmiCreativeTabs#PONDER_IO_BLOCK} 标签页。
 */
public class IoDebugBlocks {
	/** 迁移自 MachineIOBlock.js 的方块名列表，顺序与原脚本一致。 */
	public static final List<String> NAMES = List.of(
		"gui_block",
		"ponder_thermal_input",
		"ponder_thermal_output",
		"ponder_thermal_output_on",
		"ponder_thermal_input_on",
		"ponder_thermal_fluid",
		"ponder_thermal_fluid_blue",
		"ponder_thermal_fluid_orange",
		"ponder_thermal_slot",
		"ponder_thermal_slot_blue",
		"ponder_thermal_slot_orange",
		"ponder_thermal_slot_energy",
		"ponder_thermal_energy",
		"ponder_thermal_energy_empty",
		"ponder_thermal_gui",
		"ponder_thermal_gui_blue",
		"ponder_thermal_gui_blue_icon",
		"ponder_thermal_gui_green",
		"ponder_thermal_gui_green_icon",
		"ponder_thermal_gui_red",
		"ponder_thermal_gui_red_icon",
		"ponder_thermal_gui_gray",
		"ponder_thermal_gui_gray_icon",
		"ponder_thermal_gui_lightblue",
		"ponder_thermal_gui_lightblue_icon",
		"ponder_thermal_machine_front",
		"ponder_thermal_machine_side",
		"ponder_thermal_machine_side_blue",
		"ponder_thermal_machine_side_orange",
		"ponder_thermal_machine_side_mix",
		"ponder_thermal_machine_side_disabled",
		"ponder_thermal_machine_top",
		"ponder_thermal_machine_bottom",
		"ponder_thermal_rs1_off",
		"ponder_thermal_rs2_off",
		"ponder_thermal_rs3_off",
		"ponder_thermal_rs1_on",
		"ponder_thermal_rs2_on",
		"ponder_thermal_rs3_on",
		"ponder_thermal_lava",
		"ponder_thermal_water",
		"ponder_thermal_arrow",
		"ponder_thermal_arrow_active",
		"ponder_thermal_fire",
		"ponder_thermal_fire_active",
		"ponder_thermal_sand",
		"ponder_thermal_glass",
		"slot1",
		"arrow",
		"slot2",
		"patten_slot",
		"gui",
		"write",
		"gui2",
		"craft_on",
		"craft_off",
		"furnace_on",
		"furnace_off",
		"smith_on",
		"smith_off",
		"cut_on",
		"cut_off",
		"tab_on",
		"tab_off",
		"patten_blank",
		"patten_filled",
		"diamond",
		"stick",
		"pickaxe",
		"pickaxe2",
		"raw",
		"cooked",
		"cooked2",
		"coal",
		"iron",
		"sheet"
	);

	public static final BlockEntry<Block> GUI_BLOCK;
	public static final BlockEntry<Block> PONDER_THERMAL_INPUT;
	public static final BlockEntry<Block> PONDER_THERMAL_OUTPUT;
	public static final BlockEntry<Block> PONDER_THERMAL_OUTPUT_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_INPUT_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID_BLUE;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID_ORANGE;
	public static final BlockEntry<Block> PONDER_THERMAL_SLOT;
	public static final BlockEntry<Block> PONDER_THERMAL_SLOT_BLUE;
	public static final BlockEntry<Block> PONDER_THERMAL_SLOT_ORANGE;
	public static final BlockEntry<Block> PONDER_THERMAL_SLOT_ENERGY;
	public static final BlockEntry<Block> PONDER_THERMAL_ENERGY;
	public static final BlockEntry<Block> PONDER_THERMAL_ENERGY_EMPTY;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_BLUE;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_BLUE_ICON;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_GREEN;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_GREEN_ICON;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_RED;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_RED_ICON;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_GRAY;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_GRAY_ICON;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_LIGHTBLUE;
	public static final BlockEntry<Block> PONDER_THERMAL_GUI_LIGHTBLUE_ICON;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_FRONT;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_BLUE;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_ORANGE;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_MIX;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_DISABLED;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_TOP;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_BOTTOM;
	public static final BlockEntry<Block> PONDER_THERMAL_RS1_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_RS2_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_RS3_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_RS1_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_RS2_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_RS3_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_LAVA;
	public static final BlockEntry<Block> PONDER_THERMAL_WATER;
	public static final BlockEntry<Block> PONDER_THERMAL_ARROW;
	public static final BlockEntry<Block> PONDER_THERMAL_ARROW_ACTIVE;
	public static final BlockEntry<Block> PONDER_THERMAL_FIRE;
	public static final BlockEntry<Block> PONDER_THERMAL_FIRE_ACTIVE;
	public static final BlockEntry<Block> PONDER_THERMAL_SAND;
	public static final BlockEntry<Block> PONDER_THERMAL_GLASS;
	public static final BlockEntry<Block> SLOT1;
	public static final BlockEntry<Block> ARROW;
	public static final BlockEntry<Block> SLOT2;
	public static final BlockEntry<Block> PATTEN_SLOT;
	public static final BlockEntry<Block> GUI;
	public static final BlockEntry<Block> WRITE;
	public static final BlockEntry<Block> GUI2;
	public static final BlockEntry<Block> CRAFT_ON;
	public static final BlockEntry<Block> CRAFT_OFF;
	public static final BlockEntry<Block> FURNACE_ON;
	public static final BlockEntry<Block> FURNACE_OFF;
	public static final BlockEntry<Block> SMITH_ON;
	public static final BlockEntry<Block> SMITH_OFF;
	public static final BlockEntry<Block> CUT_ON;
	public static final BlockEntry<Block> CUT_OFF;
	public static final BlockEntry<Block> TAB_ON;
	public static final BlockEntry<Block> TAB_OFF;
	public static final BlockEntry<Block> PATTEN_BLANK;
	public static final BlockEntry<Block> PATTEN_FILLED;
	public static final BlockEntry<Block> DIAMOND;
	public static final BlockEntry<Block> STICK;
	public static final BlockEntry<Block> PICKAXE;
	public static final BlockEntry<Block> PICKAXE2;
	public static final BlockEntry<Block> RAW;
	public static final BlockEntry<Block> COOKED;
	public static final BlockEntry<Block> COOKED2;
	public static final BlockEntry<Block> COAL;
	public static final BlockEntry<Block> IRON;
	public static final BlockEntry<Block> SHEET;

	/** 名称 -> 方块，顺序与 NAMES 一致。 */
	private static final Map<String, BlockEntry<Block>> BY_NAME = new LinkedHashMap<>();

	static {
		GUI_BLOCK = register("gui_block");
		PONDER_THERMAL_INPUT = register("ponder_thermal_input");
		PONDER_THERMAL_OUTPUT = register("ponder_thermal_output");
		PONDER_THERMAL_OUTPUT_ON = register("ponder_thermal_output_on");
		PONDER_THERMAL_INPUT_ON = register("ponder_thermal_input_on");
		PONDER_THERMAL_FLUID = register("ponder_thermal_fluid");
		PONDER_THERMAL_FLUID_BLUE = register("ponder_thermal_fluid_blue");
		PONDER_THERMAL_FLUID_ORANGE = register("ponder_thermal_fluid_orange");
		PONDER_THERMAL_SLOT = register("ponder_thermal_slot");
		PONDER_THERMAL_SLOT_BLUE = register("ponder_thermal_slot_blue");
		PONDER_THERMAL_SLOT_ORANGE = register("ponder_thermal_slot_orange");
		PONDER_THERMAL_SLOT_ENERGY = register("ponder_thermal_slot_energy");
		PONDER_THERMAL_ENERGY = register("ponder_thermal_energy");
		PONDER_THERMAL_ENERGY_EMPTY = register("ponder_thermal_energy_empty");
		PONDER_THERMAL_GUI = register("ponder_thermal_gui");
		PONDER_THERMAL_GUI_BLUE = register("ponder_thermal_gui_blue");
		PONDER_THERMAL_GUI_BLUE_ICON = register("ponder_thermal_gui_blue_icon");
		PONDER_THERMAL_GUI_GREEN = register("ponder_thermal_gui_green");
		PONDER_THERMAL_GUI_GREEN_ICON = register("ponder_thermal_gui_green_icon");
		PONDER_THERMAL_GUI_RED = register("ponder_thermal_gui_red");
		PONDER_THERMAL_GUI_RED_ICON = register("ponder_thermal_gui_red_icon");
		PONDER_THERMAL_GUI_GRAY = register("ponder_thermal_gui_gray");
		PONDER_THERMAL_GUI_GRAY_ICON = register("ponder_thermal_gui_gray_icon");
		PONDER_THERMAL_GUI_LIGHTBLUE = register("ponder_thermal_gui_lightblue");
		PONDER_THERMAL_GUI_LIGHTBLUE_ICON = register("ponder_thermal_gui_lightblue_icon");
		PONDER_THERMAL_MACHINE_FRONT = register("ponder_thermal_machine_front");
		PONDER_THERMAL_MACHINE_SIDE = register("ponder_thermal_machine_side");
		PONDER_THERMAL_MACHINE_SIDE_BLUE = register("ponder_thermal_machine_side_blue");
		PONDER_THERMAL_MACHINE_SIDE_ORANGE = register("ponder_thermal_machine_side_orange");
		PONDER_THERMAL_MACHINE_SIDE_MIX = register("ponder_thermal_machine_side_mix");
		PONDER_THERMAL_MACHINE_SIDE_DISABLED = register("ponder_thermal_machine_side_disabled");
		PONDER_THERMAL_MACHINE_TOP = register("ponder_thermal_machine_top");
		PONDER_THERMAL_MACHINE_BOTTOM = register("ponder_thermal_machine_bottom");
		PONDER_THERMAL_RS1_OFF = register("ponder_thermal_rs1_off");
		PONDER_THERMAL_RS2_OFF = register("ponder_thermal_rs2_off");
		PONDER_THERMAL_RS3_OFF = register("ponder_thermal_rs3_off");
		PONDER_THERMAL_RS1_ON = register("ponder_thermal_rs1_on");
		PONDER_THERMAL_RS2_ON = register("ponder_thermal_rs2_on");
		PONDER_THERMAL_RS3_ON = register("ponder_thermal_rs3_on");
		PONDER_THERMAL_LAVA = register("ponder_thermal_lava");
		PONDER_THERMAL_WATER = register("ponder_thermal_water");
		PONDER_THERMAL_ARROW = register("ponder_thermal_arrow");
		PONDER_THERMAL_ARROW_ACTIVE = register("ponder_thermal_arrow_active");
		PONDER_THERMAL_FIRE = register("ponder_thermal_fire");
		PONDER_THERMAL_FIRE_ACTIVE = register("ponder_thermal_fire_active");
		PONDER_THERMAL_SAND = register("ponder_thermal_sand");
		PONDER_THERMAL_GLASS = register("ponder_thermal_glass");
		SLOT1 = register("slot1");
		ARROW = register("arrow");
		SLOT2 = register("slot2");
		PATTEN_SLOT = register("patten_slot");
		GUI = register("gui");
		WRITE = register("write");
		GUI2 = register("gui2");
		CRAFT_ON = register("craft_on");
		CRAFT_OFF = register("craft_off");
		FURNACE_ON = register("furnace_on");
		FURNACE_OFF = register("furnace_off");
		SMITH_ON = register("smith_on");
		SMITH_OFF = register("smith_off");
		CUT_ON = register("cut_on");
		CUT_OFF = register("cut_off");
		TAB_ON = register("tab_on");
		TAB_OFF = register("tab_off");
		PATTEN_BLANK = register("patten_blank");
		PATTEN_FILLED = register("patten_filled");
		DIAMOND = register("diamond");
		STICK = register("stick");
		PICKAXE = register("pickaxe");
		PICKAXE2 = register("pickaxe2");
		RAW = register("raw");
		COOKED = register("cooked");
		COOKED2 = register("cooked2");
		COAL = register("coal");
		IRON = register("iron");
		SHEET = register("sheet");
	}

	private static BlockEntry<Block> register(String name) {
		BlockEntry<Block> entry = Cmi.REGISTRATE.block(name, Block::new)
				.initialProperties(SharedProperties::stone)
				.properties((properties) -> properties.sound(SoundType.METAL))
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				/*
				 * 贴图在 src/main/resources/assets/cmi/textures/block/io_debug/<name>.png，
				 * blockstate / 方块模型 / 物品模型全部由 datagen 生成（与原来 KubeJS 的
				 * textureAll 行为一致：六面同贴图的 cube_all）。
				 *
				 * 不能沿用 Registrate 的默认 simpleBlock/cubeAll：默认实现会去找
				 * cmi:block/<name> 这张不存在的贴图，导致 runData 抛
				 * "Texture cmi:block/<name> does not exist in any known resource pack"。
				 */
				.blockstate((context, provider) -> {
					String blockName = context.getName();
					provider.simpleBlock(
							context.get(),
							provider.models().cubeAll(blockName, provider.modLoc("block/io_debug/" + blockName))
					);
				})
				.simpleItem()
				.register();
		BY_NAME.put(name, entry);
		return entry;
	}

	/** 按原脚本顺序返回全部方块。 */
	public static Stream<BlockEntry<Block>> all() {
		return NAMES.stream().map(BY_NAME::get);
	}

	/** PONDER_IO_BLOCK 物品栏图标。 */
	public static ItemStack icon() {
		return PONDER_THERMAL_GUI.asStack();
	}

	public static void register() {
		Cmi.LOGGER.info("{} IO Debug Blocks Registered!", Cmi.NAME);
	}
}