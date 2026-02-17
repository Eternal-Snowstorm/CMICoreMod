package dev.celestiacraft.cmi.compat.jade;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlock;

@WailaPlugin
public class CmiJadePlugin implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(CmiComponentProvider.INSTANCE, AdvancedSpoutBlock.class);
		Cmi.LOGGER.info("Jade Plugin is registered!");
	}
}