package dev.celestiacraft.cmi.common.block.solar_boiler.steel;

import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.cmi.config.common.SolarBoilerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SteelSolarBoilerBlockEntity extends SolarBoilerBlockEntity {
	public SteelSolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getWaterConsumptionPerTick() {
		return SolarBoilerConfig.STEEL_EFFICIENCY.get();
	}

	@Override
	protected int getFluidCapacity() {
		return SolarBoilerConfig.STEEL_CAPACITY.get();
	}
}