package dev.celestiacraft.cmi.common.block.pump.blazing_blood;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlazingBloodPumpBlock extends ControllerBlock<BlazingBloodPumpBlockEntity> {
	public BlazingBloodPumpBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<BlazingBloodPumpBlockEntity> getBlockEntityClass() {
		return BlazingBloodPumpBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends BlazingBloodPumpBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.BLAZING_BLOOD_PUMP.get();
	}
}