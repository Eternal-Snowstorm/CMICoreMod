package dev.celestiacraft.cmi.common.block.well.blazing_blood;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.block.well.blazing_blood.capability.BlazingBloodWellFluidCapability;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import dev.celestiacraft.libs.api.register.multiblock.machine.FluidSlots;
import dev.celestiacraft.libs.api.register.multiblock.machine.IOMode;
import dev.celestiacraft.libs.api.register.multiblock.machine.MachineControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class BlazingBloodWellBlockEntity extends MachineControllerBlockEntity implements IHaveGoggleInformation {
	public BlazingBloodWellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.BLAZING_BLOOD_PUMP);
	}

	@Override
	protected String getModId() {
		return Cmi.MODID;
	}

	@Override
	protected String getMultiblockName() {
		return "blazing_blood_well";
	}

	public boolean isWorkConditions() {
		if (level == null) {
			return false;
		}
		return level.dimension() == Level.NETHER;
	}

	@Override
	protected boolean useInternalFluidStorage() {
		return false;
	}

	@Override
	protected IFluidHandler createFluidCapability() {
		return new BlazingBloodWellFluidCapability(this);
	}

	@Override
	protected int getMinFluidIO() {
		return 1;
	}

	@Override
	protected int getMaxFluidIO() {
		return 1;
	}

	@Override
	protected FluidSlots[] getFluidSlots() {
		return new FluidSlots[] {
				new FluidSlots(Integer.MAX_VALUE, IOMode.OUTPUT)
		};
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (isMachineStructureValid()) {
			CmiLang.builder()
					.translate("tooltip.blazing_blood_pump.functional")
					.forGoggles(tooltip);
		} else {
			CmiLang.builder()
					.translate("tooltip.blazing_blood_pump.non_functional")
					.forGoggles(tooltip);
		}
		return true;
	}
}