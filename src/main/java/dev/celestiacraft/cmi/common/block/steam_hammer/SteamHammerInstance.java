package dev.celestiacraft.cmi.common.block.steam_hammer;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import org.joml.Quaternionf;
import dev.celestiacraft.cmi.client.block.resource.CmiBlockPartialModel;

public class SteamHammerInstance extends ShaftInstance<MechanicalPressBlockEntity> implements DynamicInstance {
	private final OrientedData pressHead;

	public SteamHammerInstance(MaterialManager materialManager, MechanicalPressBlockEntity blockEntity) {
		super(materialManager, blockEntity);

		pressHead = materialManager.defaultSolid()
				.material(Materials.ORIENTED)
				.getModel(CmiBlockPartialModel.STEAM_HAMMER, blockState)
				.createInstance();

		Quaternionf rotateY = new Quaternionf().rotateY((float) Math.toRadians(
				AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING))
		));

		pressHead.setRotation(rotateY);

		transformModels();
	}

	@Override
	public void beginFrame() {
		transformModels();
	}

	private void transformModels() {
		float renderedHeadOffset = getRenderedHeadOffset(blockEntity);

		pressHead.setPosition(getInstancePosition()).nudge(0, -renderedHeadOffset, 0);
	}

	private float getRenderedHeadOffset(MechanicalPressBlockEntity press) {
		PressingBehaviour pressingBehaviour = press.getPressingBehaviour();
		return pressingBehaviour.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks()) * pressingBehaviour.mode.headOffset;
	}

	@Override
	public void updateLight() {
		super.updateLight();

		relight(pos, pressHead);
	}

	@Override
	public void remove() {
		super.remove();
		pressHead.delete();
	}
}