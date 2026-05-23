package dev.celestiacraft.cmi.event;

import dev.celestiacraft.libs.utils.FestivalUtils;
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
			if (FestivalUtils.isAprilFoolsDay()) {
				modifyTitle("Create: Infinity Mechanism");
			} else {
				modifyTitle("Create: Mechanism and Innovation");
			}
		});
	}
}