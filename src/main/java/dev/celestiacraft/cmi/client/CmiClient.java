package dev.celestiacraft.cmi.client;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.block.CmiBlockPartialModel;
import dev.celestiacraft.cmi.client.block.CmiSpriteShiftEntry;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelPartial;
import dev.celestiacraft.cmi.event.radial.CmiRadialAction;
import dev.celestiacraft.cmi.client.key.CmiKeyMapping;
import dev.celestiacraft.cmi.client.menu.CmiRadialMenu;
import dev.celestiacraft.cmi.client.overlay.NetherBacktankAirOverlay;
import dev.celestiacraft.cmi.client.overlay.SpaceElevatorConstructionOverlay;
import dev.celestiacraft.cmi.client.overlay.SpaceElevatorFlightOverlay;
import dev.celestiacraft.cmi.client.render.SpaceElevatorHudRenderer;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.render.SpaceElevatorBaseConsoleRenderer;
import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopRenderer;
import dev.celestiacraft.cmi.common.entity.dev.qi_month.QiMonthRenderer;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketRenderer;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketTier;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorRenderer;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class CmiClient {
	public static void onCtorClient(IEventBus bus) {
		bus.addListener(CmiClient::onClientSetup);
		bus.addListener(CmiClient::onRegisterKeys);
		bus.addListener(CmiClient::onRegisterRenderers);
		bus.addListener(SpaceElevatorHudRenderer::registerShaders);
		bus.addListener(NetherBacktankAirOverlay::register);
		bus.addListener(SpaceElevatorFlightOverlay::register);
		bus.addListener(SpaceElevatorConstructionOverlay::register);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		CmiBlockPartialModel.init();
		CmiSpriteShiftEntry.init();

		MetalCogWheelPartial.init();

		EntityRenderers.register(CmiEntity.QI_MONTH.get(), QiMonthRenderer::new);
		EntityRenderers.register(CmiEntity.SPACE_ELEVATOR.get(), SpaceElevatorRenderer::new);
		for (ProspectingRocketTier tier : ProspectingRocketTier.values()) {
			EntityRenderers.register(CmiEntity.prospectingRocket(tier).get(), ProspectingRocketRenderer::new);
		}

		CmiRadialMenu.register();
		CmiRadialAction.register();
	}

	@SubscribeEvent
	public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
		event.register(CmiKeyMapping.OPEN_RADIAL);
	}

	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(CmiBlockEntity.SPACE_ELEVATOR_BASE_CONSOLE.get(), (context) -> {
			return new SpaceElevatorBaseConsoleRenderer();
		});
		event.registerBlockEntityRenderer(CmiBlockEntity.SPACE_ELEVATOR_TOP.get(), (context) -> {
			return new SpaceElevatorTopRenderer();
		});
	}
}
