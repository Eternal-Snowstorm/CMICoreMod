package dev.celestiacraft.cmi.common.block.fluid_burner.steel;

import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.common.block.fluid_burner.FluidBurnerBlock;
import dev.celestiacraft.cmi.common.block.fluid_burner.FluidBurnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SteelFluidBurnerBlock extends FluidBurnerBlock {
	public SteelFluidBurnerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<FluidBurnerBlockEntity> getBlockEntityClass() {
		return FluidBurnerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends FluidBurnerBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.STEEL_FLUID_BURNER.get();
	}
}