package dev.celestiacraft.cmi.common.block.wind_vane;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class WindVaneBlock extends BasicBlock implements IBE<WindVaneBlockEntity> {
	public WindVaneBlock(Properties properties) {
		super(Properties.copy(Blocks.COPPER_BLOCK)
				.strength(3.0F, 6.0F)
				.noOcclusion()
		);
	}

	@Override
	public Class<WindVaneBlockEntity> getBlockEntityClass() {
		return WindVaneBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends WindVaneBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.WIND_VANE.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						BlockModelProvider models = provider.models();
						return ConfiguredModel.builder()
								.modelFile(models.getExistingFile(provider.modLoc("block/wind_vane")))
								.build();
					});
		};
	}
}