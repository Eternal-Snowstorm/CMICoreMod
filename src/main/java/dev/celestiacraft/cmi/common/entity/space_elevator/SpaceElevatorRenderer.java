package dev.celestiacraft.cmi.common.entity.space_elevator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.celestiacraft.cmi.Cmi;
import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpaceElevatorRenderer extends GeoEntityRenderer<SpaceElevatorEntity> {
	private static final int CABLE_STEPS = 80;
	private static final float CABLE_HALF_WIDTH = 0.045F;
	private static final double MIN_CABLE_RENDER_EXTENT = 2048.0D;
	private static final double CABLE_RENDER_DISTANCE_SCALE = 256.0D;

	private static final RenderType UNLOAD_HINT_RENDER_TYPE = RenderType.entityCutoutNoCull(Cmi.loadResource("textures/icons/exclamation.png"));
	private static final float UNLOAD_HINT_SIZE = 1.0F;
	private static final float UNLOAD_HINT_HEIGHT_ABOVE_TOP = 1.0F;
	private static final float UNLOAD_HINT_BOB_SPEED = 0.15F;
	private static final float UNLOAD_HINT_BOB_AMPLITUDE = 0.12F;

	public SpaceElevatorRenderer(EntityRendererProvider.Context context) {
		super(context, new SpaceElevatiorModel());
	}

	@Override
	public void render(@NotNull SpaceElevatorEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
		if (entity.shouldRenderUnloadHint()) {
			renderUnloadHint(entity, partialTick, poseStack, buffer);
		}
		if (!entity.shouldRenderCables()) {
			return;
		}
		for (int cableIndex = 0; cableIndex < entity.cableCount(); cableIndex++) {
			renderCable(entity, partialTick, poseStack, buffer, cableIndex);
		}
	}

	@Override
	protected void applyRotations(SpaceElevatorEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
		float yRot = Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot());
		super.applyRotations(animatable, poseStack, ageInTicks, yRot, partialTick);
	}

	private void renderUnloadHint(SpaceElevatorEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
		float bob = Mth.sin((entity.tickCount + partialTick) * UNLOAD_HINT_BOB_SPEED) * UNLOAD_HINT_BOB_AMPLITUDE;
		poseStack.pushPose();
		poseStack.translate(0.0F, entity.getBbHeight() + UNLOAD_HINT_HEIGHT_ABOVE_TOP + bob, 0.0F);
		poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		poseStack.scale(UNLOAD_HINT_SIZE, UNLOAD_HINT_SIZE, UNLOAD_HINT_SIZE);
		PoseStack.Pose pose = poseStack.last();
		VertexConsumer consumer = buffer.getBuffer(UNLOAD_HINT_RENDER_TYPE);
		addUnloadHintVertex(consumer, pose, -0.5F, -0.5F, 0.0F, 1.0F);
		addUnloadHintVertex(consumer, pose, 0.5F, -0.5F, 1.0F, 1.0F);
		addUnloadHintVertex(consumer, pose, 0.5F, 0.5F, 1.0F, 0.0F);
		addUnloadHintVertex(consumer, pose, -0.5F, 0.5F, 0.0F, 0.0F);
		poseStack.popPose();
	}

	private static void addUnloadHintVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v) {
		consumer.vertex(pose.pose(), x, y, 0.0F)
				.color(255, 255, 255, 255)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(LightTexture.FULL_BRIGHT)
				.normal(pose.normal(), 0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	private void renderCable(SpaceElevatorEntity entity, float partialTick, PoseStack poseStack,
	                         MultiBufferSource buffer, int cableIndex) {
		Vec3 cameraPos = entityRenderDispatcher.camera.getPosition();
		CableEndpoints endpoints = resolveCableEndpoints(entity, cableIndex, cameraPos);
		Vec3 start = endpoints.start();
		Vec3 end = endpoints.end();
		Vec3 cable = end.subtract(start);
		if (cable.lengthSqr() <= 1.0E-6D) {
			return;
		}

		double entityX = Mth.lerp(partialTick, entity.xo, entity.getX());
		double entityY = Mth.lerp(partialTick, entity.yo, entity.getY());
		double entityZ = Mth.lerp(partialTick, entity.zo, entity.getZ());
		poseStack.pushPose();
		poseStack.translate(start.x - entityX, start.y - entityY, start.z - entityZ);

		VertexConsumer consumer = buffer.getBuffer(RenderType.leash());
		Matrix4f matrix = poseStack.last().pose();
		BlockPos lightPos = entity.getAnchor();
		int blockLight = getBlockLightLevel(entity, lightPos);
		int skyLight = entity.level().getBrightness(LightLayer.SKY, lightPos);
		Vec3 midpoint = start.add(end).scale(0.5D);
		Vec3 direction = cable.normalize();
		Vec3 widthAxis = direction.cross(cameraPos.subtract(midpoint));
		if (widthAxis.lengthSqr() <= 1.0E-6D) {
			widthAxis = direction.cross(new Vec3(0.0D, 1.0D, 0.0D));
		}
		if (widthAxis.lengthSqr() <= 1.0E-6D) {
			widthAxis = direction.cross(new Vec3(1.0D, 0.0D, 0.0D));
		}
		widthAxis = widthAxis.normalize().scale(CABLE_HALF_WIDTH);

		renderCablePlane(consumer, matrix, cable, blockLight, skyLight, widthAxis);
		poseStack.popPose();
	}

	private static CableEndpoints resolveCableEndpoints(SpaceElevatorEntity entity, int cableIndex, Vec3 cameraPos) {
		Vec3 start = entity.getCableStart(cableIndex);
		Vec3 end = entity.getCableEnd(cableIndex);
		double renderExtent = Math.max(
				MIN_CABLE_RENDER_EXTENT,
				Minecraft.getInstance().options.getEffectiveRenderDistance() * CABLE_RENDER_DISTANCE_SCALE
		);
		if (Planet.EARTH_ORBIT.equals(entity.level().dimension())) {
			double farBottomY = Math.min(Math.min(start.y, end.y), cameraPos.y) - renderExtent;
			return new CableEndpoints(new Vec3(start.x, farBottomY, start.z), end);
		}
		double farTopY = Math.max(Math.max(start.y, end.y), cameraPos.y) + renderExtent;
		return new CableEndpoints(start, new Vec3(end.x, farTopY, end.z));
	}

	private static void renderCablePlane(VertexConsumer consumer, Matrix4f matrix, Vec3 cable,
	                                     int blockLight, int skyLight, Vec3 widthAxis) {
		for (int i = 0; i <= CABLE_STEPS; i++) {
			addCableVertexPair(consumer, matrix, cable, blockLight, skyLight, widthAxis,
					CABLE_HALF_WIDTH, CABLE_HALF_WIDTH, i, false);
		}
		for (int i = CABLE_STEPS; i >= 0; i--) {
			addCableVertexPair(consumer, matrix, cable, blockLight, skyLight, widthAxis,
					CABLE_HALF_WIDTH, 0.0F, i, true);
		}
	}

	private static void addCableVertexPair(
			VertexConsumer consumer,
			Matrix4f matrix,
			Vec3 cable,
			int startBlockLight,
			int startSkyLight,
			Vec3 widthAxis,
			float widthA,
			float widthB,
			int index,
			boolean alternate
	) {
		float progress = index / (float) CABLE_STEPS;
		int blockLight = (int) Mth.lerp(progress, startBlockLight, 15);
		int skyLight = (int) Mth.lerp(progress, startSkyLight, 15);
		int packedLight = LightTexture.pack(blockLight, skyLight);
		float shade = index % 2 == (alternate ? 1 : 0) ? 0.7F : 1.0F;
		float x = (float) (cable.x * progress);
		float y = (float) (cable.y * progress);
		float z = (float) (cable.z * progress);
		float wx = (float) widthAxis.x;
		float wy = (float) widthAxis.y;
		float wz = (float) widthAxis.z;
		consumer.vertex(matrix, x - wx, y - wy + widthB, z - wz)
				.color(0.42F * shade, 0.44F * shade, 0.48F * shade, 1.0F)
				.uv2(packedLight)
				.endVertex();
		consumer.vertex(matrix, x + wx, y + wy + widthA - widthB, z + wz)
				.color(0.42F * shade, 0.44F * shade, 0.48F * shade, 1.0F)
				.uv2(packedLight)
				.endVertex();
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull SpaceElevatorEntity entity) {
		return Cmi.loadResource("textures/entity/space_elevator.png");
	}

	private record CableEndpoints(Vec3 start, Vec3 end) {
	}
}