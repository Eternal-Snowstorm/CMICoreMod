package dev.celestiacraft.cmi.common.block.pump.lava;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.ControllerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LavaPumpBlock extends ControllerBlock<LavaPumpBlockEntity> {
	public LavaPumpBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<LavaPumpBlockEntity> getBlockEntityClass() {
		return LavaPumpBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends LavaPumpBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.LAVA_PUMP.get();
	}
}