package dev.celestiacraft.cmi.common.block.solar_boiler.bronze;

import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlock;
import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BronzeSolarBoilerBlock extends SolarBoilerBlock {
	public BronzeSolarBoilerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<SolarBoilerBlockEntity> getBlockEntityClass() {
		return SolarBoilerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SolarBoilerBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.BRONZE_SOLAR_BOILER.get();
	}
}