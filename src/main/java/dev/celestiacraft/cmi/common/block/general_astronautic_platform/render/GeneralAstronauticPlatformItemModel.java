package dev.celestiacraft.cmi.common.block.general_astronautic_platform.render;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GeneralAstronauticPlatformItemModel extends GeoModel<GeneralAstronauticPlatformBlockItem> {
	@Override
	public ResourceLocation getModelResource(GeneralAstronauticPlatformBlockItem animatable) {
		return Cmi.loadResource("geo/block/general_astronautic_platform.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeneralAstronauticPlatformBlockItem animatable) {
		return Cmi.loadResource("textures/block/space_elevator_base.png");
	}

	@Override
	public ResourceLocation getAnimationResource(GeneralAstronauticPlatformBlockItem animatable) {
		return Cmi.loadResource("animations/block/space_elevator_base_console.animation.json");
	}
}
