package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpaceElevatorCameraLock {
	@Nullable
	private static CameraType savedCameraType;
	private static boolean wasRiding;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) {
			restoreIfNeeded(mc);
			return;
		}

		Entity vehicle = player.getVehicle();
		boolean riding = vehicle instanceof SpaceElevatorEntity;

		if (riding) {
			if (!wasRiding) {
				savedCameraType = mc.options.getCameraType();
				wasRiding = true;
			}
			if (mc.options.getCameraType() != CameraType.THIRD_PERSON_BACK) {
				mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}
		} else {
			restoreIfNeeded(mc);
		}
	}

	private static void restoreIfNeeded(Minecraft mc) {
		if (wasRiding && savedCameraType != null) {
			mc.options.setCameraType(savedCameraType);
		}
		wasRiding = false;
		savedCameraType = null;
	}
}
