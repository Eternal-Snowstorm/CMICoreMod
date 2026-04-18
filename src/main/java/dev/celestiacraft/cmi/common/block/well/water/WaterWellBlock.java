package dev.celestiacraft.cmi.common.block.well.water;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class WaterWellBlock extends ControllerBlock<WaterWellBlockEntity> {
	public WaterWellBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<WaterWellBlockEntity> getBlockEntityClass() {
		return WaterWellBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends WaterWellBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.WATER_WELL.get();
	}
}