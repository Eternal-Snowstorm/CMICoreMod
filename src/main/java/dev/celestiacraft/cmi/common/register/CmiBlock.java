package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.block.*;

public class CmiBlock {
	public static void register() {
		CoilBlocks.register();
		IoDebugBlocks.register();
		MachineBlocks.register();
		MultiblockBlocks.register();
		OtherBlocks.register();
		SpaceElevatorBlocks.register();
		WallBlocks.register();

		Cmi.LOGGER.info("{} Blocks Registered!", Cmi.NAME);
	}
}