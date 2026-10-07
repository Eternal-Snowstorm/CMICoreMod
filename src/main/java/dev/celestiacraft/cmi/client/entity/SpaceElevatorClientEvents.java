package dev.celestiacraft.cmi.client.entity;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorTravelSoundInstance;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.c2s.StartSpaceElevatorTransportPacket;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class SpaceElevatorClientEvents {
	private static @Nullable CameraType previousCameraType;
	private static boolean jumpWasDown;

	private SpaceElevatorClientEvents() {
	}

	@SubscribeEvent
	public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if (event.getEntity().getVehicle() instanceof SpaceElevatorEntity) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			jumpWasDown = false;
			restoreCamera(mc);
			return;
		}

		Entity vehicle = mc.player.getVehicle();
		if (vehicle instanceof SpaceElevatorEntity elevator) {
			if (previousCameraType == null) {
				previousCameraType = mc.options.getCameraType();
				mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}

			boolean jumpDown = mc.options.keyJump.isDown();
			if (jumpDown && !jumpWasDown) {
				CmiNetwork.CHANNEL.sendToServer(new StartSpaceElevatorTransportPacket(elevator.getId()));
			}
			jumpWasDown = jumpDown;
			return;
		}

		jumpWasDown = false;
		restoreCamera(mc);
	}

	private static void restoreCamera(Minecraft mc) {
		if (previousCameraType != null) {
			mc.options.setCameraType(previousCameraType);
			previousCameraType = null;
		}
	}

	/**
	 * 由 {@link SpaceElevatorEntity#tick()} 通过 DistExecutor 调用, 服务端不会执行到这里。
	 */
	public static void tickTravelSound(SpaceElevatorEntity elevator) {
		if (!elevator.isFlightSoundActive()) {
			elevator.setTravelSoundStarted(false);
			return;
		}
		if (elevator.isTravelSoundStarted()) {
			return;
		}
		Minecraft.getInstance().getSoundManager().play(new SpaceElevatorTravelSoundInstance(elevator));
		elevator.setTravelSoundStarted(true);
	}
}