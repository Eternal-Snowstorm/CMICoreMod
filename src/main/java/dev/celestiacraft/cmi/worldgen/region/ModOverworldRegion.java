package dev.celestiacraft.cmi.worldgen.region;

import com.mojang.datafixers.util.Pair;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.worldgen.biome.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class ModOverworldRegion extends Region {
	public ModOverworldRegion(int weight) {
		super(Cmi.loadResource("overworld"), RegionType.OVERWORLD, weight);
	}

	@Override
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
		this.addModifiedVanillaOverworldBiomes(mapper, (builder) -> {
			builder.replaceBiome(Biomes.DRIPSTONE_CAVES, ModBiomes.ANDESITE_CAVE);
		});
	}
}