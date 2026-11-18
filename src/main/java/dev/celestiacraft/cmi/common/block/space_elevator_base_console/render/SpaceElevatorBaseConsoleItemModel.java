package dev.celestiacraft.cmi.common.block.space_elevator_base_console.render;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpaceElevatorBaseConsoleItemModel extends GeoModel<SpaceElevatorBaseConsoleBlockItem> {
	@Override
	public ResourceLocation getModelResource(SpaceElevatorBaseConsoleBlockItem animatable) {
		return Cmi.loadResource("geo/block/space_elevator_base.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpaceElevatorBaseConsoleBlockItem animatable) {
		return Cmi.loadResource("textures/block/space_elevator_base.png");
	}

	@Override
	public ResourceLocation getAnimationResource(SpaceElevatorBaseConsoleBlockItem animatable) {
		return Cmi.loadResource("animations/block/space_elevator_base_console.animation.json");
	}
}