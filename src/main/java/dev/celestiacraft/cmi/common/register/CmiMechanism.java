package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.item.mechanism.NuclearItem;
import dev.celestiacraft.cmi.common.item.mechanism.WoodenItem;
import dev.celestiacraft.cmi.tag.ModItemTags;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CmiMechanism {
	public static final List<ItemEntry<?>> CREATIVE_TAB_ITEMS = new ArrayList<>();
	public static final ItemEntry<NuclearItem> NUCLEAR;
	public static final ItemEntry<WoodenItem> WOODEN;

	static {
		Cmi.REGISTRATE.defaultCreativeTab(Objects.requireNonNull(CmiCreativeTab.MECHANISMS.get()));

		WOODEN = registerMechanism("wooden", WoodenItem::new)
				.register();
		NUCLEAR = registerMechanism("nuclear", NuclearItem::new)
				.register();
	}

	private static <T extends MechanismItem> ItemBuilder<T, CreateRegistrate> registerMechanism(String name, NonNullFunction<Item.Properties, T> factory, boolean addToCreativeTab) {
		String registryId = String.format("%s_mechanism", name);
		ItemBuilder<T, CreateRegistrate> builder = Cmi.REGISTRATE.item(registryId, factory);

		builder.model((context, provider) -> {
			String path = String.format("item/mechanism/%s", name);
			provider.generated(context, provider.modLoc(path));
		});
		builder.tag(ModItemTags.MECHANISMS);
		builder.tag(ModItemTags.mechanism(name));

		if (addToCreativeTab) {
			CREATIVE_TAB_ITEMS.add(builder.register());
		}

		return builder;
	}

	private static <T extends MechanismItem> ItemBuilder<T, CreateRegistrate> registerMechanism(String name, NonNullFunction<Item.Properties, T> factory) {
		return registerMechanism(name, factory, true);
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Mechanisms Registered!");
	}
}