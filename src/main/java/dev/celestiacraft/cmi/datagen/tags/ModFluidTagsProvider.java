package dev.celestiacraft.cmi.datagen.tags;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
	public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
		super(output, provider, Cmi.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {

	}
}