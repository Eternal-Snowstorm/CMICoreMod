package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

public class MetalCogWheelVisual {
	public static BlockEntityVisual<BracketedKineticBlockEntity> create(
			VisualizationContext context,
			BracketedKineticBlockEntity entity,
			float pt
	) {
		Block block = entity.getBlockState().getBlock();

		MetalCogWheelInfo set = MetalCogWheelRegister.BLOCK_TO_SET.get(block);

		if (set == null) {
			return new SingleAxisRotatingVisual<>(
					context,
					entity,
					pt,
					Models.partial(AllPartialModels.SHAFT)
			);
		}

		String material = set.getMaterial();

		if (ICogWheel.isLargeCog(entity.getBlockState())) {
			return new LargeCogVisual(context, entity, pt, material);
		}

		Model model = Models.partial(
				MetalCogWheelPartial.SMALL.get(material)
		);

		return new SingleAxisRotatingVisual<>(context, entity, pt, model);
	}

	public static class LargeCogVisual extends SingleAxisRotatingVisual<BracketedKineticBlockEntity> {

		private final RotatingInstance additionalShaft;

		private LargeCogVisual(
				VisualizationContext context,
				BracketedKineticBlockEntity entity,
				float pt,
				String material
		) {
			super(context, entity, pt, Models.partial(MetalCogWheelPartial.LARGE.get(material)));
			Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(entity);

			additionalShaft = instancerProvider().instancer(
					AllInstanceTypes.ROTATING,
					Models.partial(AllPartialModels.COGWHEEL_SHAFT)
			).createInstance();

			additionalShaft.rotateToFace(axis)
					.setup(entity)
					.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos))
					.setPosition(getVisualPosition())
					.setChanged();
		}
	}
}