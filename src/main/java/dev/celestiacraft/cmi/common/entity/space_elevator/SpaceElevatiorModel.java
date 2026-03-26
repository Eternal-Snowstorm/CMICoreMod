package dev.celestiacraft.cmi.common.entity.space_elevator;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpaceElevatiorModel extends GeoModel<SpaceElevatorEntity> {
	@Override
	public ResourceLocation getModelResource(SpaceElevatorEntity animatable) {
		return Cmi.loadResource("geo/entity/space_elevator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpaceElevatorEntity animatable) {
		return Cmi.loadResource("textures/entity/space_elevator.png");
	}

	@Override
	public ResourceLocation getAnimationResource(SpaceElevatorEntity animatable) {
		return Cmi.loadResource("animations/entity/space_elevator.animation.json");
	}
}