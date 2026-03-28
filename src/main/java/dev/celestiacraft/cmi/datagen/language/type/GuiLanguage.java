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
				"This space station already has a ground base and docking station.",
				"这个空间站已经建立过地面基站和对接站了"
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
				"Ground base and docking station built successfully.",
				"地面基站和对接站已建立成功"
		);

		addCustomLang(
				"gui.cmi.space_elevator.title",
				"Space Elevator",
				"太空电梯"
		);
		addCustomLang(
				"gui.cmi.space_elevator.header.materials",
				"Required Materials",
				"建造所需材料"
		);
		addCustomLang(
				"gui.cmi.space_elevator.progress",
				"Construction Readiness",
				"建造就绪度"
		);
		addCustomLang(
				"gui.cmi.space_elevator.status.deployed",
				"DEPLOYED",
				"已部署"
		);
		addCustomLang(
				"gui.cmi.space_elevator.status.ready",
				"READY",
				"可建造"
		);
		addCustomLang(
				"gui.cmi.space_elevator.status.missing",
				"MISSING",
				"缺少材料"
		);
		addCustomLang(
				"gui.cmi.space_elevator.status.no_recipe",
				"NO RECIPE",
				"无配方"
		);
		addCustomLang(
				"gui.cmi.space_elevator.footer.deployed",
				"Anchor link online. Elevator entity is already deployed.",
				"锚点链路在线，太空电梯实体已经部署完成。"
		);
		addCustomLang(
				"gui.cmi.space_elevator.footer.ready",
				"Materials stored. Hold right click to build.",
				"材料已存入，长按右键即可建造。"
		);
		addCustomLang(
				"gui.cmi.space_elevator.footer.holding",
				"Material reassembly in progress. Keep holding right click until the build finishes.",
				"材料重组中，保持按住右键直到加载完成。"
		);
		addCustomLang(
				"gui.cmi.space_elevator.footer.missing",
				"Right click to store materials. Fill the list, then hold right click to build.",
				"右键存材料，补齐后长按右键建造。"
		);
		addCustomLang(
				"gui.cmi.space_elevator.footer.no_recipe",
				"No space elevator construction recipe is loaded for this dimension.",
				"当前维度没有加载太空电梯建造配方。"
		);
		addCustomLang(
				"gui.cmi.space_elevator.material.unknown",
				"Unknown Material",
				"未知材料"
		);

		addCustomLang(
				"text.cmi.space_elevator.success",
				"Space elevator deployed successfully.",
				"太空电梯部署成功。"
		);
		addCustomLang(
				"text.cmi.space_elevator.already_present",
				"This ground anchor already has a space elevator entity.",
				"这个地面锚点已经存在太空电梯实体。"
		);
		addCustomLang(
				"text.cmi.space_elevator.not_enough_materials",
				"Not enough stored materials to deploy the space elevator.",
				"已存入材料不足，无法部署太空电梯。"
		);
		addCustomLang(
				"text.cmi.space_elevator.invalid_anchor",
				"This glowing iron pillar is not a valid space elevator anchor.",
				"这个发光铁柱不是有效的太空电梯锚点。"
		);
		addCustomLang(
				"text.cmi.space_elevator.no_link",
				"This elevator is not linked to a space station terminal yet.",
				"这个太空电梯尚未连接到空间站终端。"
		);
		addCustomLang(
				"text.cmi.space_elevator.no_recipe",
				"No space elevator construction recipe is loaded for this dimension.",
				"当前维度没有加载太空电梯建造配方。"
		);
		addCustomLang(
				"text.cmi.space_elevator.spawn_failed",
				"The space elevator entity could not be created on the server.",
				"服务器未能成功创建太空电梯实体。"
		);
		addCustomLang(
				"text.cmi.space_elevator.too_far",
				"You are too far away from the anchor to deploy the space elevator.",
				"你距离锚点太远，无法部署太空电梯。"
		);
		addCustomLang(
				"text.cmi.space_elevator.store_success",
				"Stored available construction materials in the anchor.",
				"已将可用建造材料存入锚点。"
		);
		addCustomLang(
				"text.cmi.space_elevator.store_complete",
				"Materials stored. Hold right click to build.",
				"材料已存入，长按右键即可建造。"
		);
		addCustomLang(
				"text.cmi.space_elevator.store_nothing",
				"No matching construction materials were found in your inventory.",
				"背包中没有可存入的匹配建造材料。"
		);
		addCustomLang(
				"gui.cmi.space_elevator.progress.holding",
				"Building...",
				"正在建造..."
		);
	}
}
