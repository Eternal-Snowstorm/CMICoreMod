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
				"metal_detector",
				"Can search for buried ore",
				"可以搜寻埋藏在地下的矿石"
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
				"solar_boiler.artificial_efficiency",
				" ● Artificial light efficiency: %s mB / Tick",
				" ● 人造光照效率: %s mB / Tick"
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
				"A boiler that produces steam using _natural_ or _artificial light_",
				"一种利用_自然光_或_人造光_生产蒸汽的锅炉"
		);
		addTooltipLang(
				"solar_boiler.workCondition",
				"The boiler runs on light, and efficiency depends on the light source:",
				"锅炉依靠光照运行, 效率取决于光源:"
		);
		addTooltipLang(
				"solar_boiler.condition.1",
				" ● _Natural light_ (no occlusion above, daytime, clear weather): _100%_ efficiency",
				" ● _自然光照_(顶部无遮挡, 白天, 天气晴朗): _100%_ 效率"
		);
		addTooltipLang(
				"solar_boiler.condition.2",
				" ● _Artificial light_ (block light _%1$s_ or higher, e.g. indoors, at night, in rain/snow or when covered): _%2$s%%_ efficiency",
				" ● _人造光照_(方块光照_%1$s_及以上, 如室内, 夜晚, 雨雪天或顶部有遮挡): 效率_%2$s%%_"
		);
		addTooltipLang(
				"solar_boiler.condition.3",
				" ● _In The End_, only the no-occlusion condition needs to be met (_100%_ efficiency)",
				" ● 在_末地_只需满足_无遮挡_条件即可(_100%_ 效率)"
		);
		addTooltipLang(
				"solar_boiler.natural_light",
				"Light Source: Natural(100% Efficiency)",
				"当前光源: 自然光(100%效率)"
		);
		addTooltipLang(
				"solar_boiler.artificial_light",
				"Light Source: Artificial (%s%% Efficiency)",
				"当前光源: 人造光(效率%s%%)"
		);
		addTooltipLang(
				"solar_boiler.no_light",
				"Light Source: None(Cannot Work)",
				"当前光源: 无(无法工作)"
		);
	}
}