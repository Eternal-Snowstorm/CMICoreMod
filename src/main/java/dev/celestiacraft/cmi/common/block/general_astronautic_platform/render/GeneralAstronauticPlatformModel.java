package dev.celestiacraft.cmi.common.block.general_astronautic_platform.render;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GeneralAstronauticPlatformModel extends GeoModel<GeneralAstronauticPlatformBlockEntity> {
	@Override
	public ResourceLocation getModelResource(GeneralAstronauticPlatformBlockEntity animatable) {
		return Cmi.loadResource("geo/block/general_astronautic_platform.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeneralAstronauticPlatformBlockEntity animatable) {
		return Cmi.loadResource("textures/block/space_elevator_base.png");
	}

	@Override
	public ResourceLocation getAnimationResource(GeneralAstronauticPlatformBlockEntity animatable) {
		return Cmi.loadResource("animations/block/space_elevator_base_console.animation.json");
	}
}
