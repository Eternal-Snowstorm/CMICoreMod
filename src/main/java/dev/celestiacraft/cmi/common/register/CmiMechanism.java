package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.MechanismRegister;
import dev.celestiacraft.cmi.common.item.mechanism.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public class CmiMechanism extends MechanismRegister {
	public static final ItemEntry<WoodenItem> WOODEN;
	public static final ItemEntry<NuclearItem> NUCLEAR;
	public static final ItemEntry<PigIronItem> PIG_IRON;
	public static final ItemEntry<CopperItem> COPPER;
	public static final ItemEntry<CoilItem> COIL;
	public static final ItemEntry<NatureItem> NATURE;
	public static final ItemEntry<EnchantedItem> ENCHANTED;
	public static final ItemEntry<EnderItem> ENDER;
	public static final ItemEntry<PotionItem> POTION;
	public static final ItemEntry<StoneItem> STONE;
	public static final ItemEntry<IronItem> IRON;
	public static final ItemEntry<SculkItem> SCULK;

	static {
		Cmi.REGISTRATE.defaultCreativeTab(ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				Cmi.loadResource("mechanisms")
		));

		WOODEN = registerMechanism("wooden", WoodenItem::new)
				.register();
		NUCLEAR = registerMechanism("nuclear", NuclearItem::new)
				.register();
		PIG_IRON = registerMechanism("pig_iron", PigIronItem::new)
				.register();
		COPPER = registerMechanism("copper", CopperItem::new)
				.register();
		COIL = registerMechanism("coil", CoilItem::new)
				.register();
		NATURE = registerMechanism("nature", NatureItem::new)
				.register();
		ENCHANTED = registerMechanism("enchanted", EnchantedItem::new)
				.register();
		ENDER = registerMechanism("ender", EnderItem::new)
				.register();
		POTION = registerMechanism("potion", PotionItem::new)
				.register();
		STONE = registerMechanism("stone", StoneItem::new)
				.register();
		IRON = registerMechanism("iron", IronItem::new)
				.register();
		SCULK = registerMechanism("sculk", SculkItem::new)
				.register();
	}


	public static void register() {
		Cmi.LOGGER.info("CMI Core Mechanisms Registered!");
	}
}