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
				"holdForDescriptionCtrl",
				"Hold [%1$s] for Info",
				"按住 [%1$s] 可查看信息"
		);
		addTooltipLang(
				"solar_boiler.satisfy",
				"Working conditions have been met!",
				"已满足工作条件!"
		);
		addTooltipLang(
				"solar_boiler.not_satisfy",
				"Working conditions not met!",
				"未满足工作条件!"
		);
		addTooltipLang(
				"solar_boiler.info",
				"Solar Boiler Info:",
				"太阳能锅炉信息:"
		);
		addTooltipLang(
				"solar_boiler.efficiency",
				" ● Efficiency: %s mB / Tick",
				" ● 效率: %s mB / Tick"
		);
		addTooltipLang(
				"solar_boiler.capacity",
				" ● Capacity: %s mB",
				" ● 容量: %s mB"
		);
		addTooltipLang(
				"solar_boiler.total_capacity",
				" ● Total Capacity: %s mB",
				" ● 总容量: %s mB"
		);
		addTooltipLang(
				"solar_boiler.summary",
				"A boiler that uses _Solar Energy_ to produce steam",
				"一种利用_太阳能_生产蒸汽的锅炉"
		);
		addTooltipLang(
				"solar_boiler.workCondition",
				"The following conditions _Must_ be met before operation:",
				"工作前_必须_确保满足以下条件:"
		);
		addTooltipLang(
				"solar_boiler.condition.1",
				" ● The current time is _Daytime_ or _Dusk_",
				" ● 当前时间处于_白天_或_黄昏_"
		);
		addTooltipLang(
				"solar_boiler.condition.2",
				" ● The weather is clear, with no _Rain_ or _Snow_",
				" ● 天气晴朗, 未处于_雨雪_天气"
		);
		addTooltipLang(
				"solar_boiler.condition.3",
				" ● No full _Cube_ block is obstructing the top",
				" ● 顶上不存在_任何_完整方块遮挡"
		);
		addTooltipLang(
				"solar_boiler.condition.4",
				" ● At The End of the ground only need to follow the condition of no occlusion",
				" ● 在末地只需要遵循_无遮挡_的条件"
		);
	}
}
