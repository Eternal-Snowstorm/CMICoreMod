package dev.celestiacraft.cmi.common.block.solar_boiler.steel;

import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlock;
import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SteelSolarBoilerBlock extends SolarBoilerBlock {
	public SteelSolarBoilerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<SolarBoilerBlockEntity> getBlockEntityClass() {
		return SolarBoilerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SolarBoilerBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.STEEL_SOLAR_BOILER.get();
	}
}