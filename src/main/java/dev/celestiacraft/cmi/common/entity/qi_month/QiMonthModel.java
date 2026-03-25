package dev.celestiacraft.cmi.common.entity.qi_month;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class QiMonthModel extends GeoModel<QiMonthEntity> {
	@Override
	public ResourceLocation getModelResource(QiMonthEntity entity) {
		return Cmi.loadResource("geo/qi_month.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(QiMonthEntity entity) {
		return Cmi.loadResource("textures/entity/dev/qi_month.png");
	}

	@Override
	public ResourceLocation getAnimationResource(QiMonthEntity entity) {
		return Cmi.loadResource("animations/qi_month.animation.json");
	}
}