package dev.celestiacraft.cmi.api.client.textures;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class Items {
	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> generated(ResourceLocation texture) {
		return (context, provider) -> {
			provider.generated(context::getEntry, texture);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> generated(String texture) {
		return (context, provider) -> {
			provider.generated(context::getEntry, provider.modLoc(texture));
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> handheld(ResourceLocation texture) {
		return (context, provider) -> {
			provider.handheld(context::getEntry, texture);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> handheld(String texture) {
		return (context, provider) -> {
			provider.handheld(context::getEntry, provider.modLoc(texture));
		};
	}
}