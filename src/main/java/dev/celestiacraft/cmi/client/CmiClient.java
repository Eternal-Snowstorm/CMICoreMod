package dev.celestiacraft.cmi.client;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import dev.celestiacraft.cmi.client.block.resource.CmiBlockPartialModel;
import dev.celestiacraft.cmi.client.block.resource.CmiSpriteShiftEntry;
import dev.celestiacraft.cmi.client.ponder.CmiPonderIndex;

public class CmiClient {
	public static void onCtorClient(IEventBus bus) {
		bus.addListener(CmiClient::onClientSetup);
	}

	public static void onClientSetup(FMLClientSetupEvent event) {
		CmiBlockPartialModel.init();
		CmiSpriteShiftEntry.init();
		event.enqueueWork(CmiPonderIndex::register);
	}
}