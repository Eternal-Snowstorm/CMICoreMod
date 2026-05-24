package dev.celestiacraft.cmi.client.gui;

import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ScaledTankWidget extends TankWidget {
	private static final int MAJOR_DIVISIONS = 4;
	private static final int MINOR_DIVISIONS = 2;
	private static final int MAJOR_TICK_LENGTH = 4;
	private static final int MINOR_TICK_LENGTH = 2;
	private static final int TICK_COLOR = 0xFFC8C8C8;
	private static final int TICK_SHADOW = 0xFF202020;

	private static final double LERP_RATE_PER_TICK = 0.25D;
	private static final double SNAP_THRESHOLD = 0.5D;

	private double displayAmount = -1.0D;

	public ScaledTankWidget(IFluidTransfer fluidTank, int tank, int x, int y, int width, int height, boolean allowClickContainerFilling, boolean allowClickContainerEmptying) {
		super(fluidTank, tank, x, y, width, height, allowClickContainerFilling, allowClickContainerEmptying);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		FluidStack fluid = lastFluidInTank;
		long realAmount = fluid == null ? 0 : fluid.getAmount();

		if (displayAmount < 0) {
			displayAmount = realAmount;
		} else {
			double delta = realAmount - displayAmount;
			if (Math.abs(delta) <= SNAP_THRESHOLD) {
				displayAmount = realAmount;
			} else {
				double step = delta * LERP_RATE_PER_TICK * partialTicks;
				if (Math.abs(step) > Math.abs(delta)) {
					step = delta;
				}
				displayAmount += step;
			}
		}

		long restoreAmount = realAmount;
		boolean swapped = false;
		if (fluid != null && !fluid.isEmpty()) {
			long shown = Math.max(1L, Math.round(displayAmount));
			if (shown != realAmount) {
				fluid.setAmount(shown);
				swapped = true;
			}
		}
		try {
			super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
		} finally {
			if (swapped) {
				fluid.setAmount(restoreAmount);
			}
		}

		drawTicks(graphics);
	}

	private void drawTicks(GuiGraphics graphics) {
		int innerX = getPosition().x + 1;
		int innerY = getPosition().y + 1;
		int innerW = getSize().width - 2;
		int innerH = getSize().height - 2;

		int totalDivisions = MAJOR_DIVISIONS * MINOR_DIVISIONS;
		for (int i = 1; i < totalDivisions; i++) {
			boolean major = i % MINOR_DIVISIONS == 0;
			int tickLength = major ? MAJOR_TICK_LENGTH : MINOR_TICK_LENGTH;
			int y = innerY + Math.round(innerH * (i / (float) totalDivisions));

			int leftStart = innerX;
			int leftEnd = innerX + tickLength;
			int rightStart = innerX + innerW - tickLength;
			int rightEnd = innerX + innerW;

			graphics.fill(leftStart, y + 1, leftEnd, y + 2, TICK_SHADOW);
			graphics.fill(rightStart, y + 1, rightEnd, y + 2, TICK_SHADOW);
			graphics.fill(leftStart, y, leftEnd, y + 1, TICK_COLOR);
			graphics.fill(rightStart, y, rightEnd, y + 1, TICK_COLOR);
		}
	}
}
