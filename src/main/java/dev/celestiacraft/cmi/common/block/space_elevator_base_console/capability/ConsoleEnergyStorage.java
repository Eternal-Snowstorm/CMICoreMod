package dev.celestiacraft.cmi.common.block.space_elevator_base_console.capability;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class ConsoleEnergyStorage implements IEnergyStorage {
	private final SpaceElevatorBaseConsoleBlockEntity entity;

	public ConsoleEnergyStorage(SpaceElevatorBaseConsoleBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int received = Math.min(
				entity.getEnergyCapacity() - entity.getEnergyStored(),
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
		return entity.getEnergyCapacity();
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
