package dev.celestiacraft.cmi.common.block.space_elevator_base_console.render;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpaceElevatorBaseConsoleModel extends GeoModel<SpaceElevatorBaseConsoleBlockEntity> {
	@Override
	public ResourceLocation getModelResource(SpaceElevatorBaseConsoleBlockEntity animatable) {
		return Cmi.loadResource("geo/block/space_elevator_base.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpaceElevatorBaseConsoleBlockEntity animatable) {
		return Cmi.loadResource("textures/block/space_elevator_base.png");
	}

	@Override
	public ResourceLocation getAnimationResource(SpaceElevatorBaseConsoleBlockEntity animatable) {
		return Cmi.loadResource("animations/block/space_elevator_base_console.animation.json");
	}
}