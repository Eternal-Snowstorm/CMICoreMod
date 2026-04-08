package dev.celestiacraft.cmi.common.block.water_pump;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.api.register.multiblock.machine.FluidSlots;
import dev.celestiacraft.cmi.api.register.multiblock.machine.IOMode;
import dev.celestiacraft.cmi.api.register.multiblock.machine.MachineControllerBlockEntity;
import dev.celestiacraft.cmi.common.block.water_pump.capability.WaterPumpFluidCapability;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class WaterPumpBlockEntity extends MachineControllerBlockEntity implements IHaveGoggleInformation {
	public WaterPumpBlockEntity(BlockEntityType<? extends WaterPumpBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.WATER_PUMP);
	}

	@Override
	protected String getModId() {
		return Cmi.MODID;
	}

	@Override
	protected String getMultiblockName() {
		return "water_pump";
	}

	public boolean isOcean() {
		if (level != null) {
			return level.getBiome(getBlockPos()).is(BiomeTags.IS_OCEAN)
					&& getBlockPos().getY() == 62;
		}
		return false;
	}

	@Override
	protected boolean useInternalFluidStorage() {
		return false;
	}

	@Override
	protected IFluidHandler createFluidCapability() {
		return new WaterPumpFluidCapability(this);
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
					.translate("tooltip.water_pump.functional")
					.forGoggles(tooltip);
		} else {
			CmiLang.builder()
					.translate("tooltip.water_pump.non_functional")
					.forGoggles(tooltip);
		}
		return true;
	}
}