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
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class MetalCogWheelVisual {
	public static BlockEntityVisual<BracketedKineticBlockEntity> create(
			VisualizationContext context,
			BracketedKineticBlockEntity entity,
			float pt
	) {
		BlockState state = entity.getBlockState();
		MetalCogWheelInfo info = MetalCogWheelRegister.BLOCK_TO_SET.get(state.getBlock());
		boolean large = ICogWheel.isLargeCog(state);

		PartialModel partial = null;

		if (info != null) {
			String material = info.getMaterial();

			// 大齿轮用不带轴的模型, 轴由 LargeCogVisual 单独补一根(和 Create 一致)
			partial = large
					? MetalCogWheelPartial.LARGE.get(material)
					: MetalCogWheelPartial.SMALL_WITH_SHAFT.get(material);
		}

		// 模型没有烘焙出来时不能直接丢给 Flywheel, 否则会在创建 visual 阶段抛异常,
		// 表现就是齿轮整个消失, 这里退回 Create 的原版模型保证看得见
		if (partial == null || partial.get() == null) {
			return createFallback(context, entity, pt, large);
		}

		if (large) {
			return new LargeCogVisual(context, entity, pt, Models.partial(partial));
		}

		return new SingleAxisRotatingVisual<>(context, entity, pt, Models.partial(partial));
	}

	private static BlockEntityVisual<BracketedKineticBlockEntity> createFallback(
			VisualizationContext context,
			BracketedKineticBlockEntity entity,
			float pt,
			boolean large
	) {
		if (large) {
			// LargeCogVisual 内部会额外补一根 COGWHEEL_SHAFT
			return new LargeCogVisual(context, entity, pt, Models.partial(AllPartialModels.SHAFTLESS_LARGE_COGWHEEL));
		}

		return new SingleAxisRotatingVisual<>(context, entity, pt, Models.partial(AllPartialModels.COGWHEEL));
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
		public void collectCrumblingInstances(Consumer<Instance> consumer) {
			super.collectCrumblingInstances(consumer);
			consumer.accept(additionalShaft);
		}
	}
}