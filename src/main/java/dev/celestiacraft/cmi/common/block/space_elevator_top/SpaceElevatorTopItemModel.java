package dev.celestiacraft.cmi.common.block.space_elevator_top;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpaceElevatorTopItemModel extends GeoModel<SpaceElevatorTopBlockItem> {
	@Override
	public ResourceLocation getModelResource(SpaceElevatorTopBlockItem animatable) {
		return Cmi.loadResource("geo/block/space_elevator_top.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpaceElevatorTopBlockItem animatable) {
		return Cmi.loadResource("textures/block/space_elevator_top.png");
	}

	@Override
	public ResourceLocation getAnimationResource(SpaceElevatorTopBlockItem animatable) {
		return Cmi.loadResource("animations/block/space_elevator_top.animation.json");
	}
}