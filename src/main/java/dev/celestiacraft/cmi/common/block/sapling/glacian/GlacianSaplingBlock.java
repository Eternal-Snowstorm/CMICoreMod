package dev.celestiacraft.cmi.common.block.sapling.glacian;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.datagen.worldgen.tree.GlacianTreeGrower;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class GlacianSaplingBlock extends SaplingBlock {
	public GlacianSaplingBlock(Properties properties) {
		super(new GlacianTreeGrower(), properties
				.mapColor(MapColor.PLANT)
				.noCollission()
				.randomTicks()
				.instabreak()
				.sound(SoundType.GRASS)
				.pushReaction(PushReaction.DESTROY));
	}

	public static <T extends Block, P> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						BlockModelProvider models = provider.models();
						BlockModelBuilder model = models.cross(context.getName(), provider.modLoc("block/sapling/glacian"))
								.renderType("minecraft:cutout");

						return ConfiguredModel.builder()
								.modelFile(model)
								.build();
					});
		};
	}
}