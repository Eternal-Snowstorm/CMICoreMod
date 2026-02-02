package top.nebula.cmi.common.block.advanced_spout;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import top.nebula.cmi.config.CommonConfig;

public class AdvancedSpoutBlockEntity extends SpoutBlockEntity {

	public AdvancedSpoutBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		for (BlockEntityBehaviour behaviour : behaviours) {
			if (behaviour instanceof SmartFluidTankBehaviour tankBehaviour) {
				tankBehaviour.getPrimaryHandler().setCapacity(CommonConfig.ADVANCED_SPOUT_CAPACITY.get());
				break;
			}
		}
	}

	public int getFillingTime() {
		return Math.max(CommonConfig.ADVANCED_SPOUT_FILLING_TIME.get(), 6);
	}

	private SmartFluidTankBehaviour getTank() {
		return getBehaviour(SmartFluidTankBehaviour.TYPE);
	}

	private FluidStack getCurrentFluid() {
		return getTank().getPrimaryHandler().getFluid();
	}

	public boolean isPowered() {
		return level != null && level.hasNeighborSignal(worldPosition);
	}

	@Override
	protected ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if (isPowered()) {
			return ProcessingResult.HOLD;
		}

		if (processingTicks != -1 && processingTicks != 5) {
			return ProcessingResult.HOLD;
		}
		if (level == null || !FillingBySpout.canItemBeFilled(level, transported.stack)) {
			return ProcessingResult.PASS;
		}
		if (getTank().isEmpty()) {
			return ProcessingResult.HOLD;
		}
		FluidStack fluid = getCurrentFluid();
		int requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(level, transported.stack, fluid.copy());
		if (requiredAmountForItem == -1) {
			return ProcessingResult.PASS;
		}
		if (requiredAmountForItem > fluid.getAmount()) {
			return ProcessingResult.HOLD;
		}

		if (processingTicks == -1) {
			processingTicks = getFillingTime();
			notifyUpdate();
			AllSoundEvents.SPOUTING.playOnServer(level, worldPosition, 0.75f, 0.9f + 0.2f * (float) Math.random());
			return ProcessingResult.HOLD;
		}

		ItemStack out = FillingBySpout.fillItem(level, requiredAmountForItem, transported.stack, fluid);
		if (!out.isEmpty()) {
			List<TransportedItemStack> outList = new ArrayList<>();
			TransportedItemStack held = null;
			TransportedItemStack result = transported.copy();
			result.stack = out;
			if (!transported.stack.isEmpty()) {
				held = transported.copy();
			}
			outList.add(result);
			handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(outList, held));
		}

		getTank().getPrimaryHandler().setFluid(fluid);
		sendSplash = true;
		notifyUpdate();
		return ProcessingResult.HOLD;
	}

	@Override
	public void tick() {
		if (isPowered()) {
			return;
		}
		tickBehaviours();

		if (level == null)
			return;

		FluidStack currentFluidInTank = getCurrentFluid();

		if (processingTicks == -1 && (isVirtual() || !level.isClientSide()) && !currentFluidInTank.isEmpty()) {
			BlockSpoutingBehaviour.forEach(behaviour -> {
				if (customProcess != null)
					return;
				if (behaviour.fillBlock(level, worldPosition.below(2), this, currentFluidInTank, true) > 0) {
					processingTicks = getFillingTime();
					customProcess = behaviour;
					notifyUpdate();
				}
			});
		}

		if (processingTicks >= 0) {
			processingTicks--;
			if (processingTicks == 5 && customProcess != null) {
				int fillBlock = customProcess.fillBlock(level, worldPosition.below(2), this, currentFluidInTank, false);
				customProcess = null;
				if (fillBlock > 0) {
					getTank().getPrimaryHandler()
						.setFluid(FluidHelper.copyStackWithAmount(currentFluidInTank,
							currentFluidInTank.getAmount() - fillBlock));
					sendSplash = true;
					notifyUpdate();
				}
			}
		}

		if (processingTicks >= 8 && level.isClientSide()) {
			spawnProcessingParticles(getTank().getPrimaryTank().getRenderedFluid());
		}
	}

	private void tickBehaviours() {
		if (level == null)
			return;
		forEachBehaviour(BlockEntityBehaviour::tick);
	}

	protected void spawnProcessingParticles(FluidStack fluid) {
		if (isVirtual() || level == null)
			return;
		Vec3 vec = VecHelper.getCenterOf(worldPosition);
		vec = vec.subtract(0, 8 / 16f, 0);
		ParticleOptions particle = FluidFX.getFluidParticle(fluid);
		level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, 0, -.1f, 0);
	}
}