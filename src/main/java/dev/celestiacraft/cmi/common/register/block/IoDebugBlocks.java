package dev.celestiacraft.cmi.common.register.block;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiCreativeTabs;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

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
		"ponder_thermal_auto_input_button_off",
		"ponder_thermal_auto_output_button_off",
		"ponder_thermal_auto_output_button_on",
		"ponder_thermal_auto_input_button_on",
		"ponder_thermal_fluid_empty",
		"ponder_thermal_fluid_input",
		"ponder_thermal_fluid_output",
		"ponder_thermal_empty_slot",
		"ponder_thermal_input_slot",
		"ponder_thermal_output_slot",
		"ponder_thermal_energy_slot",
		"ponder_thermal_energy_full",
		"ponder_thermal_energy_empty",
		"ponder_thermal_background",
		"ponder_thermal_config_panel",
		"ponder_thermal_config_button",
		"ponder_thermal_augment_panel",
		"ponder_thermal_augment_button",
		"ponder_thermal_redstone_panel",
		"ponder_thermal_redstone_button",
		"ponder_thermal_information_panel",
		"ponder_thermal_information_button",
		"ponder_thermal_energy_panel",
		"ponder_thermal_energy_button",
		"ponder_thermal_machine_front",
		"ponder_thermal_machine_side",
		"ponder_thermal_machine_side_input",
		"ponder_thermal_machine_side_output",
		"ponder_thermal_machine_side_io",
		"ponder_thermal_machine_side_none",
		"ponder_thermal_machine_top",
		"ponder_thermal_machine_bottom",
		"ponder_thermal_redstone_button_off",
		"ponder_thermal_low_rs_touch_button_off",
		"ponder_thermal_high_rs_touch_button_off",
		"ponder_thermal_redstone_button_on",
		"ponder_thermal_low_rs_touch_button_on",
		"ponder_thermal_high_rs_touch_button_on",
		"ponder_thermal_fluid_lava",
		"ponder_thermal_fluid_water",
		"ponder_thermal_arrow_empty",
		"ponder_thermal_arrow_full",
		"ponder_thermal_flame_empty",
		"ponder_thermal_flame_full",
		"ponder_thermal_sand_slot",
		"ponder_thermal_glass_slot",
		"ponder_ae2_empty_slot_light",
		"ponder_ae2_arrow",
		"ponder_ae2_empty_slot_dark",
		"ponder_ae2_empty_pattern_slot_dark",
		"ponder_ae2_background_light",
		"ponder_ae2_write_button",
		"ponder_ae2_background_dark",
		"ponder_ae2_crafting_right_button_on",
		"ponder_ae2_crafting_right_button_off",
		"ponder_ae2_processing_right_button_on",
		"ponder_ae2_processing_right_button_off",
		"ponder_ae2_smithing_right_button_on",
		"ponder_ae2_smithing_right_button_off",
		"ponder_ae2_stonecutting_right_button_on",
		"ponder_ae2_stonecutting_right_button_off",
		"ponder_ae2_right_button_on",
		"ponder_ae2_right_button_off",
		"ponder_ae2_blank_pattern_slot_dark",
		"ponder_ae2_encoded_pattern_slot_dark",
		"ponder_ae2_diamond_slot_dark",
		"ponder_ae2_stick_slot_dark",
		"ponder_ae2_diamond_pickaxe_slot_dark",
		"ponder_ae2_diamond_pickaxe_slot_light",
		"ponder_ae2_chicken_slot_dark",
		"ponder_ae2_cooked_chicken_slot_dark",
		"ponder_ae2_cooked_chicken_slot_light",
		"ponder_ae2_coal_slot_light",
		"ponder_ae2_iron_ingot_slot_dark",
		"ponder_ae2_iron_plate_slot_dark"
	);

	public static final BlockEntry<Block> GUI_BLOCK;
	public static final BlockEntry<Block> PONDER_THERMAL_AUTO_INPUT_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_AUTO_OUTPUT_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_AUTO_OUTPUT_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_AUTO_INPUT_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID_EMPTY;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID_INPUT;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID_OUTPUT;
	public static final BlockEntry<Block> PONDER_THERMAL_EMPTY_SLOT;
	public static final BlockEntry<Block> PONDER_THERMAL_INPUT_SLOT;
	public static final BlockEntry<Block> PONDER_THERMAL_OUTPUT_SLOT;
	public static final BlockEntry<Block> PONDER_THERMAL_ENERGY_SLOT;
	public static final BlockEntry<Block> PONDER_THERMAL_ENERGY_FULL;
	public static final BlockEntry<Block> PONDER_THERMAL_ENERGY_EMPTY;
	public static final BlockEntry<Block> PONDER_THERMAL_BACKGROUND;
	public static final BlockEntry<Block> PONDER_THERMAL_CONFIG_PANEL;
	public static final BlockEntry<Block> PONDER_THERMAL_CONFIG_BUTTON;
	public static final BlockEntry<Block> PONDER_THERMAL_AUGMENT_PANEL;
	public static final BlockEntry<Block> PONDER_THERMAL_AUGMENT_BUTTON;
	public static final BlockEntry<Block> PONDER_THERMAL_REDSTONE_PANEL;
	public static final BlockEntry<Block> PONDER_THERMAL_REDSTONE_BUTTON;
	public static final BlockEntry<Block> PONDER_THERMAL_INFORMATION_PANEL;
	public static final BlockEntry<Block> PONDER_THERMAL_INFORMATION_BUTTON;
	public static final BlockEntry<Block> PONDER_THERMAL_ENERGY_PANEL;
	public static final BlockEntry<Block> PONDER_THERMAL_ENERGY_BUTTON;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_FRONT;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_INPUT;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_OUTPUT;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_IO;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_SIDE_NONE;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_TOP;
	public static final BlockEntry<Block> PONDER_THERMAL_MACHINE_BOTTOM;
	public static final BlockEntry<Block> PONDER_THERMAL_REDSTONE_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_LOW_RS_TOUCH_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_HIGH_RS_TOUCH_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_THERMAL_REDSTONE_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_LOW_RS_TOUCH_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_HIGH_RS_TOUCH_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID_LAVA;
	public static final BlockEntry<Block> PONDER_THERMAL_FLUID_WATER;
	public static final BlockEntry<Block> PONDER_THERMAL_ARROW_EMPTY;
	public static final BlockEntry<Block> PONDER_THERMAL_ARROW_FULL;
	public static final BlockEntry<Block> PONDER_THERMAL_FLAME_EMPTY;
	public static final BlockEntry<Block> PONDER_THERMAL_FLAME_FULL;
	public static final BlockEntry<Block> PONDER_THERMAL_SAND_SLOT;
	public static final BlockEntry<Block> PONDER_THERMAL_GLASS_SLOT;
	public static final BlockEntry<Block> PONDER_AE2_EMPTY_SLOT_LIGHT;
	public static final BlockEntry<Block> PONDER_AE2_ARROW;
	public static final BlockEntry<Block> PONDER_AE2_EMPTY_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_EMPTY_PATTERN_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_BACKGROUND_LIGHT;
	public static final BlockEntry<Block> PONDER_AE2_WRITE_BUTTON;
	public static final BlockEntry<Block> PONDER_AE2_BACKGROUND_DARK;
	public static final BlockEntry<Block> PONDER_AE2_CRAFTING_RIGHT_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_AE2_CRAFTING_RIGHT_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_AE2_PROCESSING_RIGHT_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_AE2_PROCESSING_RIGHT_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_AE2_SMITHING_RIGHT_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_AE2_SMITHING_RIGHT_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_AE2_STONECUTTING_RIGHT_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_AE2_STONECUTTING_RIGHT_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_AE2_RIGHT_BUTTON_ON;
	public static final BlockEntry<Block> PONDER_AE2_RIGHT_BUTTON_OFF;
	public static final BlockEntry<Block> PONDER_AE2_BLANK_PATTERN_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_ENCODED_PATTERN_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_DIAMOND_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_STICK_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_DIAMOND_PICKAXE_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_DIAMOND_PICKAXE_SLOT_LIGHT;
	public static final BlockEntry<Block> PONDER_AE2_CHICKEN_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_COOKED_CHICKEN_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_COOKED_CHICKEN_SLOT_LIGHT;
	public static final BlockEntry<Block> PONDER_AE2_COAL_SLOT_LIGHT;
	public static final BlockEntry<Block> PONDER_AE2_IRON_INGOT_SLOT_DARK;
	public static final BlockEntry<Block> PONDER_AE2_IRON_PLATE_SLOT_DARK;

	/** 名称 -> 方块，顺序与 NAMES 一致。 */
	private static final Map<String, BlockEntry<Block>> BY_NAME = new LinkedHashMap<>();

	static {
		GUI_BLOCK = register("gui_block");
		PONDER_THERMAL_AUTO_INPUT_BUTTON_OFF = register("ponder_thermal_auto_input_button_off");
		PONDER_THERMAL_AUTO_OUTPUT_BUTTON_OFF = register("ponder_thermal_auto_output_button_off");
		PONDER_THERMAL_AUTO_OUTPUT_BUTTON_ON = register("ponder_thermal_auto_output_button_on");
		PONDER_THERMAL_AUTO_INPUT_BUTTON_ON = register("ponder_thermal_auto_input_button_on");
		PONDER_THERMAL_FLUID_EMPTY = register("ponder_thermal_fluid_empty");
		PONDER_THERMAL_FLUID_INPUT = register("ponder_thermal_fluid_input");
		PONDER_THERMAL_FLUID_OUTPUT = register("ponder_thermal_fluid_output");
		PONDER_THERMAL_EMPTY_SLOT = register("ponder_thermal_empty_slot");
		PONDER_THERMAL_INPUT_SLOT = register("ponder_thermal_input_slot");
		PONDER_THERMAL_OUTPUT_SLOT = register("ponder_thermal_output_slot");
		PONDER_THERMAL_ENERGY_SLOT = register("ponder_thermal_energy_slot");
		PONDER_THERMAL_ENERGY_FULL = register("ponder_thermal_energy_full");
		PONDER_THERMAL_ENERGY_EMPTY = register("ponder_thermal_energy_empty");
		PONDER_THERMAL_BACKGROUND = register("ponder_thermal_background");
		PONDER_THERMAL_CONFIG_PANEL = register("ponder_thermal_config_panel");
		PONDER_THERMAL_CONFIG_BUTTON = register("ponder_thermal_config_button");
		PONDER_THERMAL_AUGMENT_PANEL = register("ponder_thermal_augment_panel");
		PONDER_THERMAL_AUGMENT_BUTTON = register("ponder_thermal_augment_button");
		PONDER_THERMAL_REDSTONE_PANEL = register("ponder_thermal_redstone_panel");
		PONDER_THERMAL_REDSTONE_BUTTON = register("ponder_thermal_redstone_button");
		PONDER_THERMAL_INFORMATION_PANEL = register("ponder_thermal_information_panel");
		PONDER_THERMAL_INFORMATION_BUTTON = register("ponder_thermal_information_button");
		PONDER_THERMAL_ENERGY_PANEL = register("ponder_thermal_energy_panel");
		PONDER_THERMAL_ENERGY_BUTTON = register("ponder_thermal_energy_button");
		PONDER_THERMAL_MACHINE_FRONT = register("ponder_thermal_machine_front");
		PONDER_THERMAL_MACHINE_SIDE = register("ponder_thermal_machine_side");
		PONDER_THERMAL_MACHINE_SIDE_INPUT = register("ponder_thermal_machine_side_input");
		PONDER_THERMAL_MACHINE_SIDE_OUTPUT = register("ponder_thermal_machine_side_output");
		PONDER_THERMAL_MACHINE_SIDE_IO = register("ponder_thermal_machine_side_io");
		PONDER_THERMAL_MACHINE_SIDE_NONE = register("ponder_thermal_machine_side_none");
		PONDER_THERMAL_MACHINE_TOP = register("ponder_thermal_machine_top");
		PONDER_THERMAL_MACHINE_BOTTOM = register("ponder_thermal_machine_bottom");
		PONDER_THERMAL_REDSTONE_BUTTON_OFF = register("ponder_thermal_redstone_button_off");
		PONDER_THERMAL_LOW_RS_TOUCH_BUTTON_OFF = register("ponder_thermal_low_rs_touch_button_off");
		PONDER_THERMAL_HIGH_RS_TOUCH_BUTTON_OFF = register("ponder_thermal_high_rs_touch_button_off");
		PONDER_THERMAL_REDSTONE_BUTTON_ON = register("ponder_thermal_redstone_button_on");
		PONDER_THERMAL_LOW_RS_TOUCH_BUTTON_ON = register("ponder_thermal_low_rs_touch_button_on");
		PONDER_THERMAL_HIGH_RS_TOUCH_BUTTON_ON = register("ponder_thermal_high_rs_touch_button_on");
		PONDER_THERMAL_FLUID_LAVA = register("ponder_thermal_fluid_lava");
		PONDER_THERMAL_FLUID_WATER = register("ponder_thermal_fluid_water");
		PONDER_THERMAL_ARROW_EMPTY = register("ponder_thermal_arrow_empty");
		PONDER_THERMAL_ARROW_FULL = register("ponder_thermal_arrow_full");
		PONDER_THERMAL_FLAME_EMPTY = register("ponder_thermal_flame_empty");
		PONDER_THERMAL_FLAME_FULL = register("ponder_thermal_flame_full");
		PONDER_THERMAL_SAND_SLOT = register("ponder_thermal_sand_slot");
		PONDER_THERMAL_GLASS_SLOT = register("ponder_thermal_glass_slot");
		PONDER_AE2_EMPTY_SLOT_LIGHT = register("ponder_ae2_empty_slot_light");
		PONDER_AE2_ARROW = register("ponder_ae2_arrow");
		PONDER_AE2_EMPTY_SLOT_DARK = register("ponder_ae2_empty_slot_dark");
		PONDER_AE2_EMPTY_PATTERN_SLOT_DARK = register("ponder_ae2_empty_pattern_slot_dark");
		PONDER_AE2_BACKGROUND_LIGHT = register("ponder_ae2_background_light");
		PONDER_AE2_WRITE_BUTTON = register("ponder_ae2_write_button");
		PONDER_AE2_BACKGROUND_DARK = register("ponder_ae2_background_dark");
		PONDER_AE2_CRAFTING_RIGHT_BUTTON_ON = register("ponder_ae2_crafting_right_button_on");
		PONDER_AE2_CRAFTING_RIGHT_BUTTON_OFF = register("ponder_ae2_crafting_right_button_off");
		PONDER_AE2_PROCESSING_RIGHT_BUTTON_ON = register("ponder_ae2_processing_right_button_on");
		PONDER_AE2_PROCESSING_RIGHT_BUTTON_OFF = register("ponder_ae2_processing_right_button_off");
		PONDER_AE2_SMITHING_RIGHT_BUTTON_ON = register("ponder_ae2_smithing_right_button_on");
		PONDER_AE2_SMITHING_RIGHT_BUTTON_OFF = register("ponder_ae2_smithing_right_button_off");
		PONDER_AE2_STONECUTTING_RIGHT_BUTTON_ON = register("ponder_ae2_stonecutting_right_button_on");
		PONDER_AE2_STONECUTTING_RIGHT_BUTTON_OFF = register("ponder_ae2_stonecutting_right_button_off");
		PONDER_AE2_RIGHT_BUTTON_ON = register("ponder_ae2_right_button_on");
		PONDER_AE2_RIGHT_BUTTON_OFF = register("ponder_ae2_right_button_off");
		PONDER_AE2_BLANK_PATTERN_SLOT_DARK = register("ponder_ae2_blank_pattern_slot_dark");
		PONDER_AE2_ENCODED_PATTERN_SLOT_DARK = register("ponder_ae2_encoded_pattern_slot_dark");
		PONDER_AE2_DIAMOND_SLOT_DARK = register("ponder_ae2_diamond_slot_dark");
		PONDER_AE2_STICK_SLOT_DARK = register("ponder_ae2_stick_slot_dark");
		PONDER_AE2_DIAMOND_PICKAXE_SLOT_DARK = register("ponder_ae2_diamond_pickaxe_slot_dark");
		PONDER_AE2_DIAMOND_PICKAXE_SLOT_LIGHT = register("ponder_ae2_diamond_pickaxe_slot_light");
		PONDER_AE2_CHICKEN_SLOT_DARK = register("ponder_ae2_chicken_slot_dark");
		PONDER_AE2_COOKED_CHICKEN_SLOT_DARK = register("ponder_ae2_cooked_chicken_slot_dark");
		PONDER_AE2_COOKED_CHICKEN_SLOT_LIGHT = register("ponder_ae2_cooked_chicken_slot_light");
		PONDER_AE2_COAL_SLOT_LIGHT = register("ponder_ae2_coal_slot_light");
		PONDER_AE2_IRON_INGOT_SLOT_DARK = register("ponder_ae2_iron_ingot_slot_dark");
		PONDER_AE2_IRON_PLATE_SLOT_DARK = register("ponder_ae2_iron_plate_slot_dark");
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
							provider.models().cubeAll(blockName, provider.modLoc("block/ponder/" + blockName))
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
		return PONDER_THERMAL_BACKGROUND.asStack();
	}

	public static void register() {
		Cmi.LOGGER.info("{} IO Debug Blocks Registered!", Cmi.NAME);
	}
}