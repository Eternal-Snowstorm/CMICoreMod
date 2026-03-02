package dev.celestiacraft.cmi.datagen.tags;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.tag.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
	public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, Cmi.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
	}
}