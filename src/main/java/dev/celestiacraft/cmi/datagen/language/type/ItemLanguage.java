package dev.celestiacraft.cmi.datagen.language.type;

import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemLanguage extends LanguageGenerate {
	public static void addLang() {
		mechLang();
		addItemLanguage(
				"mystic_pomelo",
				"Mystic Pomelo",
				"神秘的柚子"
		);
		addItemLanguage(
				"initial_item_kit",
				"Initial Item Kit",
				"初始套件"
		);
	}

	private static @NotNull List<String> info(String id, String en, String zh) {
		return List.of(id, en, zh);
	}

	private static void mechLang() {
		List<List<String>> mechList = List.of(
				info("wooden", "Storage", "存储"),
				info("stone", "Heat", "热能"),
				info("iron", "Magnetic", "磁力"),
				info("redstone", "Redstone", "红石"),
				info("nature", "Nature", "自然"),
				info("pig_iron", "Pig Iron", "生铁"),
				info("potion", "Potion", "秘药"),
				info("colorful", "Colorful", "多彩"),
				info("enchanted", "Enchanted", "附魔"),
				info("nether", "Nether", "下界"),
				info("sculk", "Sculk", "幽匿"),
				info("ender", "Ender", "末影"),
				info("copper", "Fluid", "流体"),
				info("andesite", "Kinetic", "动能"),
				info("bronze", "Steam", "蒸汽"),
				info("railway", "Railway", "铁路"),
				info("precision", "Precision", "精密"),
				info("light_engineering", "Light Engineering", "轻型工程"),
				info("heavy_engineering", "Heavy Engineering", "重型工程"),
				info("coil", "Coil", "线圈"),
				info("smart", "Smart", "智能"),
				info("cobalt", "Accelerate", "增速"),
				info("photosensitive", "Photosensitive", "感光"),
				info("thermal", "Thermal", "热力"),
				info("reinforced", "Reinforced", "强化"),
				info("gold", "Parallel", "并行"),
				info("basic_mekanism", "Basic General", "基础通用"),
				info("advanced_mekanism", "Advanced General", "高级通用"),
				info("elite_mekanism", "Elite General", "精英通用"),
				info("ultimate_mekanism", "Ultimate General", "终极通用"),
				info("computing", "Computing", "计算"),
				info("air_tight", "Air Tight", "气密"),
				info("tier_1_aviation", "Tier 1 Aviation", "壹级科技航天"),
				info("tier_2_aviation", "Tier 2 Aviation", "贰级科技航天"),
				info("tier_3_aviation", "Tier 3 Aviation", "叁级科技航天"),
				info("tier_4_aviation", "Tier 4 Aviation", "肆级科技航天"),
				info("nuclear", "Nuclear", "核"),
				info("antimatter", "Antimatter", "反物质"),
				info("creative", "Creative", "创造")
		);
		mechList.forEach((mech) -> {
			String id = mech.get(0);
			String en = mech.get(1);
			String zh = mech.get(2);

			String completeKey = String.format("%s_mechanism", id);
			String completeEn = String.format("%s Mechanism", en);
			String completeZh = String.format("%s构件", zh);

			addItemLanguage(completeKey, completeEn, completeZh);

			String incompleteKey = String.format("incomplete_%s_mechanism", id);
			String incompleteEn = String.format("Incomplete %s Mechanism", en);
			String incompleteZh = String.format("%s构件(半成品)", zh);

			addItemLanguage(incompleteKey, incompleteEn, incompleteZh);
		});
	}
}