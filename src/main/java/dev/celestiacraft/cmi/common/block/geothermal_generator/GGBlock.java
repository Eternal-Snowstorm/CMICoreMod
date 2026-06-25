package dev.celestiacraft.cmi.common.block.geothermal_generator;

import com.simibubi.create.foundation.block.IBE;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GGBlock extends BasicBlock implements IBE<GGBlockEntity> {
	public GGBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<GGBlockEntity> getBlockEntityClass() {
		return GGBlockEntity.class;
	}

	@Override
	protected boolean useLitState() {
		return true;
	}

	@Override
	public BlockEntityType<? extends GGBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.GEOTHERMAL_GENERATOR.get();
	}
}