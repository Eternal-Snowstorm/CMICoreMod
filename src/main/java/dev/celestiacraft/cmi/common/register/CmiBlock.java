package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.block.*;

public class CmiBlock {
	public static void register() {
		FluidBurnerBlocks.register();
		MachineBlocks.register();
		MultiblockBlocks.register();
		OtherBlocks.register();
		SolarBoilerBlocks.register();
		SpaceElevatorBlocks.register();
		VentBlocks.register();
		WallBlocks.register();

		Cmi.LOGGER.info("{} Blocks Registered!", Cmi.MODID);
	}
}