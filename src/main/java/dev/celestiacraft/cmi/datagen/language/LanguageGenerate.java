package dev.celestiacraft.cmi.datagen.language;

import dev.celestiacraft.cmi.Cmi;

import java.util.ArrayList;
import java.util.List;

public class LanguageGenerate {
	public static final List<List<String>> TRANSLATION_LIST = new ArrayList<>();

	public static void register() {
	}

	/**
	 * 添加翻译
	 *
	 * @param type    类型
	 * @param key     翻译键
	 * @param english 英文
	 * @param chinese 中文
	 */
	@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
	public static void addLanguage(String type, String key, String english, String chinese) {
		List<String> newList = new ArrayList<>();
		if (type == null) {
			newList.add(String.format("%s.%s", Cmi.MODID, key));
		} else {
			newList.add(String.format("%s.%s.%s", type, Cmi.MODID, key));
		}
		newList.add(english);
		newList.add(chinese);
		TRANSLATION_LIST.add(newList);
	}
}