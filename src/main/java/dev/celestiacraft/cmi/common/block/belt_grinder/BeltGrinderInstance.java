package dev.celestiacraft.cmi.common.block.belt_grinder;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.saw.SawVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Consumer;

public class BeltGrinderInstance extends KineticBlockEntityVisual<BeltGrinderBlockEntity> {
	protected final RotatingInstance rotatingModel;

	public BeltGrinderInstance(VisualizationContext context, BeltGrinderBlockEntity entity, float partialTick) {
		super(context, entity, partialTick);
		rotatingModel = SawVisual.shaft(instancerProvider(), blockState)
				.setup(blockEntity)
				.setPosition(getVisualPosition());
		rotatingModel.setChanged();
	}

	public static RotatingInstance shaft(InstancerProvider provider, BlockState state) {
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		Direction.Axis axis = facing.getAxis();
		if (axis.isHorizontal()) {
			Direction align = facing.getOpposite();
			return provider.instancer(
					AllInstanceTypes.ROTATING,
					Models.partial(AllPartialModels.SHAFT_HALF)
			).createInstance().rotateTo(
					0,
					0,
					1,
					align.getStepX(),
					align.getStepY(),
					align.getStepZ()
			);
		} else {
			return provider.instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT))
					.createInstance()
					.rotateToFace(state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) ? Direction.Axis.X : Direction.Axis.Z);
		}
	}

	@Override
	public void update(float pt) {
		rotatingModel.setup(blockEntity)
				.setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		relight(rotatingModel);
	}

	@Override
	protected void _delete() {
		rotatingModel.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(rotatingModel);
	}
}