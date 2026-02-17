package dev.celestiacraft.cmi.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.time.LocalDateTime;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GetDateModifyTitle {
	private static void modifyTitle(String title) {
		Minecraft.getInstance().getWindow().setTitle(title);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		int month = LocalDateTime.now().getMonthValue();
		int day = LocalDateTime.now().getDayOfMonth();

		event.enqueueWork(() -> {
			if (month == 4 && day == 1) {
				modifyTitle("Create: Infinity Mechanism");
			} else {
				modifyTitle("Create: Mechanism and Innovation");
			}
		});
	}
}