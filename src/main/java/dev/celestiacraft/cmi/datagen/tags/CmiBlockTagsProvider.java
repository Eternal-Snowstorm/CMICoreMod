package dev.celestiacraft.cmi.datagen.tags;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.tags.CmiBlockTags;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CmiBlockTagsProvider extends BlockTagsProvider {
	public CmiBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, Cmi.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(CmiBlockTags.FREEZING_CATALYST)
				.add(ModBlocks.CRYO_FUEL.get())
				.add(Blocks.POWDER_SNOW);

		tag(CmiBlockTags.SMOKE_SOURCE)
				.add(Blocks.HAY_BLOCK)
				.addTag(Tags.Blocks.STORAGE_BLOCKS_COAL);
	}
}