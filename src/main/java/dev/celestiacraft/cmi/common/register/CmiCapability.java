package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenFluidCapability;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenItemCapability;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenItemHandler;
import dev.celestiacraft.cmi.common.block.void_dust_collector.capability.VDCEnergyStorage;
import dev.celestiacraft.cmi.common.block.void_dust_collector.capability.VDCItemHandler;
import dev.celestiacraft.cmi.common.block.void_dust_collector.capability.VDCItmeCapability;
import dev.celestiacraft.cmi.common.block.water_pump.capability.WaterPumpFluidCapability;
import dev.celestiacraft.cmi.compat.steam_powered.block.FluidBurnerFluidHandler;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CmiCapability {
	@SubscribeEvent
	public static void registerCapability(RegisterCapabilitiesEvent event) {
		// 水井
		event.register(WaterPumpFluidCapability.class);
		// 虚空粉末收集器
		event.register(VDCEnergyStorage.class);
		event.register(VDCItemHandler.class);
		event.register(VDCItmeCapability.class);
		// TestCokeOven
		event.register(CokeOvenFluidCapability.class);
		event.register(CokeOvenItemCapability.class);
		event.register(CokeOvenItemHandler.class);
		// FluidBurner
		event.register(FluidBurnerFluidHandler.class);
	}
}
