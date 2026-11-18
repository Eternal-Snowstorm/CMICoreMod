package dev.celestiacraft.cmi.common.block.general_astronautic_platform.io;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AstronauticPlatformIoPortBlock extends SpaceElevatorIoPortBlock {
	public AstronauticPlatformIoPortBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<? extends SpaceElevatorIoPortBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.ASTRONAUTIC_PLATFORM_IO_PORT.get();
	}
}