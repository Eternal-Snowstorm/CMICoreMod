package dev.celestiacraft.cmi.datagen.language.type;

import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemLanguage extends LanguageGenerate {
	public static void addLang() {
		List<List<String>> mechList = List.of(
				setInfo("wooden", "Storage", "存储"),
				setInfo("stone", "Heat", "热能"),
				setInfo("iron", "Magnetic", "磁力"),
				setInfo("redstone", "Redstone", "红石"),
				setInfo("nature", "Nature", "自然"),
				setInfo("pig_iron", "Pig Iron", "生铁"),
				setInfo("potion", "Potion", "秘药"),
				setInfo("colorful", "Colorful", "多彩"),
				setInfo("enchanted", "Enchanted", "附魔"),
				setInfo("nether", "Nether", "下界"),
				setInfo("sculk", "Sculk", "幽匿"),
				setInfo("ender", "Ender", "末影"),
				setInfo("copper", "Fluid", "流体"),
				setInfo("andesite", "Kinetic", "动能"),
				setInfo("bronze", "Steam", "蒸汽"),
				setInfo("railway", "Railway", "铁路"),
				setInfo("precision", "Precision", "精密"),
				setInfo("light_engineering", "Light Engineering", "轻型工程"),
				setInfo("heavy_engineering", "Heavy Engineering", "重型工程"),
				setInfo("coil", "Coil", "线圈"),
				setInfo("smart", "Smart", "智能"),
				setInfo("cobalt", "Accelerate", "增速"),
				setInfo("photosensitive", "Photosensitive", "感光"),
				setInfo("thermal", "Thermal", "热力"),
				setInfo("reinforced", "Reinforced", "强化"),
				setInfo("gold", "Parallel", "并行"),
				setInfo("basic_mekanism", "Basic General", "基础通用"),
				setInfo("advanced_mekanism", "Advanced General", "高级通用"),
				setInfo("elite_mekanism", "Elite General", "精英通用"),
				setInfo("ultimate_mekanism", "Ultimate General", "终极通用"),
				setInfo("computing", "Computing", "计算"),
				setInfo("air_tight", "Air Tight", "气密"),
				setInfo("tier_1_aviation", "Tier 1 Aviation", "壹级科技航天"),
				setInfo("tier_2_aviation", "Tier 2 Aviation", "贰级科技航天"),
				setInfo("tier_3_aviation", "Tier 3 Aviation", "叁级科技航天"),
				setInfo("tier_4_aviation", "Tier 4 Aviation", "肆级科技航天"),
				setInfo("nuclear", "Nuclear", "核"),
				setInfo("antimatter", "Antimatter", "反物质"),
				setInfo("creative", "Creative", "创造")
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

	private static @NotNull List<String> setInfo(String id, String en, String zh) {
		return List.of(id, en, zh);
	}
}