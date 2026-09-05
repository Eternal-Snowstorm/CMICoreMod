package dev.celestiacraft.cmi.datagen.language.type;

import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class ModifierLanguage extends LanguageGenerate {
	public static void addLang() {
		extendo();
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
}