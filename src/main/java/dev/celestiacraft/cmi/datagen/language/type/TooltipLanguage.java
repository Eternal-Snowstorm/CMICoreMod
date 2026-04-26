package dev.celestiacraft.cmi.datagen.language.type;


import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class TooltipLanguage extends LanguageGenerate {
	public static void addLang() {
		// Water Well
		addTooltipLang(
				"water_well.functional",
				"Water Well Functional",
				"水井工作正常"
		);
		addTooltipLang(
				"water_well.non_functional",
				"Water Well Not Functional",
				"水井结构不完整"
		);
		addTooltipLang(
				"water_well.runningEnvironment",
				"Can operate in any environment",
				"可在任何环境下工作"
		);
		// Lava Well
		addTooltipLang(
				"lava_well.functional",
				"Lava Well Functional",
				"熔岩井工作正常"
		);
		addTooltipLang(
				"lava_well.non_functional",
				"Lava Well Not Functional",
				"熔岩井结构不完整"
		);
		addTooltipLang(
				"lava_well.runningEnvironment",
				"Can only run in Nether",
				"只能在下界运行"
		);
		// Blazing Blood Well
		addTooltipLang(
				"blazing_blood_well.functional",
				"Blazing Blood Well Functional",
				"烈焰血井工作正常"
		);
		addTooltipLang(
				"blazing_blood_well.non_functional",
				"Blazing Blood Well Not Functional",
				"烈焰血井结构不完整"
		);
		addTooltipLang(
				"blazing_blood_well.runningEnvironment",
				"Can only run in Nether",
				"只能在下界运行"
		);
		// Steam Hammer
		addTooltipLang(
				"steam_hammer.summary",
				"Pressing _stacks_ of items while working (Invalid for _Automated Packing_)",
				"工作时可以处理_一整组_物品(对_自动打包_无效)"
		);
		addTooltipLang(
				"steam_hammer.condition1",
				"When working:",
				"工作时:"
		);
		addTooltipLang(
				"steam_hammer.behaviour1",
				"Only functions when _%s mB_ of steam is in the device",
				"内部蒸汽不足 _%s mB_ 时将停止工作"
		);
		addTooltipLang(
				"steam_hammer.behaviour2",
				"Consumes _%s mB_ of steam per operation",
				"每次工作消耗 _%s mB_ 蒸汽"
		);
		// Accelerator Motor
		addTooltipLang(
				"accelerator_motor.behaviour1",
				"A rotational generator that provides _no stress_",
				"不提供_任何应力_的旋转发生器"
		);
		addTooltipLang(
				"accelerator_motor.behaviour2",
				"Its default maximum rotational speed is _%s RPM_",
				"默认最高转速为 _%s RPM_"
		);
		// Void Dust Collector
		addTooltipLang(
				"void_dust_collector.working",
				"Working",
				"虚空粉末收集器正常工作中"
		);
		addTooltipLang(
				"void_dust_collector.unworking",
				"Stopped",
				"虚空粉末收集器工作停止"
		);
		addTooltipLang(
				"void_dust_collector.summary",
				"Can collect void dust while working on vanity spring",
				"放在虚空涌泉上工作时可以收集虚空粉末"
		);
		addTooltipLang(
				"void_dust_collector.isWorking",
				"When working:",
				"工作时:"
		);
		addTooltipLang(
				"void_dust_collector.workTime",
				"Collect one void dust per _%s Tick_",
				"每 _%s Tick_ 收集一个虚空粉末"
		);
		addTooltipLang(
				"void_dust_collector.energyConsumption",
				"Consumes _%s FE_ per Tick",
				"每Tick消耗 _%s FE_"
		);
		addTooltipLang(
				"initial_item_kit.usage",
				"Sneak and Right-click on _any block_ to use",
				"潜行对着_任意方块_右键使用"
		);
		addTooltipLang(
				"initial_item_kit",
				"Make sure you have_%s_free slots",
				"请确保物品栏内有_%s_个空位"
		);
		addTooltipLang(
				"initial_item_kit.list",
				"You will receive:",
				"打开可获得以下物品:"
		);
		addTooltipLang(
				"initial_item_kit.entry",
				"{%s, %s}%s",
				"{%s, %s}%s"
		);
		addTooltipLang(
				"initial_item_kit.hold_shift",
				"Hold _Shift_ for details",
				"按住 _Shift_ 查看列表"
		);
	}
}