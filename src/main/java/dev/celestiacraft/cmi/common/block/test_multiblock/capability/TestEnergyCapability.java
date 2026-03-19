package dev.celestiacraft.cmi.common.block.test_multiblock.capability;

import dev.celestiacraft.cmi.common.block.test_multiblock.TestMultiblockBlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class TestEnergyCapability implements IEnergyStorage {
	private final TestMultiblockBlockEntity entity;

	public TestEnergyCapability(TestMultiblockBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return entity.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return entity.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return entity.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return 32000;
	}

	@Override
	public boolean canExtract() {
		return entity.isStructureValid();
	}

	@Override
	public boolean canReceive() {
		return entity.isStructureValid();
	}
}