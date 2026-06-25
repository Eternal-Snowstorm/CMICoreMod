package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ProspectingRocketModel extends GeoModel<ProspectingRocketEntity> {
	@Override
	public RenderType getRenderType(ProspectingRocketEntity animatable, ResourceLocation texture) {
		return RenderType.entityCutoutNoCullZOffset(texture);
	}

	@Override
	public ResourceLocation getModelResource(ProspectingRocketEntity animatable) {
		return animatable.getTier().modelResource();
	}

	@Override
	public ResourceLocation getTextureResource(ProspectingRocketEntity animatable) {
		return animatable.getTier().textureResource();
	}

	@Override
	public ResourceLocation getAnimationResource(ProspectingRocketEntity animatable) {
		return animatable.getTier().animationResource();
	}
}
