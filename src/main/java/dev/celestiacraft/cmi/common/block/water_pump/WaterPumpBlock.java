package dev.celestiacraft.cmi.common.block.water_pump;

import dev.celestiacraft.cmi.api.register.multiblock.MultiblockControllerBlock;
import net.minecraft.world.level.block.Blocks;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class WaterPumpBlock extends MultiblockControllerBlock<WaterPumpBlockEntity> {
	public WaterPumpBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<WaterPumpBlockEntity> getBlockEntityClass() {
		return WaterPumpBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends WaterPumpBlockEntity> getBlockEntityType() {
		return CmiBlockEntityTypes.WATER_PUMP.get();
	}
}