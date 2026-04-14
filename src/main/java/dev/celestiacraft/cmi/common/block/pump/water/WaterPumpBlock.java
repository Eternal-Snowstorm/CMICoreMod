package dev.celestiacraft.cmi.common.block.pump.water;

import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlock;
import net.minecraft.world.level.block.Blocks;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class WaterPumpBlock extends ControllerBlock<WaterPumpBlockEntity> {
	public WaterPumpBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<WaterPumpBlockEntity> getBlockEntityClass() {
		return WaterPumpBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends WaterPumpBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.WATER_PUMP.get();
	}
}