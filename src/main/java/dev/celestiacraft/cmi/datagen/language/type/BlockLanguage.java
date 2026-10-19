package dev.celestiacraft.cmi.datagen.language.type;

import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class BlockLanguage extends LanguageGenerate {
	public static void addLang() {
		addBlockLanguage(
				"accelerator",
				"Mechanism Accelerator",
				"构件催生器"
		);
		addBlockLanguage(
				"water_well",
				"Water Well",
				"多方块水井"
		);
		addBlockLanguage(
				"lava_well",
				"Lava Well",
				"多方块熔岩井"
		);
		addBlockLanguage(
				"blazing_blood_well",
				"Blazing Blood Well",
				"多方块烈焰血井"
		);
		addBlockLanguage(
				"mars_geothermal_vent",
				"Ares Gerthermal Vent",
				"阿瑞斯地热通口"
		);
		addBlockLanguage(
				"mercury_geothermal_vent",
				"Hermes Geothermal Vent",
				"赫尔墨斯地热通口"
		);
		addBlockLanguage(
				"accelerator_motor",
				"Accelerator Motor",
				"变速马达"
		);
		addBlockLanguage(
				"void_dust_collector",
				"Void Dust Collector",
				"虚空粉末收集器"
		);
		addBlockLanguage(
				"mechanical_belt_grinder",
				"Mechanical Belt Grinder",
				"动力磨砂机"
		);
		addBlockLanguage(
				"nahuatl_scaffold",
				"Nahuatl Scaffold",
				"纳瓦特尔脚手架"
		);
		addBlockLanguage(
				"blazewood_scaffold",
				"Blazewood Scaffold",
				"烈焰木脚手架"
		);
		addBlockLanguage(
				"iron_scaffold",
				"Iron Scaffold",
				"铁脚手架"
		);
		addBlockLanguage(
				"space_elevator_base_console",
				"Space Elevator Base Console",
				"太空电梯基座控制台"
		);
		addBlockLanguage(
				"space_elevator_io_port",
				"Space Elevator IO Port",
				"太空电梯接口"
		);
		addBlockLanguage(
				"space_elevator_top",
				"Space Elevator Top Receiver",
				"太空电梯顶部接收器"
		);
		addBlockLanguage(
				"wind_vane",
				"Wind Vane",
				"风向标"
		);
		addCogWheelLang(
				"bronze",
				"Bronze",
				"青铜"
		);
		addCogWheelLang(
				"cast_iron",
				"Cast Iron",
				"铸铁"
		);
		addCogWheelLang(
				"steel",
				"Steel",
				"钢"
		);
		addCoilLang(
				"copper",
				"Copper",
				"铜"
		);
		addCoilLang(
				"electrum",
				"Electrum",
				"琥珀金"
		);
		addCoilLang(
				"steel",
				"High-Voltage",
				"高压"
		);
		addBlockLanguage(
				"glacian_sapling",
				"Glacian Sapling",
				"霜原树苗"
		);
		addBlockLanguage(
				"gold_sapling",
				"Gold Sapling",
				"黄金树苗"
		);
		addBlockLanguage(
				"geothermal_generator",
				"Geothermal Generator",
				"地热发电机"
		);
		addBlockLanguage(
				"test_coke_oven",
				"Test Coke Oven",
				"测试焦炭炉"
		);
		addBlockLanguage(
				"test_coke_oven_io",
				"Test Coke Oven IO Port",
				"测试焦炭炉IO端口"
		);
		addBlockLanguage(
				"test_gravel",
				"Test Gravel",
				"测试砂砾"
		);
		addBlockLanguage(
				"test_multiblock_controller",
				"Test Multiblock Controller",
				"测试多方块控制器"
		);
	}

	private static void addCogWheelLang(String material, String english, String chinese) {
		addBlockLanguage(
				"%s_cogwheel".formatted(material),
				"%s Cogwheel".formatted(english),
				"%s齿轮".formatted(chinese)
		);
		addBlockLanguage(
				"%s_large_cogwheel".formatted(material),
				"%s Large Cogwheel".formatted(english),
				"%s大齿轮".formatted(chinese)
		);
	}

	private static void addCoilLang(String material, String english, String chinese) {
		addBlockLanguage(
				"%s_coil".formatted(material),
				"%s Coil Block".formatted(english),
				"%s线圈".formatted(chinese)
		);
	}
}