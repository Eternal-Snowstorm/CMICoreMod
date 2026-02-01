package top.nebula.cmi.common.block.advanced_spout;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
				tankBehaviour.getPrimaryHandler().setCapacity(CommonConfig.FAST_SPOUT_CAPACITY.get());
				break;
			}
		}
	}

	public int getFillingTime() {
		return Math.max(CommonConfig.FAST_SPOUT_FILLING_TIME.get(), 6);
	}

	private SmartFluidTankBehaviour getTank() {
		return getBehaviour(SmartFluidTankBehaviour.TYPE);
	}

	private FluidStack getCurrentFluid() {
		return getTank().getPrimaryHandler().getFluid();
	}

	@Override
	protected ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if (processingTicks != -1 && processingTicks != 5) {
			return ProcessingResult.HOLD;
		}
		if (!FillingBySpout.canItemBeFilled(level, transported.stack)) {
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
		boolean wasIdle = (processingTicks == -1);
		super.tick();
		if (wasIdle && processingTicks == FILLING_TIME - 1) {
			processingTicks = getFillingTime() - 1;
		}
	}
}