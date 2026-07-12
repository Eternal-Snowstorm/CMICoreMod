package dev.celestiacraft.cmi.common.block.fluid_burner;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public abstract class FluidBurnerBlock extends BasicBlock implements IBE<FluidBurnerBlockEntity> {
	public FluidBurnerBlock(Properties properties) {
		super(properties.sound(SoundType.LANTERN)
				.lightLevel(litBlockEmission(15))
		);
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.HORIZONTAL;
	}

	@Override
	protected boolean useLitState() {
		return true;
	}

	@Override
	public boolean creativeUseFluidInteraction() {
		return true;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						boolean lit = state.getValue(LIT);
						Direction facing = state.getValue(HORIZONTAL_FACING);
						String stateName = lit ? "on" : "off";
						String path = String.format("block/fluid_burner/%s/%s", material, stateName);
						BlockModelProvider models = provider.models();

						BlockModelBuilder modelFile = models.withExistingParent(path, "block/orientable").texture("top", models.modLoc("block/fluid_burner/%s/top".formatted(material)))
								.texture("bottom", models.modLoc("block/fluid_burner/%s/down".formatted(material)))
								.texture("side", models.modLoc("block/fluid_burner/%s/side".formatted(material)))
								.texture("front", models.modLoc("block/fluid_burner/%s/%s".formatted(material, (lit ? "on" : "off"))));

						return ConfiguredModel.builder()
								.modelFile(modelFile)
								.rotationY(BasicBlock.getYRotFromFacing(facing))
								.build();
					});
		};
	}
}