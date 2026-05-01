package dev.celestiacraft.cmi.common.block.well.lava;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.well.lava.capability.LavaWellFluidCapability;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import dev.celestiacraft.libs.api.register.multiblock.machine.FluidSlots;
import dev.celestiacraft.libs.api.register.multiblock.machine.IOMode;
import dev.celestiacraft.libs.api.register.multiblock.machine.MachineControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class LavaWellBlockEntity extends MachineControllerBlockEntity {
	public LavaWellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.LAVA_PUMP);
	}

	@Override
	protected String getModId() {
		return Cmi.MODID;
	}

	@Override
	protected String getMultiblockName() {
		return "lava_well";
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
		return new LavaWellFluidCapability(this);
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
}