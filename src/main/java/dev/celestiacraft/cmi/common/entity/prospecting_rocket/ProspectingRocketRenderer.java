package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ProspectingRocketRenderer extends GeoEntityRenderer<ProspectingRocketEntity> {
	public ProspectingRocketRenderer(EntityRendererProvider.Context context) {
		super(context, new ProspectingRocketModel());
		this.shadowRadius = 0.8F;
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull ProspectingRocketEntity entity) {
		return entity.getTier().textureResource();
	}

	@Override
	protected void applyRotations(ProspectingRocketEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
		float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
		super.applyRotations(entity, poseStack, ageInTicks, yRot, partialTick);
	}
}
