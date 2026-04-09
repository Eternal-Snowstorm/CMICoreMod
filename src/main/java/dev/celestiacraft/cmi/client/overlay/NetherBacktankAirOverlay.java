package dev.celestiacraft.cmi.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import dev.celestiacraft.cmi.compat.create.CreateOxygenSupport;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

public class NetherBacktankAirOverlay implements IGuiOverlay {
	public static final NetherBacktankAirOverlay INSTANCE = new NetherBacktankAirOverlay();

	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("cmi_nether_backtank_air", INSTANCE);
	}

	@Override
	public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui || mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
			return;
		}

		LocalPlayer player = mc.player;
		if (player == null || player.isCreative()) {
			return;
		}
		if (player.level().dimension() != Level.NETHER) {
			return;
		}
		if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || player.isInLava()) {
			return;
		}
		if (!CreateOxygenSupport.hasBacktankSupport(player)) {
			return;
		}

		int timeLeft = CreateOxygenSupport.getVisualBacktankAir(player);

		PoseStack poseStack = graphics.pose();
		poseStack.pushPose();

		ItemStack backtank = getDisplayedBacktank(player);
		poseStack.translate((float) width / 2 + 90, height - 63 + (backtank.getItem().isFireResistant() ? 9 : 0), 0);

		Component text = Component.literal(StringUtil.formatTickDuration(Math.max(0, timeLeft - 1) * 20));
		GuiGameElement.of(backtank).at(0, 0).render(graphics);
		int color = 0xFF_FFFFFF;
		if (timeLeft < 60 && timeLeft % 2 == 0) {
			color = Color.mixColors(0xFF_FF0000, color, Math.max(timeLeft / 60f, .25f));
		}
		graphics.drawString(mc.font, text, 16, 5, color);

		poseStack.popPose();
	}

	private static ItemStack getDisplayedBacktank(LocalPlayer player) {
		List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);
		if (!backtanks.isEmpty()) {
			return backtanks.get(0);
		}
		return AllItems.COPPER_BACKTANK.asStack();
	}
}