package dev.celestiacraft.cmi.common.block.solar_boiler;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.libs.api.interaction.IFluidInteractable;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public abstract class SolarBoilerBlock extends BasicBlock implements IBE<SolarBoilerBlockEntity>, IFluidInteractable {
	public SolarBoilerBlock(Properties properties) {
		super(properties.sound(SoundType.LANTERN));
	}

	@Override
	public boolean creativeUseFluidInteraction() {
		return true;
	}

	public static <T extends Block, P> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						String path = String.format("block/solar_boiler/%s", material);
						BlockModelProvider models = provider.models();

						BlockModelBuilder modelFile = models.withExistingParent(path, "block/cube_bottom_top")
								.texture("up", models.modLoc("block/solar_boiler/" + material + "/top"))
								.texture("down", models.modLoc("block/solar_boiler/" + material + "/down"))
								.texture("side", models.modLoc("block/solar_boiler/" + material + "/side"));

						return ConfiguredModel.builder()
								.modelFile(modelFile)
								.build();
					});
		};
	}
}