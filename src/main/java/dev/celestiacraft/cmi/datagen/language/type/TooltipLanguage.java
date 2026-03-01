package dev.celestiacraft.cmi.datagen.language.type;


import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class TooltipLanguage extends LanguageGenerate {
	public static void addLang() {
		// Water Pump
		addTooltipLang(
				"water_pump.functional",
				"Water Pump Functional",
				"水井工作正常"
		);
		addTooltipLang(
				"water_pump.non_functional",
				"Water Pump Not Functional",
				"水井结构不完整"
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
	}
}