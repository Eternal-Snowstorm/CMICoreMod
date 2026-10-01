package dev.celestiacraft.cmi.common.block.space_elevator_top;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpaceElevatorTopModel extends GeoModel<SpaceElevatorTopBlockEntity> {
	@Override
	public ResourceLocation getModelResource(SpaceElevatorTopBlockEntity animatable) {
		return Cmi.loadResource("geo/block/space_elevator_top.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpaceElevatorTopBlockEntity animatable) {
		return Cmi.loadResource("textures/block/space_elevator_top.png");
	}

	@Override
	public ResourceLocation getAnimationResource(SpaceElevatorTopBlockEntity animatable) {
		return Cmi.loadResource("animations/block/space_elevator_top.animation.json");
	}
}