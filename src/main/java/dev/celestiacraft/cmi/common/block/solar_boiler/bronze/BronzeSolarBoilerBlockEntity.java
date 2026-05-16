package dev.celestiacraft.cmi.common.block.solar_boiler.bronze;

import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.cmi.config.common.SolarBoilerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeSolarBoilerBlockEntity extends SolarBoilerBlockEntity {
	public BronzeSolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getWaterConsumptionPerTick() {
		return SolarBoilerConfig.BRONZE_EFFICIENCY.get();
	}

	@Override
	protected int getFluidCapacity() {
		return SolarBoilerConfig.BRONZE_CAPACITY.get();
	}
}