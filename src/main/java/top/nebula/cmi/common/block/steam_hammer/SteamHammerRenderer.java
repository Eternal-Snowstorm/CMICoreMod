package top.nebula.cmi.common.block.steam_hammer;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import top.nebula.cmi.client.block.resource.CmiBlockPartialModel;

public class SteamHammerRenderer extends KineticBlockEntityRenderer<MechanicalPressBlockEntity> {
	public SteamHammerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(@NotNull MechanicalPressBlockEntity entity) {
		return true;
	}

	@Override
	protected void renderSafe(
			MechanicalPressBlockEntity entity,
			float partialTicks,
			PoseStack stack,
			MultiBufferSource source,
			int light,
			int overlay
	) {
		super.renderSafe(entity, partialTicks, stack, source, light, overlay);

		if (!Backend.canUseInstancing(entity.getLevel())) {
			BlockState state = entity.getBlockState();
			PressingBehaviour pressingBehaviour = entity.getPressingBehaviour();

			float renderedHeadOffset = pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset;

			SuperByteBuffer headRender = CachedBufferer.partialFacing(
					CmiBlockPartialModel.HYDRAULIC_PRESS_HEAD,
					state,
					state.getValue(BlockStateProperties.HORIZONTAL_FACING)
			);
			headRender.translate(0.0F, -renderedHeadOffset, 0.0F)
					.light(light)
					.renderInto(stack, source.getBuffer(RenderType.solid()));
		}
	}

	@Override
	protected BlockState getRenderedBlockState(MechanicalPressBlockEntity entity) {
		return shaft(getRotationAxisOf(entity));
	}
}