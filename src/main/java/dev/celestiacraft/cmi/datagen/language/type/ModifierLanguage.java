package dev.celestiacraft.cmi.datagen.language.type;

import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class ModifierLanguage extends LanguageGenerate {
	public static void addLang() {
		extendo();
		diving();
	}

	private static void extendo() {
		addModifierLanguage(
				"extendo",
				"Engineer's Extendo Grip",
				"工程师伸缩机械手"
		);
		addModifierFlavorLanguage(
				"extendo",
				"Boioioing",
				"鞭长可及"
		);
		addModifierDescriptionLanguage(
				"extendo",
				"Increase the intersection range",
				"提高互交触及距离"
		);
	}

	private static void diving() {
		addModifierLanguage(
				"diving",
				"Diving",
				"潜水"
		);
		addModifierFlavorLanguage(
				"diving",
				"Breathe from the tank",
				"罐中呼吸"
		);
		addModifierDescriptionLanguage(
				"diving",
				"Consumes backtank air to breathe underwater or in the Nether",
				"在水下或下界中消耗背罐空气来维持呼吸"
		);
	}
}