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
		MetalCogWheelInfo info = MetalCogWheelRegister.BLOCK_TO_SET.get(block);

		if (info == null) {
			return new SingleAxisRotatingVisual<>(
					context,
					entity,
					pt,
					Models.partial(AllPartialModels.SHAFT)
			);
		}

		String material = info.getMaterial();
		boolean large = ICogWheel.isLargeCog(entity.getBlockState());

		boolean hasShaft = true;

		Model model;

		if (large) {
			model = Models.partial(hasShaft
					? MetalCogWheelPartial.LARGE_WITH_SHAFT.get(material)
					: MetalCogWheelPartial.LARGE.get(material)
			);
			return new LargeCogVisual(context, entity, pt, model);
		}

		model = Models.partial(hasShaft
				? MetalCogWheelPartial.SMALL_WITH_SHAFT.get(material)
				: MetalCogWheelPartial.SMALL.get(material)
		);

		return new SingleAxisRotatingVisual<>(context, entity, pt, model);
	}

	public static class LargeCogVisual extends SingleAxisRotatingVisual<BracketedKineticBlockEntity> {
		private final RotatingInstance additionalShaft;

		public LargeCogVisual(
				VisualizationContext context,
				BracketedKineticBlockEntity entity,
				float pt,
				Model model
		) {
			super(context, entity, pt, model);

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

		@Override
		public void update(float pt) {
			super.update(pt);
			additionalShaft.setup(blockEntity)
					.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(rotationAxis(), pos))
					.setChanged();
		}

		@Override
		public void updateLight(float partialTick) {
			super.updateLight(partialTick);
			relight(additionalShaft);
		}

		@Override
		protected void _delete() {
			super._delete();
			additionalShaft.delete();
		}

		@Override
		public void collectCrumblingInstances(java.util.function.Consumer<dev.engine_room.flywheel.api.instance.Instance> consumer) {
			super.collectCrumblingInstances(consumer);
			consumer.accept(additionalShaft);
		}
	}
}