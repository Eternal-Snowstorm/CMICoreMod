package dev.celestiacraft.cmi.compat.jade;

import dev.celestiacraft.cmi.compat.jade.provider.CmiComponentProvider;
import dev.celestiacraft.cmi.compat.jade.provider.SpaceElevatorEntityProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlock;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;

@WailaPlugin
public class CmiJadePlugin implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(CmiComponentProvider.INSTANCE, AdvancedSpoutBlock.class);
		registration.registerEntityComponent(SpaceElevatorEntityProvider.INSTANCE, SpaceElevatorEntity.class);
		Cmi.LOGGER.info("Jade Plugin is registered!");
	}
}