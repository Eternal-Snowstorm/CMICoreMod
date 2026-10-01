package dev.celestiacraft.cmi.common.block.space_elevator_top;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SpaceElevatorTopRenderer extends GeoBlockRenderer<SpaceElevatorTopBlockEntity> {
	private static final float MODEL_TOP_Y = 185.0F;
	private static final float MODEL_OFFSET_Y = -(MODEL_TOP_Y / 16.0F - 1.0F);

	public SpaceElevatorTopRenderer() {
		super(new SpaceElevatorTopModel());
	}

	@Override
	protected Direction getFacing(SpaceElevatorTopBlockEntity block) {
		return Direction.SOUTH;
	}

	@Override
	public void preRender(PoseStack stack, SpaceElevatorTopBlockEntity animatable, BakedGeoModel model, MultiBufferSource source, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		stack.translate(0.0F, MODEL_OFFSET_Y, 0.0F);
		super.preRender(stack, animatable, model, source, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}
}