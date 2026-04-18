package dev.celestiacraft.cmi.common.block.well.lava;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LavaWellBlock extends ControllerBlock<LavaWellBlockEntity> {
	public LavaWellBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<LavaWellBlockEntity> getBlockEntityClass() {
		return LavaWellBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends LavaWellBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.LAVA_WELL.get();
	}
}