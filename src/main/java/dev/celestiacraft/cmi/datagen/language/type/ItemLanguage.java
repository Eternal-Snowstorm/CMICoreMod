package dev.celestiacraft.cmi.datagen.language.type;

import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemLanguage extends LanguageGenerate {
	public static void addLang() {
		List<List<String>> mechList = List.of(
				setInfo("nature", "Nature", "自然"),
				setInfo("wooden", "Wooden", "木质"),
				setInfo("stone", "Stone", "石质"),
				setInfo("iron", "Iron", "铁质"),
				setInfo("copper", "Copper", "铜质"),
				setInfo("andesite", "Andesite", "安山"),
				setInfo("photosensitive", "Photosensitive", "感光"),
				setInfo("gold", "Gold", "金质"),
				setInfo("cobalt", "Cobalt", "钴质"),
				setInfo("thermal", "Thermal", "热力"),
				setInfo("reinforced", "Reinforced", "强化"),
				setInfo("resonant", "Resonant", "谐振"),
				setInfo("railway", "Railway", "铁路"),
				setInfo("ender", "Ender", "末影"),
				setInfo("light_engineering", "Light Engineering", "轻型工程"),
				setInfo("heavy_engineering", "Heavy Engineering", "重型工程"),
				setInfo("enchanted", "Enchanted", "附魔"),
				setInfo("smart", "Smart", "智能"),
				setInfo("computing", "Computing", "计算"),
				setInfo("tier_1_aviation", "Tier 1 Aviation", "壹级科技航天"),
				setInfo("tier_2_aviation", "Tier 2 Aviation", "贰级科技航天"),
				setInfo("tier_3_aviation", "Tier 3 Aviation", "叁级科技航天"),
				setInfo("tier_4_aviation", "Tier 4 Aviation", "肆级科技航天"),
				setInfo("basic_mekanism", "Basic Mekanism", "基础通用"),
				setInfo("advanced_mekanism", "Advanced Mekanism", "高级通用"),
				setInfo("elite_mekanism", "Elite Mekanism", "精英通用"),
				setInfo("ultimate_mekanism", "Ultimate Mekanism", "终极通用"),
				setInfo("nuclear", "Nuclear", "核"),
				setInfo("antimatter", "Antimatter", "反物质"),
				setInfo("coil", "Coil", "线圈"),
				setInfo("sculk", "Sculk", "幽匿"),
				setInfo("colorful", "Colorful", "多彩"),
				setInfo("pig_iron", "Pig Iron", "生铁"),
				setInfo("nether", "Nether", "下界"),
				setInfo("creative", "Creative", "创造"),
				setInfo("precision", "Precision", "精密"),
				setInfo("redstone", "Redstone", "红石"),
				setInfo("potion", "Potion", "秘药"),
				setInfo("bronze", "Bronze", "青铜"),
				setInfo("air_tight", "Air Tight", "气密")
		);
		mechList.forEach((mech) -> {
			String id = mech.get(0);
			String en = mech.get(1);
			String zh = mech.get(2);

			String mechKey = String.format("%s_mechanism", id);

			addItemLanguage(mechKey, en, zh);
		});
	}

	private static @NotNull List<String> setInfo(String id, String en, String zh) {
		return List.of(id, en, zh);
	}
}