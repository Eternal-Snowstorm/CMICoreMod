package dev.celestiacraft.cmi.common.block.solar_boiler.steel;

import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SteelSolarBoilerBlockEntity extends SolarBoilerBlockEntity {
	public SteelSolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected int getWaterConsumptionPerTick() {
		return 6;
	}

	@Override
	protected int getFluidCapacity() {
		return 12000;
	}
}