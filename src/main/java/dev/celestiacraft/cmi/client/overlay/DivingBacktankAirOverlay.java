package dev.celestiacraft.cmi.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import dev.celestiacraft.cmi.compat.create.CreateOxygenSupport;
import dev.celestiacraft.cmi.common.modifier.diving.DivingModifier;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

/**
 * Diving 强化专用 Backtank 余量显示。
 * <p>
 * 参照 {@link NetherBacktankAirOverlay}：当玩家的头盔带有
 * {@link DivingModifier}，并且处于水中或 minecraft:the_nether 维度时，
 * 在 HUD 上显示当前剩余的可呼吸 Backtank 空气。
 */
public class DivingBacktankAirOverlay implements IGuiOverlay {
	public static final DivingBacktankAirOverlay INSTANCE = new DivingBacktankAirOverlay();

	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("cmi_diving_backtank_air", INSTANCE);
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

		Level level = player.level();
		boolean inNether = level.dimension() == Level.NETHER;
		boolean inWater = player.isEyeInFluidType(ForgeMod.WATER_TYPE.get());
		if (!inNether && !inWater) {
			return;
		}

		// 水中若可自然呼吸（水下呼吸效果、气泡柱等）则不消耗背罐空气，也不显示
		if (!inNether) {
			boolean isBubbleColumn = level.getBlockState(BlockPos.containing(player.getX(), player.getEyeY(), player.getZ()))
					.is(Blocks.BUBBLE_COLUMN);
			boolean canBreathe = !player.canDrownInFluidType(player.getEyeInFluidType())
					|| MobEffectUtil.hasWaterBreathing(player)
					|| player.getAbilities().invulnerable;
			if (isBubbleColumn || canBreathe) {
				return;
			}
		}

		if (!DivingModifier.isActive(player)) {
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
