package dev.celestiacraft.cmi.common.feature.cargogrid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public sealed interface CargoGridRule {
	String matchType();

	String pattern();

	CargoGridDimensions dimensions();

	@Nullable
	Integer backgroundColor();

	boolean matches(ItemStack stack);

	record ById(String pattern, CargoGridDimensions dimensions, @Nullable Integer backgroundColor) implements CargoGridRule {
		@Override
		public String matchType() {
			return "item";
		}

		@Override
		public boolean matches(ItemStack stack) {
			if (stack.isEmpty()) return false;
			ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
			return id.toString().equals(pattern);
		}
	}

	record ByTag(String pattern, TagKey<Item> tagKey, CargoGridDimensions dimensions, @Nullable Integer backgroundColor) implements CargoGridRule {
		public static ByTag of(String raw, CargoGridDimensions dims, @Nullable Integer color) {
			String trimmed = raw.startsWith("#") ? raw.substring(1) : raw;
			ResourceLocation id = ResourceLocation.tryParse(trimmed);
			if (id == null) return null;
			return new ByTag(raw, TagKey.create(Registries.ITEM, id), dims, color);
		}

		@Override
		public String matchType() {
			return "tag";
		}

		@Override
		public boolean matches(ItemStack stack) {
			if (stack.isEmpty()) return false;
			return stack.is(tagKey);
		}
	}

	record ByRegex(String pattern, Pattern compiled, CargoGridDimensions dimensions, @Nullable Integer backgroundColor) implements CargoGridRule {
		public static ByRegex of(String raw, CargoGridDimensions dims, @Nullable Integer color) {
			try {
				return new ByRegex(raw, Pattern.compile(raw), dims, color);
			} catch (PatternSyntaxException ex) {
				return null;
			}
		}

		@Override
		public String matchType() {
			return "regex";
		}

		@Override
		public boolean matches(ItemStack stack) {
			if (stack.isEmpty()) return false;
			ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
			return compiled.matcher(id.toString()).matches();
		}
	}
}
