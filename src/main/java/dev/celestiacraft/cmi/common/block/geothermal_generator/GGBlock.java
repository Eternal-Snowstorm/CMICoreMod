package dev.celestiacraft.cmi.common.block.geothermal_generator;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class GGBlock extends BasicBlock implements IBE<GGBlockEntity> {
	public GGBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<GGBlockEntity> getBlockEntityClass() {
		return GGBlockEntity.class;
	}

	@Override
	protected boolean useLitState() {
		return true;
	}

	@Override
	public BlockEntityType<? extends GGBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.GEOTHERMAL_GENERATOR.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						BlockModelProvider models = provider.models();
						boolean working = state.getValue(BasicBlock.LIT);
						String modelPath = working ? "block/geothermal_generator/on" : "block/geothermal_generator/off";
						return ConfiguredModel.builder()
								.modelFile(models.getExistingFile(provider.modLoc(modelPath)))
								.build();
					});
		};
	}
}