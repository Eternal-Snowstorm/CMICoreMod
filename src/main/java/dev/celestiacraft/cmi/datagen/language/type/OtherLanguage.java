package dev.celestiacraft.cmi.datagen.language.type;


import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class OtherLanguage extends LanguageGenerate {
	public static void addLang() {
		addCreativeTabLang(
				"mechanisms",
				"CMI: Mechanisms",
				"构件与革新: 构件"
		);
		addCustomLang(
				"curios.identifier.mechanisms",
				"Mechanisms",
				"构件"
		);
		addLanguage(
				"multiblock.building",
				"water_pump",
				"Multiblock Water Pump Build",
				"多方块水井搭建"
		);
		addBiomeLanguage(
				"andesite_cave",
				"Andesite Cave",
				"安山岩洞窟"
		);
		addLanguage(
				"pattern",
				"mechanism",
				"Mechanism",
				"构件"
		);
		// Jede
		addLanguage(
				"config.jade",
				"plugin_cmi.common",
				"CMI",
				"CMI"
		);
		addLanguage(
				"jade.tip",
				"powered.off",
				"State: off",
				"状态: 关"
		);
		addLanguage(
				"jade.tip",
				"powered.on",
				"State: on",
				"状态: 开"
		);
		addRecipeLang(
				"freezing",
				"Freezing",
				"冷冻"
		);
		addKeyLang(
				"categories",
				"CMI",
				"机械动力: 构件与革新"
		);
		addKeyLang(
				"open_radial",
				"Open Radial",
				"打开轮盘"
		);
		addRadialLang(
				"open_quest",
				"Open Quests",
				"打开任务书"
		);
		addRadialLang(
				"claim_chunk",
				"Claim Chunk",
				"认领区块"
		);
	}
}