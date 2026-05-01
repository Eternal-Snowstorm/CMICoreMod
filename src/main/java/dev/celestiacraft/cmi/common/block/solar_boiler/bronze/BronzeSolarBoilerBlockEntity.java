package dev.celestiacraft.cmi.common.block.solar_boiler.bronze;

import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeSolarBoilerBlockEntity extends SolarBoilerBlockEntity {
	public BronzeSolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected int getWaterConsumptionPerTick() {
		return 2;
	}

	@Override
	protected int getFluidCapacity() {
		return 4000;
	}
}