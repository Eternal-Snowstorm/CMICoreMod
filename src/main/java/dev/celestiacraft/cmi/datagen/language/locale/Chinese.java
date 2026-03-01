package dev.celestiacraft.cmi.datagen.language.locale;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.List;

public class Chinese extends LanguageProvider {
	public Chinese(PackOutput output) {
		super(output, Cmi.MODID, "zh_cn");
	}

	@Override
	protected void addTranslations() {
		for (List<String> item : LanguageGenerate.TRANSLATION_LIST) {
			add(item.get(0), item.get(2));
		}
	}
}