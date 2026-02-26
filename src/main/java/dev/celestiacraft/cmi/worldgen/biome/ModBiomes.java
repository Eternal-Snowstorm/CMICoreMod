package dev.celestiacraft.cmi.worldgen.biome;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.*;

public class ModBiomes {
	public static final ResourceKey<Biome> ANDESITE_CAVE = register("andesite_cave");

	public static void bootstrap(BootstapContext<Biome> context) {
		context.register(ANDESITE_CAVE, andesiteCave(context));
	}

	private static Biome andesiteCave(BootstapContext<Biome> context) {
		MobSpawnSettings.Builder mobBuilder = new MobSpawnSettings.Builder();

		BiomeDefaultFeatures.caveSpawns(mobBuilder);
		BiomeDefaultFeatures.monsters(mobBuilder, 95, 5, 100, false);

		BiomeGenerationSettings.Builder genBuilder =
				new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE),
						context.lookup(Registries.CONFIGURED_CARVER));

		BiomeDefaultFeatures.addPlainGrass(genBuilder);
		BiomeDefaultFeatures.addDefaultOres(genBuilder, true);
		BiomeDefaultFeatures.addPlainVegetation(genBuilder);
		BiomeDefaultFeatures.addDefaultMushrooms(genBuilder);
		BiomeDefaultFeatures.addDefaultExtraVegetation(genBuilder);

		Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);

		return new Biome.BiomeBuilder()
				.hasPrecipitation(true)
				.temperature(0.8F)
				.downfall(0.4F)
				.mobSpawnSettings(mobBuilder.build())
				.generationSettings(genBuilder.build())
				.specialEffects(new BiomeSpecialEffects.Builder()
						.backgroundMusic(music)
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(0x78A7FF)
						.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
						.build())
				.build();
	}

	private static ResourceKey<Biome> register(String name) {
		return ResourceKey.create(Registries.BIOME, Cmi.loadResource(name));
	}
}