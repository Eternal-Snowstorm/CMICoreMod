package dev.celestiacraft.cmi.common.block.space_elevator_base_console.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

public class SpaceElevatorConsoleTransferEvent extends Event {
	private final ServerLevel level;
	private final BlockPos consolePos;

	public SpaceElevatorConsoleTransferEvent(ServerLevel level, BlockPos consolePos) {
		this.level = level;
		this.consolePos = consolePos.immutable();
	}

	public ServerLevel level() {
		return level;
	}

	public BlockPos consolePos() {
		return consolePos;
	}
}
