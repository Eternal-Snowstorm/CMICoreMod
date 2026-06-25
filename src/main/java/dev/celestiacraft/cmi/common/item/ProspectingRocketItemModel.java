package dev.celestiacraft.cmi.common.item;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ProspectingRocketItemModel extends GeoModel<ProspectingRocketItem> {
	@Override
	public RenderType getRenderType(ProspectingRocketItem item, ResourceLocation texture) {
		return RenderType.entityCutoutNoCullZOffset(texture);
	}

	@Override
	public ResourceLocation getModelResource(ProspectingRocketItem item) {
		return item.getTier().modelResource();
	}

	@Override
	public ResourceLocation getTextureResource(ProspectingRocketItem item) {
		return item.getTier().textureResource();
	}

	@Override
	public ResourceLocation getAnimationResource(ProspectingRocketItem item) {
		return item.getTier().animationResource();
	}
}
