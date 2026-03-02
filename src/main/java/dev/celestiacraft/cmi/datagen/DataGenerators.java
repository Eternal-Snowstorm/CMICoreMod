package dev.celestiacraft.cmi.datagen;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.datagen.language.LanguageGenerate;
import dev.celestiacraft.cmi.datagen.language.locale.Chinese;
import dev.celestiacraft.cmi.datagen.language.locale.English;
import dev.celestiacraft.cmi.datagen.tags.ModBlockTagsProvider;
import dev.celestiacraft.cmi.datagen.tags.ModFluidTagsProvider;
import dev.celestiacraft.cmi.datagen.tags.ModItemTagsProvider;
import dev.celestiacraft.cmi.worldgen.WorldGenProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	@SubscribeEvent
	public static void onDatagen(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

		// Client
		LanguageGenerate.register();
		generator.addProvider(event.includeClient(), new English(output));
		generator.addProvider(event.includeClient(), new Chinese(output));

		// Server
		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, provider, helper);
		ModItemTagsProvider itemTags = new ModItemTagsProvider(output, provider, blockTags, helper);
		ModFluidTagsProvider fluidTags = new ModFluidTagsProvider(output, provider, helper);
		WorldGenProvider worldGen = new WorldGenProvider(output, provider);

		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(), itemTags);
		generator.addProvider(event.includeServer(), fluidTags);

		generator.addProvider(event.includeServer(), worldGen);
	}
}