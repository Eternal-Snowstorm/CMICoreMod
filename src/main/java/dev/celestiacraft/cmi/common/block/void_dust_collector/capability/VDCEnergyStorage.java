package dev.celestiacraft.cmi.common.block.void_dust_collector.capability;

import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class VDCEnergyStorage implements IEnergyStorage {
	private final VoidDustCollectorBlockEntity entity;

	public VDCEnergyStorage(VoidDustCollectorBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int received = Math.min(
				entity.getCapacity() - entity.getEnergyStored(),
				Math.min(entity.getMaxReceive(), maxReceive)
		);

		if (!simulate && received > 0) {
			entity.addEnergy(received);
		}

		return received;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return entity.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return entity.getCapacity();
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
}