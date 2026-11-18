package dev.celestiacraft.cmi.common.block.general_astronautic_platform.render;

import java.util.EnumMap;
import java.util.Map;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.AstronauticPlatformDisplayState;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

// 在 displays 骨骼的三块显示器外壳上叠加状态图标
@OnlyIn(Dist.CLIENT)
public class GeneralAstronauticPlatformScreenLayer extends GeoRenderLayer<GeneralAstronauticPlatformBlockEntity> {
	private static final String TARGET_BONE = "displays";
	private static final String TEXTURE_PATH = "textures/block/astronautic_platform_screens/%s.png";

	// 三块屏幕薄片为 12x6, 图标矩形在此范围内绘制
	private static final float LEFT_X_MIN = -21.0F / 16.0F;
	private static final float LEFT_X_MAX = -9.0F / 16.0F;
	private static final float CENTER_X_MIN = -6.0F / 16.0F;
	private static final float CENTER_X_MAX = 6.0F / 16.0F;
	private static final float RIGHT_X_MIN = 9.0F / 16.0F;
	private static final float RIGHT_X_MAX = 21.0F / 16.0F;
	private static final float SCREEN_Y_MIN = 18.0F / 16.0F;
	private static final float SCREEN_Y_MAX = 26.0F / 16.0F;
	private static final float NORTH_Z = -39.0F / 16.0F - 1.0F / 256.0F;

	private static final Map<AstronauticPlatformDisplayState, RenderType> RENDER_TYPES = new EnumMap<>(AstronauticPlatformDisplayState.class);

	static {
		for (AstronauticPlatformDisplayState state : AstronauticPlatformDisplayState.values()) {
			String textureName = state.getTextureName();
			if (textureName == null) {
				continue;
			}
			ResourceLocation texture = Cmi.loadResource(TEXTURE_PATH.formatted(textureName));
			RENDER_TYPES.put(state, ForgeRenderTypes.getUnlitTranslucent(texture, false));
		}
	}

	public GeneralAstronauticPlatformScreenLayer(GeoRenderer<GeneralAstronauticPlatformBlockEntity> renderer) {
		super(renderer);
	}

	@Override
	public void renderForBone(PoseStack poseStack, GeneralAstronauticPlatformBlockEntity animatable, GeoBone bone,
							  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
							  float partialTick, int packedLight, int packedOverlay) {
		if (!TARGET_BONE.equals(bone.getName())) {
			return;
		}
		RenderType screenType = RENDER_TYPES.get(animatable.getDisplayState());
		if (screenType == null) {
			return;
		}
		VertexConsumer screenBuffer = bufferSource.getBuffer(screenType);
		Matrix4f pose = poseStack.last().pose();
		addToScreen(screenBuffer, pose, LEFT_X_MIN, LEFT_X_MAX, packedOverlay);
		addToScreen(screenBuffer, pose, CENTER_X_MIN, CENTER_X_MAX, packedOverlay);
		addToScreen(screenBuffer, pose, RIGHT_X_MIN, RIGHT_X_MAX, packedOverlay);
	}

	// 只画外壳北面(背面), 南面会被前方屏幕薄片遮挡
	private static void addToScreen(VertexConsumer buffer, Matrix4f pose, float xMin, float xMax, int packedOverlay) {
		addQuad(buffer, pose, xMin, xMax, NORTH_Z, -1.0F, packedOverlay);
	}

	private static void addQuad(VertexConsumer buffer, Matrix4f pose, float xMin, float xMax, float z, float normalZ, int packedOverlay) {
		addVertex(buffer, pose, xMin, SCREEN_Y_MAX, z, 0.0F, 0.0F, normalZ, packedOverlay);
		addVertex(buffer, pose, xMax, SCREEN_Y_MAX, z, 1.0F, 0.0F, normalZ, packedOverlay);
		addVertex(buffer, pose, xMax, SCREEN_Y_MIN, z, 1.0F, 1.0F, normalZ, packedOverlay);
		addVertex(buffer, pose, xMin, SCREEN_Y_MIN, z, 0.0F, 1.0F, normalZ, packedOverlay);
	}

	private static void addVertex(VertexConsumer buffer, Matrix4f pose, float x, float y, float z, float u, float v, float normalZ, int packedOverlay) {
		buffer.vertex(pose, x, y, z)
				.color(1.0F, 1.0F, 1.0F, 1.0F)
				.uv(u, v)
				.overlayCoords(packedOverlay)
				.uv2(LightTexture.FULL_BRIGHT)
				.normal(0.0F, 0.0F, normalZ)
				.endVertex();
	}
}
