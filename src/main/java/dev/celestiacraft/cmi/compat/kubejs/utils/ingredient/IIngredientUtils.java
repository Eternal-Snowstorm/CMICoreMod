package dev.celestiacraft.cmi.compat.kubejs.utils.ingredient;

import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface IIngredientUtils {
	static ItemStack getFirstItemId(Ingredient tagOrItem) {
		ItemStack[] ids = Arrays.stream(IngredientWrapper.of(tagOrItem).getItems())
				.toList()
				.toArray(new ItemStack[0]);

		if (ids.length > 0) {
			return ids[0];
		} else {
			ConsoleJS.SERVER.warn("No corresponding item under %s".formatted(tagOrItem));
			return null;
		}
	}

	static boolean isNotNull(Ingredient tag) {
		return IngredientWrapper.of(tag).getItems().length > 0;
	}

	static String getUnifiedItemId(Ingredient ingredient, List<String> namespacePriority) {
		ItemStack[] stacks = IngredientWrapper.of(ingredient).getItems();

		String outputId = null;
		Integer priorityValue = null;

		for (ItemStack stack : stacks) {
			ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());
			if (rl == null) continue;

			String currentNamespace = rl.getNamespace();
			String id = rl.toString();

			// 遍历优先级
			for (int i = 0; i < namespacePriority.size(); i++) {
				if (currentNamespace.equals(namespacePriority.get(i))) {
					if (priorityValue == null || i <= priorityValue) {
						outputId = id;
						priorityValue = i;
					}
				}
			}
		}

		return outputId;
	}

	static ResourceLocation getFirstFluidId(String fluidTag) {
		TagKey<Fluid> tag = FluidTags.create(ResourceLocation.parse(fluidTag));
		Optional<HolderSet.Named<Fluid>> optional = BuiltInRegistries.FLUID.getTag(tag);

		if (optional.isPresent()) {
			Holder<Fluid> fluidHolder = optional.get()
					.stream()
					.findFirst()
					.orElse(null);

			if (fluidHolder != null) {
				ResourceLocation fluidKey = ForgeRegistries.FLUIDS.getKey(fluidHolder.value());
				ConsoleJS.SERVER.debug("The first fluid is: %s".formatted(fluidKey));
				return fluidKey;
			}
		}
		ConsoleJS.SERVER.warn("No corresponding fluid under %s".formatted(fluidTag));
		return null;
	}

	static String getFluidString(String fluidTag) {
		ResourceLocation id = getFirstFluidId(fluidTag);
		return id != null ? id.toString() : null;
	}

	static String getPath(String name) {
		return name.contains(":") ? name.split(":")[1] : name;
	}
}