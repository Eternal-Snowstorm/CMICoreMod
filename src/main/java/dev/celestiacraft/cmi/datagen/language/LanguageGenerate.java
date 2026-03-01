package dev.celestiacraft.cmi.datagen.language;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.datagen.language.type.*;

import java.util.ArrayList;
import java.util.List;

public class LanguageGenerate {
	public static final List<List<String>> TRANSLATION_LIST = new ArrayList<>();

	public static void register() {
		ItemLanguage.addLang();
		BlockLanguage.addLang();
		OtherLanguage.addLang();
		TooltipLanguage.addLang();
		JeiLanguage.addLang();
		GuiLanguage.addLang();
	}

	public static void addLanguage(String type, String key, String english, String chinese) {
		String fullKey;

		if (type == null || type.isEmpty()) {
			fullKey = String.format("%s.%s", Cmi.MODID, key);
		} else {
			fullKey = String.format("%s.%s.%s", type, Cmi.MODID, key);
		}

		addCustomLang(fullKey, english, chinese);
	}

	/**
	 * 添加自定义翻译
	 *
	 * @param key     翻译键
	 * @param english 英文
	 * @param chinese 中文
	 */
	public static void addCustomLang(String key, String english, String chinese) {
		List<String> newList = new ArrayList<>();
		newList.add(key);
		newList.add(english);
		newList.add(chinese);
		TRANSLATION_LIST.add(newList);
	}

	public static void addBlockLanguage(String key, String english, String chinese) {
		addLanguage("block", key, english, chinese);
	}

	public static void addItemLanguage(String key, String english, String chinese) {
		addLanguage("item", key, english, chinese);
	}

	public static void addBiomeLanguage(String key, String english, String chinese) {
		addLanguage("biome", key, english, chinese);
	}

	public static void addCreativeTabLang(String key, String english, String chinese) {
		addLanguage("itemGroup", key, english, chinese);
	}

	public static void addTooltipLang(String key, String english, String chinese) {
		String fullKey = String.format("cmi.tooltip.%s", key);
		addCustomLang(fullKey, english, chinese);
	}

	public static void addJeiCategoryLang(String key, String english, String chinese) {
		addLanguage("jei.category", key, english, chinese);
	}
}