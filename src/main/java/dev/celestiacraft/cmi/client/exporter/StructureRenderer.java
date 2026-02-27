package dev.celestiacraft.cmi.client.exporter;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import dev.celestiacraft.cmi.Cmi;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class StructureRenderer {
    private final StructureScene scene;
    private final VirtualBlockLevel virtualLevel;

    public StructureRenderer(StructureScene scene, net.minecraft.world.level.Level realLevel) {
        this.scene = scene;
        this.virtualLevel = new VirtualBlockLevel(realLevel, scene.getBlocks(), scene.getBlockEntityNbt());
        this.virtualLevel.initAllBlockEntities();
        this.virtualLevel.updateNeighborStates();
    }

    public void renderPreview(PoseStack guiPose, float rotX, float rotY, float zoom,
                              float panX, float panY, int screenWidth, int screenHeight) {
        float dim = scene.getMaxDimension();
        if (dim < 1f) dim = 1f;
        float orthoRange = dim / zoom * 0.7f;
        Minecraft mc = Minecraft.getInstance();
        float aspect = (float) mc.getWindow().getGuiScaledWidth() / mc.getWindow().getGuiScaledHeight();
        Matrix4f savedProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        Matrix4f ortho = new Matrix4f().ortho(
                -orthoRange * aspect, orthoRange * aspect,
                -orthoRange, orthoRange,
                0.01f, 400f);
        RenderSystem.setProjectionMatrix(ortho, VertexSorting.ORTHOGRAPHIC_Z);
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.setIdentity();
        modelView.translate(panX, panY, -200);
        modelView.mulPose(Axis.XP.rotationDegrees(rotX));
        modelView.mulPose(Axis.YP.rotationDegrees(rotY));
        modelView.translate(-scene.getCenterX(), -scene.getCenterY(), -scene.getCenterZ());
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        PoseStack blockPose = new PoseStack();
        renderBlocks(blockPose, bufferSource);
        bufferSource.endBatch();
        RenderSystem.disableBlend();
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(savedProj, com.mojang.blaze3d.vertex.VertexSorting.ORTHOGRAPHIC_Z);
    }

    public void exportToPng(Path outputPath, int resolution, float rotX, float rotY, float zoom,
                            float panX, float panY,
                            Consumer<Path> onSuccess, Consumer<Exception> onError) {
        int maxTex = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        int maxSize = Math.min(resolution, maxTex);
        Minecraft mc = Minecraft.getInstance();
        float aspect = (float) mc.getWindow().getGuiScaledWidth() / mc.getWindow().getGuiScaledHeight();

        int exportWidth, exportHeight;
        if (aspect >= 1.0f) {
            exportWidth = maxSize;
            exportHeight = Math.max(1, Math.round(maxSize / aspect));
        } else {
            exportWidth = Math.max(1, Math.round(maxSize * aspect));
            exportHeight = maxSize;
        }
        TextureTarget fbo = new TextureTarget(exportWidth, exportHeight, true, Minecraft.ON_OSX);
        fbo.setClearColor(0f, 0f, 0f, 0f);
        fbo.clear(Minecraft.ON_OSX);
        fbo.bindWrite(true);
        RenderSystem.viewport(0, 0, exportWidth, exportHeight);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        float dim = scene.getMaxDimension();
        if (dim < 1f) dim = 1f;
        float orthoRange = dim / zoom * 0.7f;
        Matrix4f ortho = new Matrix4f().ortho(
                -orthoRange * aspect, orthoRange * aspect,
                -orthoRange, orthoRange,
                0.01f, 400f);
        RenderSystem.setProjectionMatrix(ortho, com.mojang.blaze3d.vertex.VertexSorting.ORTHOGRAPHIC_Z);
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.setIdentity();
        modelView.translate(panX, panY, -200);
        modelView.mulPose(Axis.XP.rotationDegrees(rotX));
        modelView.mulPose(Axis.YP.rotationDegrees(rotY));
        modelView.translate(-scene.getCenterX(), -scene.getCenterY(), -scene.getCenterZ());
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        PoseStack blockPose = new PoseStack();
        renderBlocks(blockPose, bufferSource);
        bufferSource.endBatch();
        // 读取像素（必须在渲染线程）
        NativeImage image = new NativeImage(exportWidth, exportHeight, false);
        RenderSystem.bindTexture(fbo.getColorTextureId());
        image.downloadTexture(0, false);
        image.flipY();
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
        fbo.destroyBuffers();
        mc.getMainRenderTarget().bindWrite(true);
        RenderSystem.viewport(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());

        // 文件写入异步执行，不阻塞渲染线程，卡游戏
        CompletableFuture.runAsync(() -> {
            try {
                if (outputPath.getParent() != null) {
                    outputPath.getParent().toFile().mkdirs();
                }
                image.writeToFile(outputPath);
                image.close();
                mc.execute(() -> onSuccess.accept(outputPath));
            } catch (Exception e) {
                Cmi.LOGGER.error("Export failed", e);
                image.close();
                mc.execute(() -> onError.accept(e));
            }
        });
    }

    private void renderBlocks(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource) {
        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        for (Map.Entry<BlockPos, BlockState> entry : scene.getBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (state.getRenderShape() == RenderShape.INVISIBLE) continue;
            ModelData modelData = ModelData.EMPTY;
            BlockEntity be = virtualLevel.getBlockEntity(pos);
            if (be != null) {
                try {
                    modelData = be.getModelData();
                } catch (Exception ignored) {
                }
                if (modelData == ModelData.EMPTY && be instanceof IModelOffsetProvider offsetProvider) {
                    try {
                        BlockPos offset = offsetProvider.getModelOffset(state, Vec3i.ZERO);
                        if (offset != null) {
                            modelData = ModelData.builder()
                                    .with(IEProperties.Model.SUBMODEL_OFFSET, offset)
                                    .build();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            BakedModel bakedModel = dispatcher.getBlockModel(state);
            modelData = bakedModel.getModelData(virtualLevel, pos, state, modelData);
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            dispatcher.renderSingleBlock(state, poseStack, bufferSource,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                    modelData, null);
            poseStack.popPose();
        }
        bufferSource.endBatch();

        // 液体处理
        for (BlockEntity be : virtualLevel.getRenderedBlockEntities()) {
            BlockEntityRenderer<BlockEntity> beRenderer =
                    mc.getBlockEntityRenderDispatcher().getRenderer(be);
            if (beRenderer != null) {
                BlockPos pos = be.getBlockPos();
                poseStack.pushPose();
                poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
                try {
                    beRenderer.render(be, 0f, poseStack, bufferSource,
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                } catch (Exception ignored) {
                }
                poseStack.popPose();
            }
        }
        bufferSource.endBatch();
        for (Map.Entry<BlockPos, BlockState> entry : scene.getBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            FluidState fluidState = state.getFluidState();
            if (fluidState.isEmpty()) continue;
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            VertexConsumer fluidBuffer = bufferSource.getBuffer(RenderType.translucent());
            dispatcher.renderLiquid(pos, virtualLevel, fluidBuffer, state, fluidState);
            poseStack.popPose();
        }
        bufferSource.endBatch(RenderType.translucent());
    }
}
