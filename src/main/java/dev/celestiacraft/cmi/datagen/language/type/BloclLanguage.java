package dev.celestiacraft.cmi.datagen.language.type;


import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;

public class BloclLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"block",
				"water_pump",
				"Water Pump",
				"水泵"
		);
	}
}