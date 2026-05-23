package dev.celestiacraft.cmi.common.entity.space_elevator;

public interface ElevatorEnergyAnchor {
	int getEnergyStored();

	int getLaunchEnergyCost();

	boolean consumeLaunchEnergy();
}
