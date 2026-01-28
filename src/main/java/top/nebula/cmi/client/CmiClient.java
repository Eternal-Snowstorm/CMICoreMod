package top.nebula.cmi.client;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.nebula.cmi.client.block.resource.CmiBlockPartialModel;

public class CmiClient {
	public static void onCtorClient(IEventBus bus) {
		bus.addListener(CmiClient::onClientSetup);
	}

	public static void onClientSetup(final FMLClientSetupEvent event) {
		CmiBlockPartialModel.init();
	}
}