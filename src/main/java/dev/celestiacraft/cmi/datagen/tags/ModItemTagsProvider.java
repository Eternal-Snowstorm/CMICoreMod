package dev.celestiacraft.cmi.datagen.tags;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
	public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider blockTags, @Nullable ExistingFileHelper helper) {
		super(output, provider, blockTags.contentsGetter(), Cmi.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
	}
}