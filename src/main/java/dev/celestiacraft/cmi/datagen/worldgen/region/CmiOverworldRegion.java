package dev.celestiacraft.cmi.datagen.worldgen.region;

import com.mojang.datafixers.util.Pair;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiBiome;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class CmiOverworldRegion extends Region {
	public CmiOverworldRegion(int weight) {
		super(Cmi.loadResource("overworld"), RegionType.OVERWORLD, weight);
	}

	@Override
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
		addModifiedVanillaOverworldBiomes(mapper, (builder) -> {
			builder.replaceBiome(Biomes.DRIPSTONE_CAVES, CmiBiome.ANDESITE_CAVE);
		});
	}
}