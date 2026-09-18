package dev.celestiacraft.cmi.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.Objects;

public class SpaceElevatorHudRenderer {
	private static ShaderInstance lineShader;
	private static ShaderInstance panelShader;

	public static void registerShaders(RegisterShadersEvent event) {
		try {
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Cmi.loadResource("space_elevator_line"), DefaultVertexFormat.POSITION_COLOR_TEX), shader -> lineShader = shader);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Cmi.loadResource("space_elevator_panel"), DefaultVertexFormat.POSITION_COLOR_TEX), shader -> panelShader = shader);
		} catch (IOException exception) {
			throw new RuntimeException("Failed to register space elevator HUD shaders", exception);
		}
	}

	public static boolean isAvailable() {
		return lineShader != null && panelShader != null;
	}

	public static void drawSignalLine(PoseStack poseStack, float x1, float y1, float x2, float y2, int color, float thickness) {
		if (lineShader == null) {
			return;
		}

		drawLinePass(poseStack, x1, y1, x2, y2, scaleAlpha(color, 0.12F), thickness + 0.85F, 1.1F);
		drawLinePass(poseStack, x1, y1, x2, y2, color, thickness, 0.55F);
	}

	public static void drawRoundedRect(
			PoseStack poseStack,
			float x,
			float y,
			float width,
			float height,
			float radius,
			float softness,
			int topColor,
			int bottomColor,
			int strokeColor,
			float strokeWidth
	) {
		if (panelShader == null || width <= 0.0F || height <= 0.0F) {
			return;
		}

		ShaderInstance shader = requirePanelShader();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		RenderSystem.setShader(() -> shader);
		shader.safeGetUniform("RectSize").set(width, height);
		shader.safeGetUniform("PanelMetrics").set(radius, Math.max(softness, 0.001F), Math.max(strokeWidth, 0.0F), 0.0F);
		shader.safeGetUniform("StrokeColor").set(colorRed(strokeColor), colorGreen(strokeColor), colorBlue(strokeColor), colorAlpha(strokeColor));

		Matrix4f matrix = poseStack.last().pose();
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		vertex(buffer, matrix, x, y, topColor, 0.0F, 0.0F);
		vertex(buffer, matrix, x, y + height, bottomColor, 0.0F, height);
		vertex(buffer, matrix, x + width, y + height, bottomColor, width, height);
		vertex(buffer, matrix, x + width, y, topColor, width, 0.0F);
		BufferUploader.drawWithShader(buffer.end());

		RenderSystem.enableDepthTest();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}

	public static void drawGlowDot(PoseStack poseStack, float centerX, float centerY, float radius, int color) {
		float diameter = radius * 2.0F;
		drawRoundedRect(poseStack, centerX - radius - 1.2F, centerY - radius - 1.2F, diameter + 2.4F, diameter + 2.4F, radius + 1.2F, 1.2F, scaleAlpha(color, 0.10F), scaleAlpha(color, 0.03F), 0, 0.0F);
		drawRoundedRect(poseStack, centerX - radius, centerY - radius, diameter, diameter, radius, 0.8F, color, color, scaleAlpha(color, 0.50F), 0.75F);
	}

	private static void drawLinePass(PoseStack poseStack, float x1, float y1, float x2, float y2, int color, float thickness, float feather) {
		ShaderInstance shader = requireLineShader();
		float padding = thickness + feather + 2.0F;
		float minX = Math.min(x1, x2) - padding;
		float minY = Math.min(y1, y2) - padding;
		float maxX = Math.max(x1, x2) + padding;
		float maxY = Math.max(y1, y2) + padding;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		RenderSystem.setShader(() -> shader);
		shader.safeGetUniform("LineStart").set(x1, y1);
		shader.safeGetUniform("LineEnd").set(x2, y2);
		shader.safeGetUniform("LineMetrics").set(Math.max(thickness, 0.5F), Math.max(feather, 0.001F), 0.0F, 0.0F);

		Matrix4f matrix = poseStack.last().pose();
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
		vertex(buffer, matrix, minX, minY, color, minX, minY);
		vertex(buffer, matrix, minX, maxY, color, minX, maxY);
		vertex(buffer, matrix, maxX, maxY, color, maxX, maxY);
		vertex(buffer, matrix, maxX, minY, color, maxX, minY);
		BufferUploader.drawWithShader(buffer.end());

		RenderSystem.enableDepthTest();
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}

	private static void vertex(BufferBuilder buffer, Matrix4f matrix, float x, float y, int color, float u, float v) {
		buffer.vertex(matrix, x, y, 0.0F)
				.color(colorRedInt(color), colorGreenInt(color), colorBlueInt(color), colorAlphaInt(color))
				.uv(u, v)
				.endVertex();
	}

	private static ShaderInstance requireLineShader() {
		return Objects.requireNonNull(lineShader, "Space elevator line shader is not loaded");
	}

	private static ShaderInstance requirePanelShader() {
		return Objects.requireNonNull(panelShader, "Space elevator panel shader is not loaded");
	}

	private static int scaleAlpha(int argb, float factor) {
		int alpha = Math.max(0, Math.min(255, Math.round(colorAlpha(argb) * factor * 255.0F)));
		return alpha << 24 | (argb & 0xFFFFFF);
	}

	private static float colorAlpha(int argb) {
		return ((argb >>> 24) & 0xFF) / 255.0F;
	}

	private static float colorRed(int argb) {
		return ((argb >>> 16) & 0xFF) / 255.0F;
	}

	private static float colorGreen(int argb) {
		return ((argb >>> 8) & 0xFF) / 255.0F;
	}

	private static float colorBlue(int argb) {
		return (argb & 0xFF) / 255.0F;
	}

	private static int colorAlphaInt(int argb) {
		return (argb >>> 24) & 0xFF;
	}

	private static int colorRedInt(int argb) {
		return (argb >>> 16) & 0xFF;
	}

	private static int colorGreenInt(int argb) {
		return (argb >>> 8) & 0xFF;
	}

	private static int colorBlueInt(int argb) {
		return argb & 0xFF;
	}
}
