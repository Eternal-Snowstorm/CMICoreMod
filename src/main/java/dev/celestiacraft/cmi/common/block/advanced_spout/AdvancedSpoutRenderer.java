package dev.celestiacraft.cmi.common.block.advanced_spout;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.celestiacraft.cmi.client.block.CmiBlockPartialModel;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

public class AdvancedSpoutRenderer extends SafeBlockEntityRenderer<AdvancedSpoutBlockEntity> {
	static final PartialModel[] BITS = {
			CmiBlockPartialModel.SPOUT_TOP,
			CmiBlockPartialModel.SPOUT_MIDDLE,
			CmiBlockPartialModel.SPOUT_BOTTOM
	};

	public AdvancedSpoutRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(AdvancedSpoutBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		SmartFluidTankBehaviour tank = entity.getBehaviour(SmartFluidTankBehaviour.TYPE);
		if (tank == null) {
			return;
		}

		TankSegment primaryTank = tank.getPrimaryTank();
		FluidStack fluidStack = primaryTank.getRenderedFluid();
		float level = primaryTank.getFluidLevel().getValue(partialTicks);

		boolean top = fluidStack.getFluid().getFluidType().isLighterThanAir();

		level = Math.max(level, 0.175f);
		float min = 2.5f / 16f;
		float max = min + (11 / 16f);
		float yOffset = (11 / 16f) * level;

		if (!fluidStack.isEmpty() && level != 0) {
			stack.pushPose();
			if (!top) {
				stack.translate(0, yOffset, 0);
			} else {
				stack.translate(0, max - min, 0);
			}

			ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
					fluidStack,
					min,
					min - yOffset,
					min,
					max,
					min,
					max,
					source,
					stack,
					light,
					false,
					true
			);

			stack.popPose();
		}

		int processingTicks = entity.processingTicks;
		float processingPT = processingTicks - partialTicks;
		float processingProgress = 1 - (processingPT - 5) / 10;
		processingProgress = Mth.clamp(processingProgress, 0, 1);
		float radius = 0;

		if (!fluidStack.isEmpty() && processingTicks != -1) {
			radius = (float) (Math.pow(((2 * processingProgress) - 1), 2) - 1);
			AABB bb = new AABB(0.5, 0.0, 0.5, 0.5, -1.2, 0.5).inflate(radius / 32f);
			ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
					fluidStack,
					(float) bb.minX,
					(float) bb.minY,
					(float) bb.minZ,
					(float) bb.maxX,
					(float) bb.maxY,
					(float) bb.maxZ,
					source,
					stack,
					light,
					true,
					true
			);
		}

		float squeeze = radius;
		if (processingPT < 0) {
			squeeze = 0;
		} else if (processingPT < 2) {
			squeeze = Mth.lerp(processingPT / 2f, 0, -1);
		} else if (processingPT < 10) {
			squeeze = -1;
		}

		stack.pushPose();
		for (PartialModel bit : BITS) {
			CachedBuffers.partial(bit, entity.getBlockState())
					.light(light)
					.renderInto(stack, source.getBuffer(RenderType.solid()));
			stack.translate(0, -3 * squeeze / 32f, 0);
		}
		stack.popPose();
	}
}
