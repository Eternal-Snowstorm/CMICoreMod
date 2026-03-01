package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.mechanism.*;
import dev.celestiacraft.cmi.tag.ModItemTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class CmiMechanism {
	public static final ItemEntry<WoodenItem> WOODEN;
	public static final ItemEntry<NuclearItem> NUCLEAR;
	public static final ItemEntry<PigIronItem> PIG_IRON;
	public static final ItemEntry<CopperItem> COPPER;
	public static final ItemEntry<CoilItem> COIL;

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
		COIL = registerMechanism("coil",CoilItem::new)
				.register();
	}

	/**
	 * 对外唯一入口:
	 * 注册一个完整构件, 并自动注册其 incomplete 版本
	 * <p>
	 * complete 由外部手动 register()
	 *
	 * @param name
	 * @param factory
	 * @param <T>
	 * @return
	 */
	private static <T extends Item> ItemBuilder<T, CreateRegistrate> registerMechanism(String name, NonNullFunction<Item.Properties, T> factory) {
		registerIncomplete(name).register();
		return registerComplete(name, factory);
	}

	/**
	 * 注册构件
	 *
	 * @param name
	 * @param factory
	 * @param <T>
	 * @return
	 */
	private static <T extends Item> ItemBuilder<T, CreateRegistrate> registerComplete(String name, NonNullFunction<Item.Properties, T> factory) {
		String registryId = String.format("%s_mechanism", name);

		ItemBuilder<T, CreateRegistrate> builder = Cmi.REGISTRATE.item(registryId, factory);

		builder.model((context, provider) -> {
			String path = String.format("item/mechanism/complete/%s", name);
			provider.generated(context, provider.modLoc(path));
		});

		builder.tag(ModItemTags.MECHANISMS);
		builder.tag(ModItemTags.mechanism(name));

		return builder;
	}

	/**
	 * 半成品
	 *
	 * @param name
	 * @return
	 */
	private static ItemBuilder<SequencedAssemblyItem, CreateRegistrate> registerIncomplete(String name) {
		String registryId = String.format("incomplete_%s_mechanism", name);

		ItemBuilder<SequencedAssemblyItem, CreateRegistrate> builder = Cmi.REGISTRATE.item(registryId, SequencedAssemblyItem::new);

		builder.model((context, provider) -> {
			String path = String.format("item/mechanism/incomplete/%s", name);
			provider.generated(context, provider.modLoc(path));
		});

		builder.tag(ModItemTags.INCOMPLETE_MECHANISMS);

		return builder;
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Mechanisms Registered!");
	}
}