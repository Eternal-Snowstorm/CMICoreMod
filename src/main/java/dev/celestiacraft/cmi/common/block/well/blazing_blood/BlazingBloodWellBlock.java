package dev.celestiacraft.cmi.common.block.well.blazing_blood;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlazingBloodWellBlock extends ControllerBlock<BlazingBloodWellBlockEntity> {
	public BlazingBloodWellBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<BlazingBloodWellBlockEntity> getBlockEntityClass() {
		return BlazingBloodWellBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends BlazingBloodWellBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.BLAZING_BLOOD_WELL.get();
	}
}