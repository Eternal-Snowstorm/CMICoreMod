package dev.celestiacraft.cmi.common.block.wind_vane;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WindVaneBlockEntity extends BlockEntity {
	private boolean registered = false;

	public WindVaneBlockEntity(BlockEntityType<? extends WindVaneBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (level != null && !registered) {
			WindVaneManager.add(level, worldPosition);
			registered = true;
		}
	}

	@Override
	public void setRemoved() {
		if (this.level != null && registered) {
			WindVaneManager.remove(level, worldPosition);
			registered = false;
		}
		super.setRemoved();
	}
}