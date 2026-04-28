package dev.celestiacraft.cmi.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.utils.ModResources;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SpaceElevatorFlightOverlay implements IGuiOverlay {
	public static final SpaceElevatorFlightOverlay INSTANCE = new SpaceElevatorFlightOverlay();
	private static final ResourceLocation ROCKET_BAR;
	private static final ResourceLocation ROCKET;

	static {
		ROCKET_BAR = ModResources.loadAd("textures/gui/sprites/overlay/rocket_bar.png").getLocation();
		ROCKET = ModResources.loadAd("textures/gui/sprites/overlay/rocket.png").getLocation();
	}

	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("cmi_space_elevator_flight", INSTANCE);
	}

	@Override
	public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui || mc.options.renderDebug || mc.player == null || mc.player.isSpectator() || mc.gameMode == null) {
			return;
		}
		if (!(mc.player.getVehicle() instanceof SpaceElevatorEntity elevator) || !elevator.shouldRenderLaunchHud()) {
			return;
		}

		Font font = mc.font;
		PoseStack poseStack = graphics.pose();

		if (elevator.isLaunchCountdownActive()) {
			poseStack.pushPose();
			poseStack.translate(width / 2.0F, height / 2.0F, 0.0F);
			poseStack.scale(4.0F, 4.0F, 4.0F);
			graphics.drawCenteredString(font, String.valueOf(elevator.getLaunchCountdownSeconds()), 0, -10, 0xE53253);
			poseStack.popPose();
		}

		graphics.blit(ROCKET_BAR, 0, height / 2, 0, 0, 16, 128, 16, 128);

		poseStack.pushPose();
		double y = Mth.clamp(100.0D + elevator.getLaunchHudProgress() * (AdAstraConfig.atmosphereLeave - 100.0D), 100.0D, AdAstraConfig.atmosphereLeave);
		poseStack.translate(0.3F, (float) ((AdAstraConfig.atmosphereLeave - y - 500.0D) / 4.5D), 0.0F);
		graphics.blit(ROCKET, 3, height / 2 + 113, 0, 0, 8, 11, 8, 11);
		poseStack.popPose();
	}
}
