package dev.celestiacraft.cmi.common.block.solar_boiler.cast_iron;

import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CastIronSolarBoilerBlockEntity extends SolarBoilerBlockEntity {
	public CastIronSolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected int getWaterConsumptionPerTick() {
		return 4;
	}

	@Override
	protected int getFluidCapacity() {
		return 8000;
	}
}