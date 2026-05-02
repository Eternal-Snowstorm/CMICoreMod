package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class MetalCogWheelBlock extends CogWheelBlock {
	protected MetalCogWheelBlock(boolean large, Properties properties) {
		super(large, properties.sound(SoundType.LANTERN));
	}

	public static MetalCogWheelBlock small(Properties properties) {
		return new MetalCogWheelBlock(false, properties);
	}

	public static MetalCogWheelBlock large(Properties properties) {
		return new MetalCogWheelBlock(true, properties);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> blockstate(String material, boolean large) {
		return (context, provider) -> {
			String type = large ? "large" : "small";
			BlockModelProvider models = provider.models();
			String path = String.format("block/cogwheel/%s/%s", material, type);

			BlockModelBuilder model = models.withExistingParent(path, models.modLoc("block/cogwheel/basic/" + type))
					.texture("particle",models.modLoc("block/cogwheel/" + material + "/" + type))
					.texture("cogwheel", models.modLoc("block/cogwheel/" + material + "/" + type));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

						return ConfiguredModel.builder()
								.modelFile(model)
								.rotationX(rotationX(axis))
								.rotationY(rotationY(axis))
								.build();
					});
		};
	}

	private static int rotationX(Direction.Axis axis) {
		return switch (axis) {
			case X, Z -> 90;
			case Y -> 0;
		};
	}

	private static int rotationY(Direction.Axis axis) {
		return switch (axis) {
			case X -> 90;
			case Y -> 0;
			case Z -> 180;
		};
	}
}