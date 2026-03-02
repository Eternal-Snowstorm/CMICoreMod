package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.MechanismRegister;
import dev.celestiacraft.cmi.common.item.mechanism.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public class CmiMechanism extends MechanismRegister {
	public static final ItemEntry<WoodenItem> WOODEN;
	public static final ItemEntry<StoneItem> STONE;
	public static final ItemEntry<IronItem> IRON;
	public static final ItemEntry<NatureItem> NATURE;
	public static final ItemEntry<PigIronItem> PIG_IRON;
	public static final ItemEntry<PotionItem> POTION;
	public static final ItemEntry<ColorfulItem> COLORFUL;
	public static final ItemEntry<EnchantedItem> ENCHANTED;
	public static final ItemEntry<NetherItem> NETHER;
	public static final ItemEntry<SculkItem> SCULK;
	public static final ItemEntry<EnderItem> ENDER;
	public static final ItemEntry<CopperItem> COPPER;
	public static final ItemEntry<AndesiteItem> ANDESITE;
	public static final ItemEntry<BronzeItem> BRONZE;
	public static final ItemEntry<RailwayItem> RAILWAY;
	public static final ItemEntry<LightEngineeringItem> LIGHT_ENGINEERING;
	public static final ItemEntry<HeavyEngineeringItem> HEAVY_ENGINEERING;
	public static final ItemEntry<CoilItem> COIL;
	public static final ItemEntry<SmartItem> SMART;
	public static final ItemEntry<CobaltItem> COBALT;
	public static final ItemEntry<Photosensitive> PHOTOSENSITIVE;
	public static final ItemEntry<ThermalItem> THERMAL;
	public static final ItemEntry<ReinforcedItem> REINFORCED;
	public static final ItemEntry<GoldItem> GOLD;
	public static final ItemEntry<BasicMekanismItem> BASIC_MEKANISM;
	public static final ItemEntry<AdvancedMekanismItem> ADVANCED_MEKANISM;
	public static final ItemEntry<EliteMekanismItem> ELITE_MEKANISM;
	public static final ItemEntry<UltimateMekanismItem> ULTIMATE_MEKANISM;
	public static final ItemEntry<ComputingItem> COMPUTING;
	public static final ItemEntry<AirTightItem> AIR_TIGHT;
	public static final ItemEntry<TierOneAviationItem> TIER_ONE_AVIATION;
	public static final ItemEntry<TierTwoAviationItem> TIER_TWO_AVIATION;
	public static final ItemEntry<TierThreeAviationItem> TIER_THREE_AVIATION;
	public static final ItemEntry<TierFourAviationItem> TIER_FOUR_AVIATION;
	public static final ItemEntry<NuclearItem> NUCLEAR;
	public static final ItemEntry<AntimatterItem> ANTIMATTER;
	public static final ItemEntry<CreativeItem> CREATIVE;

	static {
		Cmi.REGISTRATE.defaultCreativeTab(ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				Cmi.loadResource("mechanisms")
		));

		WOODEN = registerMechanism("wooden", WoodenItem::new)
				.register();
		STONE = registerMechanism("stone", StoneItem::new)
				.register();
		IRON = registerMechanism("iron", IronItem::new)
				.register();
		NATURE = registerMechanism("nature", NatureItem::new)
				.register();
		PIG_IRON = registerMechanism("pig_iron", PigIronItem::new)
				.register();
		POTION = registerMechanism("potion", PotionItem::new)
				.register();
		COLORFUL = registerMechanism("colorful", ColorfulItem::new)
				.register();
		ENCHANTED = registerMechanism("enchanted", EnchantedItem::new)
				.register();
		NETHER = registerMechanism("nether", NetherItem::new)
				.register();
		SCULK = registerMechanism("sculk", SculkItem::new)
				.register();
		ENDER = registerMechanism("ender", EnderItem::new)
				.register();
		COPPER = registerMechanism("copper", CopperItem::new)
				.register();
		ANDESITE = registerMechanism("andesite", AndesiteItem::new)
				.register();
		BRONZE = registerMechanism("bronze", BronzeItem::new)
				.register();
		RAILWAY = registerMechanism("railway", RailwayItem::new)
				.register();
		LIGHT_ENGINEERING = registerMechanism("light_engineering", LightEngineeringItem::new)
				.register();
		HEAVY_ENGINEERING = registerMechanism("heavy_engineering", HeavyEngineeringItem::new)
				.register();
		COIL = registerMechanism("coil", CoilItem::new)
				.register();
		SMART = registerMechanism("smart", SmartItem::new)
				.register();
		COBALT = registerMechanism("gobalt", CobaltItem::new)
				.register();
		PHOTOSENSITIVE = registerMechanism("photosensitive", Photosensitive::new)
				.register();
		THERMAL = registerMechanism("thermal", ThermalItem::new)
				.register();
		REINFORCED = registerMechanism("reinforced", ReinforcedItem::new)
				.register();
		GOLD = registerMechanism("gold", GoldItem::new)
				.register();
		BASIC_MEKANISM = registerMechanism("basic_mekanism", BasicMekanismItem::new)
				.register();
		ADVANCED_MEKANISM = registerMechanism("advancced_mekanism", AdvancedMekanismItem::new)
				.register();
		ELITE_MEKANISM = registerMechanism("elite_mekanism", EliteMekanismItem::new)
				.register();
		ULTIMATE_MEKANISM = registerMechanism("ultimate_mekanism", UltimateMekanismItem::new)
				.register();
		COMPUTING = registerMechanism("computing", ComputingItem::new)
				.register();
		AIR_TIGHT = registerMechanism("air_tight", AirTightItem::new)
				.register();
		TIER_ONE_AVIATION = registerMechanism("tier_1_aviation", TierOneAviationItem::new)
				.register();
		TIER_TWO_AVIATION = registerMechanism("tier_2_aviation", TierTwoAviationItem::new)
				.register();
		TIER_THREE_AVIATION = registerMechanism("tier_3_aviation", TierThreeAviationItem::new)
				.register();
		TIER_FOUR_AVIATION = registerMechanism("tier_4_aviation", TierFourAviationItem::new)
				.register();
		NUCLEAR = registerMechanism("nuclear", NuclearItem::new)
				.register();
		ANTIMATTER = registerMechanism("antimatter", AntimatterItem::new)
				.register();
		CREATIVE = registerMechanism("creative", CreativeItem::new)
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Mechanisms Registered!");
	}
}