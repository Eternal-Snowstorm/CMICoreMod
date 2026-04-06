package dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.bronze;

import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.FluidBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeFluidBurnerBlockEntity extends FluidBurnerBlockEntity {
	public BronzeFluidBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getFluidTankCapacity() {
		return 4000;
	}

	@Override
	protected double getEfficiency() {
		return 1.33;
	}
}