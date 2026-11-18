package dev.celestiacraft.cmi.common.block.space_elevator_io;

import org.jetbrains.annotations.NotNull;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortType;
import net.minecraftforge.common.util.LazyOptional;

public interface SpaceElevatorIoHost {
	// 按端口类型返回对外暴露的能力，没有对应能力时返回空
	@NotNull
	LazyOptional<?> getIoCapability(@NotNull IoPortType type);
}
