package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpaceElevatorBaseConsoleItemModel extends GeoModel<SpaceElevatorBaseConsoleBlockItem> {
	private static final ResourceLocation MODEL = Cmi.loadResource("geo/block/space_elevator_base.geo.json");
	private static final ResourceLocation TEXTURE = Cmi.loadResource("textures/block/space_elevator_base.png");
	private static final ResourceLocation ANIMATION = Cmi.loadResource("animations/block/space_elevator_base_console.animation.json");

	@Override
	public ResourceLocation getModelResource(SpaceElevatorBaseConsoleBlockItem animatable) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(SpaceElevatorBaseConsoleBlockItem animatable) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(SpaceElevatorBaseConsoleBlockItem animatable) {
		return ANIMATION;
	}
}
