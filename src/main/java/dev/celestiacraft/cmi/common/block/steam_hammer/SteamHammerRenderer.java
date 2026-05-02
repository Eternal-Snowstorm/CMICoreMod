package dev.celestiacraft.cmi.common.block.steam_hammer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import dev.celestiacraft.cmi.client.block.CmiBlockPartialModel;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class SteamHammerRenderer extends KineticBlockEntityRenderer<SteamHammerBlockEntity> {
	public SteamHammerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(@NotNull SteamHammerBlockEntity entity) {
		return true;
	}

	@Override
	protected void renderSafe(SteamHammerBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		super.renderSafe(entity, partialTicks, stack, source, light, overlay);
		if (VisualizationManager.supportsVisualization(entity.getLevel())) return;

		BlockState blockState = entity.getBlockState();
		PressingBehaviour pressingBehaviour = entity.pressingBehaviour;

		stack.pushPose();
		float renderedHeadOffset = pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset;
		SuperByteBuffer headRender = CachedBuffers.partialFacing(
				CmiBlockPartialModel.STEAM_HAMMER,
				blockState,
				blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		);
		headRender.translate(0, -renderedHeadOffset, 0)
				.light(light)
				.renderInto(stack, source.getBuffer(RenderType.solid()));
		stack.popPose();
	}

	@Override
	protected BlockState getRenderedBlockState(SteamHammerBlockEntity entity) {
		return shaft(getRotationAxisOf(entity));
	}
}