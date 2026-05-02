package dev.celestiacraft.cmi.common.block.steam_hammer;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import dev.celestiacraft.cmi.client.block.CmiBlockPartialModel;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import org.joml.Quaternionf;

public class SteamHammerInstance extends ShaftVisual<SteamHammerBlockEntity> implements SimpleDynamicVisual {
	private final OrientedInstance pressHead;

	public SteamHammerInstance(VisualizationContext context, SteamHammerBlockEntity entity, float partialTick) {
		super(context, entity, partialTick);

		pressHead = instancerProvider().instancer(
				InstanceTypes.ORIENTED,
				Models.partial(CmiBlockPartialModel.STEAM_HAMMER)
		).createInstance();

		Quaternionf rotateY = new Quaternionf().rotateY((float) Math.toRadians(
				AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING))
		));

		pressHead.rotation(rotateY);

		transformModels(partialTick);
	}

	@Override
	public void beginFrame(DynamicVisual.Context context) {
		transformModels(context.partialTick());
	}

	private void transformModels(float partialTick) {
		float renderedHeadOffset = getRenderedHeadOffset(partialTick);
		pressHead.position(getVisualPosition())
				.translatePosition(0, -renderedHeadOffset, 0)
				.setChanged();
	}

	private float getRenderedHeadOffset(float partialTick) {
		PressingBehaviour pressingBehaviour = blockEntity.pressingBehaviour;
		return pressingBehaviour.getRenderedHeadOffset(partialTick) * pressingBehaviour.mode.headOffset;
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		relight(pressHead);
	}

	@Override
	protected void _delete() {
		super._delete();
		pressHead.delete();
	}
}