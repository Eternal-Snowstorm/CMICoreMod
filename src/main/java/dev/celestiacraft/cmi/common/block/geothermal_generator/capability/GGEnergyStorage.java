package dev.celestiacraft.cmi.common.block.geothermal_generator.capability;

import dev.celestiacraft.cmi.common.block.geothermal_generator.GGBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class GGEnergyStorage implements IEnergyStorage {
	private final GGBlockEntity entity;
	private final LazyOptional<IEnergyStorage> optional;

	public GGEnergyStorage(GGBlockEntity entity) {
		this.entity = entity;
		optional = LazyOptional.of(() -> this);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	/**
	 * 抽取能量需要照顾所剩能量不足指定的数时的情况
	 *
	 * @param maxExtract
	 * @param simulate
	 * @return
	 */
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int extracted = Math.min(
				getEnergyStored(),
				Math.min(maxExtract, 5000)
		);

		if (!simulate) {
			entity.setStoragedEnergy(entity.getStoragedEnergy() - extracted);
		}

		return extracted;
	}

	@Override
	public int getEnergyStored() {
		return entity.getStoragedEnergy();
	}

	@Override
	public int getMaxEnergyStored() {
		return 500000;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	public LazyOptional<IEnergyStorage> get(Direction direction) {
		return optional;
	}
}