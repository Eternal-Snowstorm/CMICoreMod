package dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.cast_iron;

import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.FluidBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CastIronFluidBurnerBlockEntity extends FluidBurnerBlockEntity {
	public CastIronFluidBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getFluidTankCapacity() {
		return 8000;
	}

	@Override
	protected double getEfficiency() {
		return 1.67;
	}
}