package dev.celestiacraft.cmi.common.entity.qi_month;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class QiMonthRenderer extends GeoEntityRenderer<QiMonthEntity> {
	public QiMonthRenderer(EntityRendererProvider.Context context) {
		super(context, new QiMonthModel());
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull QiMonthEntity entity) {
		return Cmi.loadResource("textures/entity/dev/qi_month.png");
	}

	@Override
	public void render(@NotNull QiMonthEntity entity, float entityYaw, float partialTick, @NotNull PoseStack stack, @NotNull MultiBufferSource source, int packedLight) {
		if (entity.isBaby()) {
			stack.scale(0.4f, 0.4f, 0.4f);
		}

		super.render(entity, entityYaw, partialTick, stack, source, packedLight);
	}
}