package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import dev.celestiacraft.cmi.api.register.block.MetalCogWheelRegister;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

public class MetalCogWheelRenderer extends KineticBlockEntityRenderer<BracketedKineticBlockEntity> {
	public MetalCogWheelRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	protected void renderSafe(
			BracketedKineticBlockEntity entity,
			float partialTicks,
			PoseStack stack,
			MultiBufferSource source,
			int light,
			int overlay
	) {
		if (!VisualizationManager.supportsVisualization(entity.getLevel())) {
			Block block = entity.getBlockState().getBlock();
			MetalCogWheelInfo set = MetalCogWheelRegister.BLOCK_TO_SET.get(block);

			if (set == null) {
				super.renderSafe(entity, partialTicks, stack, source, light, overlay);
				return;
			}

			String material = set.getMaterial();
			Direction.Axis axis = getRotationAxisOf(entity);
			Direction facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

			VertexConsumer vc = source.getBuffer(RenderType.solid());

			if (!ICogWheel.isLargeCog(entity.getBlockState())) {
				SuperByteBuffer buffer = CachedBuffers.partialFacingVertical(
						MetalCogWheelPartial.SMALL.get(material),
						entity.getBlockState(),
						facing
				);

				renderRotatingBuffer(entity, buffer, stack, vc, light);
				return;
			}

			SuperByteBuffer cog = CachedBuffers.partialFacingVertical(
					MetalCogWheelPartial.LARGE.get(material),
					entity.getBlockState(),
					facing
			);

			renderRotatingBuffer(entity, cog, stack, vc, light);

			float angle = getAngleForLargeCogShaft(entity, axis);

			SuperByteBuffer shaft = CachedBuffers.partialFacingVertical(
					AllPartialModels.COGWHEEL_SHAFT,
					entity.getBlockState(),
					facing
			);

			kineticRotationTransform(shaft, entity, axis, angle, light);
			shaft.renderInto(stack, vc);
		}
	}

	public static float getAngleForLargeCogShaft(SimpleKineticBlockEntity entity, Direction.Axis axis) {
		BlockPos pos = entity.getBlockPos();
		float offset = getShaftAngleOffset(axis, pos);
		float time = AnimationTickHolder.getRenderTime(entity.getLevel());
		return (time * entity.getSpeed() * 3.0F / 10.0F + offset) % 360.0F / 180.0F * (float) Math.PI;
	}

	public static float getShaftAngleOffset(Direction.Axis axis, BlockPos pos) {
		return KineticBlockEntityVisual.shouldOffset(axis, pos) ? 22.5F : 0.0F;
	}
}