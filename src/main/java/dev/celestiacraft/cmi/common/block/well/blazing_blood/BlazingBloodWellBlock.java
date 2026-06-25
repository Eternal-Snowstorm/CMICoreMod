package dev.celestiacraft.cmi.common.block.well.blazing_blood;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class BlazingBloodWellBlock extends ControllerBlock<BlazingBloodWellBlockEntity> {
	public BlazingBloodWellBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<BlazingBloodWellBlockEntity> getBlockEntityClass() {
		return BlazingBloodWellBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends BlazingBloodWellBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.BLAZING_BLOOD_WELL.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						BlockModelProvider models = provider.models();
						return ConfiguredModel.builder()
								.modelFile(models.getExistingFile(provider.modLoc("block/well/blazing_blood")))
								.build();
					});
		};
	}
}