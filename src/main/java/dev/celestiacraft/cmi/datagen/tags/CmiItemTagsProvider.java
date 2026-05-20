package dev.celestiacraft.cmi.datagen.tags;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiItem;
import dev.celestiacraft.cmi.tags.CmiItemTags;
import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CmiItemTagsProvider extends ItemTagsProvider {
	public CmiItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider blockTags, @Nullable ExistingFileHelper helper) {
		super(output, provider, blockTags.contentsGetter(), Cmi.MODID, helper);
	}

	private static final List<String> ALL_SLOTS = List.of(
			"back",
			"belt",
			"body",
			"bracelet",
			"charm",
			"curio",
			"hands",
			"head",
			"necklace",
			"ring",
			"mechanism"
	);

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		addAllSlot();

		tag(CmiItemTags.WORKBENCHES)
				.add(Items.CRAFTING_TABLE);
	}

	private void addAllSlot() {
		ALL_SLOTS.forEach((slot) -> {
			TagKey<Item> curiosTag = TagsBuilder.item(slot).namespace("curios");
			tag(curiosTag)
					.add(CmiItem.NUTRITION_SYRINGE.asItem());
		});
	}
}