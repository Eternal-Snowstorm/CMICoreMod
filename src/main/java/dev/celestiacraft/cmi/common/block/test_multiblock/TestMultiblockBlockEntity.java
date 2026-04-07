package dev.celestiacraft.cmi.common.block.test_multiblock;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.machine.FluidSlots;
import dev.celestiacraft.cmi.api.register.multiblock.machine.IOMode;
import dev.celestiacraft.cmi.api.register.multiblock.machine.MachineControllerBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TestMultiblockBlockEntity extends MachineControllerBlockEntity {
	public TestMultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_MULTIBLOCK);
	}

	@Override
	protected int getMinItemIO() {
		return 1;
	}

	@Override
	protected int getMaxItemIO()	 {
		return 1;
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
	protected int getMinEnergyIO() {
		return 1;
	}

	@Override
	protected int getMaxEnergyIO() {
		return 1;
	}

	@Override
	protected int getItemSlots() {
		return 32;
	}

	@Override
	protected FluidSlots[] getFluidSlots() {
		return new FluidSlots[]{
				new FluidSlots(32000, IOMode.BOTH)
		};
	}

	@Override
	protected int getEnergyCapacity() {
		return 32000;
	}

	@Override
	protected String getModId() {
		return Cmi.MODID;
	}

	@Override
	protected String getMultiblockName() {
		return "test_multiblock";
	}
}
