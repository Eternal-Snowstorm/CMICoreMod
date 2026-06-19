package dev.celestiacraft.cmi.datagen.tags;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.tags.CmiFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.concurrent.CompletableFuture;

public class CmiFluidTagsProvider extends FluidTagsProvider {
	public CmiFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
		super(output, provider, Cmi.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(CmiFluidTags.GG_WORK_FLUID)
				.add(Fluids.LAVA)
				.add(TinkerFluids.blazingBlood.get())
				.add(TinkerFluids.magma.get());
	}
}