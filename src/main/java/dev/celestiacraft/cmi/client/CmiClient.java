package dev.celestiacraft.cmi.client;

import dev.celestiacraft.cmi.client.block.resource.CmiBlockPartialModel;
import dev.celestiacraft.cmi.client.block.resource.CmiSpriteShiftEntry;
import dev.celestiacraft.cmi.client.overlay.NetherBacktankAirOverlay;
import dev.celestiacraft.cmi.client.ponder.CmiPonderIndex;
import dev.celestiacraft.cmi.common.entity.qi_month.QiMonthRenderer;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CmiClient {
	public static void onCtorClient(IEventBus bus) {
		bus.addListener(CmiClient::onClientSetup);
		bus.addListener(NetherBacktankAirOverlay::register);
	}

	public static void onClientSetup(FMLClientSetupEvent event) {
		CmiBlockPartialModel.init();
		CmiSpriteShiftEntry.init();

		EntityRenderers.register(CmiEntity.QI_MONTH.get(), QiMonthRenderer::new);

		event.enqueueWork(() -> {
			CmiPonderIndex.register();
		});
	}
}