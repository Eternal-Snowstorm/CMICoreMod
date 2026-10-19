package dev.celestiacraft.cmi.datagen.language.type;


import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class TooltipLanguage extends LanguageGenerate {
	public static void addLang() {
		// Water Well
		addTooltipLang(
				"water_well.runningEnvironment",
				"Can operate in any environment",
				"可在任何环境下工作"
		);
		// Lava Well
		addTooltipLang(
				"lava_well.runningEnvironment",
				"Can only run in Nether",
				"只能在下界运行"
		);
		// Blazing Blood Well
		addTooltipLang(
				"blazing_blood_well.runningEnvironment",
				"Can only run in Nether, and needs to be in Nether Fortress",
				"只能在下界运行, 且需要处于下界堡垒"
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
		addTooltipLang(
				"nutrition_syringe",
				"After the equipment let you never hunger",
				"装备后让你永不饥饿"
		);
		addTooltipLang(
				"wind_vane.1",
				"\"It is said that praying to the weather vane brings good weather\"",
				"\"据说向风向标祈祷会获得好天气\""
		);
		addTooltipLang(
				"wind_vane.2",
				"\"See? The weather's starting to clear up\"",
				"\"呐, 天气要开始放晴了哦\""
		);
		addTooltipLang(
				"metal_detector",
				"Can search for buried ore",
				"可以搜寻埋藏在地下的矿石"
		);
		addTooltipLang(
				"holdForDescriptionCtrl",
				"Hold [%1$s] for Info",
				"按住 [%1$s] 可查看信息"
		);
	}
}