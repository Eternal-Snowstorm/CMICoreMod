package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
			String mainPath = String.format("block/cogwheel/%s/%s", material, type);
			String partialPath = String.format("block/cogwheel/%s/%s_gear", material, type);

			BlockModelBuilder main = models.withExistingParent(mainPath, models.modLoc("block/cogwheel/basic/" + type))
					.texture("particle", models.modLoc("block/cogwheel/" + material + "/" + type))
					.texture("cogwheel", models.modLoc("block/cogwheel/" + material + "/" + type));

			BlockModelBuilder partial = models.withExistingParent(partialPath, models.modLoc("block/cogwheel/basic/" + type + "_gear"))
					.texture("particle", models.modLoc("block/cogwheel/" + material + "/" + type))
					.texture("cogwheel", models.modLoc("block/cogwheel/" + material + "/" + type));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

						return ConfiguredModel.builder()
								.modelFile(main)
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

	@Override
	public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.registerCogWheel().get();
	}
}