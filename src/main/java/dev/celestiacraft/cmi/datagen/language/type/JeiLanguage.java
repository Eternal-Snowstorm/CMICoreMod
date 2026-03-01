package dev.celestiacraft.cmi.datagen.language.type;


import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class JeiLanguage extends LanguageGenerate {
	public static void addLang() {
		// 虚空粉末
		addJeiCategoryLang(
				"void_dust_collector",
				"Void Dust Collector",
				"虚空粉末收集"
		);
		// 构件催生
		addJeiCategoryLang(
				"accelerator",
				"Mechanism Accelerator",
				"构件催生"
		);
		addJeiCategoryLang(
				"accelerator.1",
				"Use a mechanism on the accelerator",
				"对着催生器使用构件"
		);
		addJeiCategoryLang(
				"accelerator.2",
				"Requires at least 24 triggerable blocks in a 5×5 area around",
				"需要周围5x5范围内至少24个可触发方块"
		);
		// 水井
		addJeiCategoryLang(
				"water_pump",
				"Multiblock Water Pump",
				"多方块水井"
		);
		addJeiCategoryLang(
				"water_pump_sea_water",
				"Multiblock Water Pump",
				"多方块水井(海水)"
		);
		addJeiCategoryLang(
				"water_pump.complete",
				"Structural Complete",
				"结构完整"
		);
		addJeiCategoryLang(
				"water_pump.ocean",
				"In the ocean biome",
				"处于海洋群系"
		);
		addJeiCategoryLang(
				"water_pump.pos",
				"Core block at coordinate Y = 62",
				"核心方块处于坐标Y = 62"
		);
		// 打磨
		addJeiCategoryLang(
				"grinding",
				"Grinding",
				"打磨"
		);
	}
}