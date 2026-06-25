package dev.celestiacraft.cmi.common.block.solar_boiler.cast_iron;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.cmi.config.common.SolarBoilerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CastIronSolarBoilerBlockEntity extends SolarBoilerBlockEntity {
	public CastIronSolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public int getWaterConsumptionPerTick() {
		return SolarBoilerConfig.CAST_IRON_EFFICIENCY.get();
	}

	@Override
	protected int getFluidCapacity() {
		return SolarBoilerConfig.CAST_IRON_CAPACITY.get();
	}
}