package dev.celestiacraft.cmi.compat.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.celestiacraft.cmi.common.recipe.accelerator.AcceleratorRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockOrTagComponent implements RecipeComponent<AcceleratorRecipe.Target> {
	public static final BlockOrTagComponent INSTANCE = new BlockOrTagComponent();

	@Override
	public String componentType() {
		return "cmi:block_or_tag";
	}

	@Override
	public Class<?> componentClass() {
		return AcceleratorRecipe.Target.class;
	}

	@Override
	public ComponentRole role() {
		return ComponentRole.OUTPUT;
	}

	@Override
	public JsonElement write(RecipeJS recipe, AcceleratorRecipe.Target value) {
		if (value.isTag()) {
			return new JsonPrimitive("#" + value.tag().location());
		}

		return new JsonPrimitive(String.valueOf(ForgeRegistries.BLOCKS.getKey(value.block())));
	}

	@Override
	public AcceleratorRecipe.Target read(RecipeJS recipe, Object from) {
		if (from instanceof AcceleratorRecipe.Target target) {
			return target;
		}

		if (from instanceof TagKey<?> tag) {
			return AcceleratorRecipe.Target.ofTag(TagKey.create(Registries.BLOCK, tag.location()));
		}

		if (from instanceof Block block) {
			return AcceleratorRecipe.Target.ofBlock(block);
		}

		String value = from instanceof JsonElement element ? element.getAsString() : String.valueOf(from);

		if (!value.isEmpty() && value.charAt(0) == '#') {
			return AcceleratorRecipe.Target.ofTag(TagKey.create(Registries.BLOCK, parseLocation(value.substring(1))));
		}

		Block block = ForgeRegistries.BLOCKS.getValue(parseLocation(value));

		if (block == null) {
			throw new JsonParseException("accelerator 引用了未知方块: " + value);
		}

		return AcceleratorRecipe.Target.ofBlock(block);
	}

	private static ResourceLocation parseLocation(String value) {
		ResourceLocation location = ResourceLocation.tryParse(value);

		if (location == null) {
			throw new JsonParseException("accelerator 引用了非法标识符: " + value);
		}

		return location;
	}
}