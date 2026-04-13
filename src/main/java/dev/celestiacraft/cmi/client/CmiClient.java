package dev.celestiacraft.cmi.client;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.block.resource.CmiBlockPartialModel;
import dev.celestiacraft.cmi.client.block.resource.CmiSpriteShiftEntry;
import dev.celestiacraft.cmi.client.overlay.NetherBacktankAirOverlay;
import dev.celestiacraft.cmi.client.overlay.SpaceElevatorConstructionOverlay;
import dev.celestiacraft.cmi.client.overlay.SpaceElevatorFlightOverlay;
import dev.celestiacraft.cmi.client.ponder.CmiPonderPlugin;
import dev.celestiacraft.cmi.client.render.SpaceElevatorHudRenderer;
import dev.celestiacraft.cmi.common.entity.dev.qi_month.QiMonthRenderer;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorRenderer;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class CmiClient {
	public static void onCtorClient(IEventBus bus) {
		bus.addListener(CmiClient::onClientSetup);
		bus.addListener(SpaceElevatorHudRenderer::registerShaders);
		bus.addListener(NetherBacktankAirOverlay::register);
		bus.addListener(SpaceElevatorFlightOverlay::register);
		bus.addListener(SpaceElevatorConstructionOverlay::register);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		CmiBlockPartialModel.init();
		CmiSpriteShiftEntry.init();

		EntityRenderers.register(CmiEntity.QI_MONTH.get(), QiMonthRenderer::new);
		EntityRenderers.register(CmiEntity.SPACE_ELEVATOR.get(), SpaceElevatorRenderer::new);

		PonderIndex.addPlugin(new CmiPonderPlugin());
	}
}