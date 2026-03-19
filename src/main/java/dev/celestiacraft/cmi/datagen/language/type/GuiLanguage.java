package dev.celestiacraft.cmi.datagen.language.type;

import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class GuiLanguage extends LanguageGenerate {
	public static void addLang() {
		addCustomLang(
				"gui.cmi.space_elevator_base.title",
				"Build Base",
				"建立地基"
		);
		addCustomLang(
				"gui.cmi.space_elevator_base.cost",
				"Hover Build to preview the required materials",
				"悬停在建造按钮上查看所需材料"
		);
		addCustomLang(
				"gui.cmi.space_elevator_base.confirm",
				"Build the ground base on Earth?",
				"是否在地球建立基站?"
		);
		addCustomLang(
				"gui.cmi.space_elevator_base.build",
				"Build",
				"建造"
		);
		addCustomLang(
				"gui.cmi.space_elevator_base.cancel",
				"Cancel",
				"取消"
		);

		addCustomLang(
				"text.cmi.space_elevator_base.already_built",
				"This space station already has a ground base.",
				"这个空间站已经建立过地面基站了"
		);
		addCustomLang(
				"text.cmi.space_elevator_base.need_earth_launch",
				"Launch from Earth again before building the ground base.",
				"需要先从地球重新发射，才能建立地面基站"
		);
		addCustomLang(
				"text.cmi.space_elevator_base.not_enough_materials",
				"Not enough materials to build the ground base.",
				"材料不足，无法建立地面基站"
		);
		addCustomLang(
				"text.cmi.space_elevator_base.invalid_launch_position",
				"The current launch position cannot build a ground base.",
				"当前起飞位置无法建立地面基站"
		);
		addCustomLang(
				"text.cmi.space_elevator_base.success",
				"Ground base built on Earth.",
				"已在地球建立地面基站"
		);
	}
}
