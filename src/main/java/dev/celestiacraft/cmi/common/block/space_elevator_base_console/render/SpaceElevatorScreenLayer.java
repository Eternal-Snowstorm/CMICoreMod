package dev.celestiacraft.cmi.common.block.space_elevator_base_console.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.render.SpaceElevatorConsoleScreenState;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorConsoleDisplayState;
import dev.celestiacraft.cmi.event.SpaceElevatorWrenchClientHandler;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SpaceElevatorScreenLayer extends GeoRenderLayer<SpaceElevatorBaseConsoleBlockEntity> {
	private static final ResourceLocation BASE_TEXTURE = Cmi.loadResource("textures/block/space_elevator_base.png");
	private static final String TARGET_BONE = "displays";

	private static final float TEXTURE_WIDTH = 512.0F;
	private static final float TEXTURE_HEIGHT = 512.0F;

	private static final TextureRegion CONSTRUCTING = new TextureRegion(428.0F, 472.0F, 456.0F, 492.0F);
	private static final TextureRegion COUNTDOWN = new TextureRegion(428.0F, 492.0F, 456.0F, 512.0F);
	private static final TextureRegion ASCENDING = new TextureRegion(428.0F, 452.0F, 456.0F, 472.0F);
	private static final TextureRegion APPROACHING_STATION = new TextureRegion(428.0F, 432.0F, 456.0F, 452.0F);
	private static final TextureRegion DOCKED = new TextureRegion(428.0F, 412.0F, 456.0F, 432.0F);
	private static final TextureRegion CMI = new TextureRegion(456.0F, 492.0F, 484.0F, 512.0F);
	private static final TextureRegion NO_ELEVATOR = new TextureRegion(456.0F, 472.0F, 484.0F, 492.0F);
	private static final TextureRegion TRANSFER_ACTIVE = new TextureRegion(484.0F, 492.0F, 512.0F, 512.0F);
	private static final TextureRegion TRANSFER_FINISHED = new TextureRegion(484.0F, 472.0F, 512.0F, 492.0F);

	private static final float LEFT_MONITOR_X_MIN = -22.0F / 16.0F;
	private static final float LEFT_MONITOR_X_MAX = -8.0F / 16.0F;
	private static final float CENTER_MONITOR_X_MIN = -7.0F / 16.0F;
	private static final float CENTER_MONITOR_X_MAX = 7.0F / 16.0F;
	private static final float RIGHT_MONITOR_X_MIN = 8.0F / 16.0F;
	private static final float RIGHT_MONITOR_X_MAX = 22.0F / 16.0F;
	private static final float MONITOR_Y_MIN = 17.0F / 16.0F;
	private static final float MONITOR_Y_MAX = 27.0F / 16.0F;
	private static final float MONITOR_Z = -39.0F / 16.0F - 1.0F / 256.0F;

	public SpaceElevatorScreenLayer(GeoRenderer<SpaceElevatorBaseConsoleBlockEntity> renderer) {
		super(renderer);
	}

	@Override
	public void renderForBone(PoseStack poseStack, SpaceElevatorBaseConsoleBlockEntity animatable, GeoBone bone,
							  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
							  float partialTick, int packedLight, int packedOverlay) {
		if (!TARGET_BONE.equals(bone.getName())) {
			return;
		}

		VertexConsumer screenBuffer = bufferSource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(BASE_TEXTURE, false));
		Matrix4f pose = poseStack.last().pose();

		if (SpaceElevatorWrenchClientHandler.isCharging(animatable.getBlockPos())
				|| SpaceElevatorConsoleScreenState.isConstructionActive(animatable.getBlockPos())) {
			addToAllMonitors(screenBuffer, pose, CONSTRUCTING, packedOverlay);
			return;
		}

		if (!animatable.isElevatorPresent()) {
			addToAllMonitors(screenBuffer, pose, NO_ELEVATOR, packedOverlay);
			return;
		}

		addQuad(screenBuffer, pose, LEFT_MONITOR_X_MIN, LEFT_MONITOR_X_MAX, getElevatorStateRegion(animatable.getElevatorDisplayState()), packedOverlay);
		addQuad(screenBuffer, pose, CENTER_MONITOR_X_MIN, CENTER_MONITOR_X_MAX, CMI, packedOverlay);
		if (SpaceElevatorConsoleScreenState.isTransferActive(animatable.getBlockPos())) {
			addQuad(screenBuffer, pose, RIGHT_MONITOR_X_MIN, RIGHT_MONITOR_X_MAX, TRANSFER_ACTIVE, packedOverlay);
		} else if (SpaceElevatorConsoleScreenState.isTransferFinished(animatable.getBlockPos())) {
			addQuad(screenBuffer, pose, RIGHT_MONITOR_X_MIN, RIGHT_MONITOR_X_MAX, TRANSFER_FINISHED, packedOverlay);
		} else {
			addQuad(screenBuffer, pose, RIGHT_MONITOR_X_MIN, RIGHT_MONITOR_X_MAX, CMI, packedOverlay);
		}
	}

	private static TextureRegion getElevatorStateRegion(SpaceElevatorConsoleDisplayState state) {
		return switch (state) {
			case COUNTDOWN -> COUNTDOWN;
			case ASCENDING -> ASCENDING;
			case APPROACHING_STATION -> APPROACHING_STATION;
			case DOCKED -> DOCKED;
			case READY -> CMI;
		};
	}

	private static void addToAllMonitors(VertexConsumer buffer, Matrix4f pose, TextureRegion region, int packedOverlay) {
		addQuad(buffer, pose, LEFT_MONITOR_X_MIN, LEFT_MONITOR_X_MAX, region, packedOverlay);
		addQuad(buffer, pose, CENTER_MONITOR_X_MIN, CENTER_MONITOR_X_MAX, region, packedOverlay);
		addQuad(buffer, pose, RIGHT_MONITOR_X_MIN, RIGHT_MONITOR_X_MAX, region, packedOverlay);
	}

	private static void addQuad(VertexConsumer buffer, Matrix4f pose, float xMin, float xMax, TextureRegion region, int packedOverlay) {
		addVertex(buffer, pose, xMin, MONITOR_Y_MAX, MONITOR_Z, region.uMax(), region.vMin(), LightTexture.FULL_BRIGHT, packedOverlay);
		addVertex(buffer, pose, xMax, MONITOR_Y_MAX, MONITOR_Z, region.uMin(), region.vMin(), LightTexture.FULL_BRIGHT, packedOverlay);
		addVertex(buffer, pose, xMax, MONITOR_Y_MIN, MONITOR_Z, region.uMin(), region.vMax(), LightTexture.FULL_BRIGHT, packedOverlay);
		addVertex(buffer, pose, xMin, MONITOR_Y_MIN, MONITOR_Z, region.uMax(), region.vMax(), LightTexture.FULL_BRIGHT, packedOverlay);
	}

	private static void addVertex(VertexConsumer buffer, Matrix4f pose, float x, float y, float z, float u, float v, int packedLight, int packedOverlay) {
		buffer.vertex(pose, x, y, z)
				.color(1.0F, 1.0F, 1.0F, 1.0F)
				.uv(u, v)
				.overlayCoords(packedOverlay)
				.uv2(packedLight)
				.normal(0.0F, 0.0F, -1.0F)
				.endVertex();
	}

	private record TextureRegion(float uMin, float vMin, float uMax, float vMax) {
		private TextureRegion {
			uMin /= TEXTURE_WIDTH;
			uMax /= TEXTURE_WIDTH;
			vMin /= TEXTURE_HEIGHT;
			vMax /= TEXTURE_HEIGHT;
		}
	}
}
