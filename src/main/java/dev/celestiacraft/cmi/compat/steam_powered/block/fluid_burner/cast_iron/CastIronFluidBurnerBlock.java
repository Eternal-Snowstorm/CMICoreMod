package dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.cast_iron;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.FluidBurnerBlock;
import dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner.FluidBurnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CastIronFluidBurnerBlock extends FluidBurnerBlock {
	public CastIronFluidBurnerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<FluidBurnerBlockEntity> getBlockEntityClass() {
		return FluidBurnerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends FluidBurnerBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.CAST_IRON_FLUID_BURNER.get();
	}
}