package dev.celestiacraft.cmi.common.entity.space_elevator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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

	public SpaceElevatorRenderer(EntityRendererProvider.Context context) {
		super(context, new SpaceElevatiorModel());
	}

	@Override
	public void render(SpaceElevatorEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
		if (!entity.shouldRenderCables()) {
			return;
		}

		for (int i = 0; i < entity.cableCount(); i++) {
			renderCable(entity, partialTick, poseStack, buffer, i);
		}
	}

	private void renderCable(SpaceElevatorEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int cableIndex) {
		Vec3 start = entity.getCableStart(cableIndex, partialTick);
		Vec3 end = entity.getCableEnd(cableIndex);
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

		BlockPos startPos = BlockPos.containing(start);
		int startBlockLight = getBlockLightLevel(entity, startPos);
		int startSkyLight = entity.level().getBrightness(LightLayer.SKY, startPos);
		Vec3 midpoint = start.add(end).scale(0.5D);
		Vec3 cameraPos = entityRenderDispatcher.camera.getPosition();
		Vec3 view = cameraPos.subtract(midpoint);
		Vec3 direction = cable.normalize();
		Vec3 widthAxis = direction.cross(view);
		if (widthAxis.lengthSqr() <= 1.0E-6D) {
			widthAxis = direction.cross(new Vec3(0.0D, 1.0D, 0.0D));
		}
		if (widthAxis.lengthSqr() <= 1.0E-6D) {
			widthAxis = direction.cross(new Vec3(1.0D, 0.0D, 0.0D));
		}
		widthAxis = widthAxis.normalize().scale(CABLE_HALF_WIDTH);

		renderCablePlane(consumer, matrix, cable, startBlockLight, startSkyLight, widthAxis);
		poseStack.popPose();
	}

	private static void renderCablePlane(
			VertexConsumer consumer,
			Matrix4f matrix,
			Vec3 cable,
			int startBlockLight,
			int startSkyLight,
			Vec3 widthAxis
	) {
		for (int i = 0; i <= CABLE_STEPS; ++i) {
			addCableVertexPair(consumer, matrix, cable, startBlockLight, startSkyLight, widthAxis, CABLE_HALF_WIDTH, CABLE_HALF_WIDTH, i, false);
		}
		for (int i = CABLE_STEPS; i >= 0; --i) {
			addCableVertexPair(consumer, matrix, cable, startBlockLight, startSkyLight, widthAxis, CABLE_HALF_WIDTH, 0.0F, i, true);
		}
	}

	private static void addCableVertexPair(
			VertexConsumer consumer, Matrix4f matrix, Vec3 cable,
			int startBlockLight, int startSkyLight,
			Vec3 widthAxis,
			float widthA, float widthB, int index, boolean alternate) {
		float progress = (float) index / (float) CABLE_STEPS;
		int blockLight = (int) Mth.lerp(progress, startBlockLight, 15);
		int skyLight = (int) Mth.lerp(progress, startSkyLight, 15);
		int packedLight = LightTexture.pack(blockLight, skyLight);

		float shade = index % 2 == (alternate ? 1 : 0) ? 0.7F : 1.0F;
		float r = 0.42F * shade;
		float g = 0.44F * shade;
		float b = 0.48F * shade;

		float x = (float) (cable.x * progress);
		float y = (float) (cable.y * progress);
		float z = (float) (cable.z * progress);
		float wx = (float) widthAxis.x;
		float wy = (float) widthAxis.y;
		float wz = (float) widthAxis.z;
		consumer.vertex(matrix, x - wx, y - wy + widthB, z - wz).color(r, g, b, 1.0F).uv2(packedLight).endVertex();
		consumer.vertex(matrix, x + wx, y + wy + widthA - widthB, z + wz).color(r, g, b, 1.0F).uv2(packedLight).endVertex();
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull SpaceElevatorEntity entity) {
		return Cmi.loadResource("textures/entity/space_elevator.png");
	}
}
