package dev.celestiacraft.cmi.common.feature.cargogrid;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.c2s.RotateCarriedItemPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class CargoGridWidget extends WidgetGroup {
	private static final int SLOT_SIZE = 18;
	private static final int ICON_PADDING = 1;
	private static final int PREVIEW_FILL_OK = 0x4055FF55;
	private static final int PREVIEW_FILL_BAD = 0x40FF5555;
	private static final int PREVIEW_BORDER_OK = 0xFF55FF55;
	private static final int PREVIEW_BORDER_BAD = 0xFFFF5555;
	private static final int SLOT_SHADOW = 0xFF373737;
	private static final int SLOT_HIGHLIGHT = 0xFFFFFFFF;
	private static final int SLOT_INNER = 0xFF8B8B8B;

	private final Container container;
	private final int cols;
	private final int rows;
	private final CargoGridLayout layout;
	private final CargoGridSlotWidget[] slots;

	public CargoGridWidget(int x, int y, int cols, int rows, Container container) {
		super(new Position(x, y), new Size(cols * SLOT_SIZE, rows * SLOT_SIZE));
		this.container = container;
		this.cols = cols;
		this.rows = rows;
		layout = new CargoGridLayout(cols, rows);
		slots = new CargoGridSlotWidget[cols * rows];

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int index = row * cols + col;
				CargoGridSlotWidget slot = new CargoGridSlotWidget(
						this,
						container,
						index,
						col * SLOT_SIZE,
						row * SLOT_SIZE
				);
				slot.setLocationInfo(false, false);
				slots[index] = slot;
				addWidget(slot);
			}
		}
	}

	public CargoGridLayout snapshotLayout() {
		layout.recompute(container);
		return layout;
	}

	public CargoGridSlotWidget slotAt(int index) {
		if (index < 0 || index >= slots.length) {
			return null;
		}
		return slots[index];
	}

	@Override
	public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		layout.recompute(container);
		drawSlotBackgrounds(graphics);
		super.drawInBackground(graphics, mouseX, mouseY, partialTicks);

		Position pos = getPosition();
		RenderSystem.enableBlend();
		for (int slot = 0; slot < cols * rows; slot++) {
			ItemStack stack = container.getItem(slot);
			if (stack.isEmpty()) continue;

			CargoGridDimensions dims = CargoGridRules.resolve(stack);
			int col = layout.colOf(slot);
			int row = layout.rowOf(slot);
			int spanW = Math.min(dims.width(), cols - col);
			int spanH = Math.min(dims.height(), rows - row);
			int drawW = spanW * SLOT_SIZE;
			int drawH = spanH * SLOT_SIZE;
			int drawX = pos.x + col * SLOT_SIZE;
			int drawY = pos.y + row * SLOT_SIZE;

			boolean isLarge = spanW > 1 || spanH > 1;
			int fill = fillColorFor(stack);
			int border = borderColorFor(stack);

			if (isLarge) {
				DrawerHelper.drawSolidRect(graphics, drawX + 1, drawY + 1, drawW - 2, drawH - 2, fill);
			}
			DrawerHelper.drawBorder(graphics, drawX + 1, drawY + 1, drawW - 2, drawH - 2, border, 1);

			if (isLarge) {
				drawScaledItem(graphics, stack, drawX, drawY, drawW, drawH);
			}
		}
	}

	@Override
	public void drawInForeground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
		drawPlacementPreview(graphics, mouseX, mouseY);
	}

	private void drawPlacementPreview(GuiGraphics graphics, int mouseX, int mouseY) {
		if (gui == null) {
			return;
		}
		if (!isMouseOverElement(mouseX, mouseY)) {
			return;
		}
		ItemStack carried = gui.getModularUIContainer().getCarried();
		if (carried.isEmpty()) {
			return;
		}

		Position pos = getPosition();
		int relX = mouseX - pos.x;
		int relY = mouseY - pos.y;
		int hoveredCol = relX / SLOT_SIZE;
		int hoveredRow = relY / SLOT_SIZE;
		if (isBoolean(hoveredCol, hoveredRow)) {
			return;
		}
		int hovered = hoveredRow * cols + hoveredCol;

		layout.recompute(container);
		CargoGridDimensions dims = CargoGridRules.resolve(carried);
		int anchor = layout.findAnchor(hovered, dims);
		boolean canPlace = anchor >= 0;
		int targetAnchor = canPlace ? anchor : hovered;

		int anchorCol = layout.colOf(targetAnchor);
		int anchorRow = layout.rowOf(targetAnchor);
		int spanW = Math.min(dims.width(), cols - anchorCol);
		int spanH = Math.min(dims.height(), rows - anchorRow);
		int previewW = spanW * SLOT_SIZE;
		int previewH = spanH * SLOT_SIZE;
		int previewX = pos.x + anchorCol * SLOT_SIZE;
		int previewY = pos.y + anchorRow * SLOT_SIZE;

		int fillColor = canPlace ? PREVIEW_FILL_OK : PREVIEW_FILL_BAD;
		int borderColor = canPlace ? PREVIEW_BORDER_OK : PREVIEW_BORDER_BAD;
		DrawerHelper.drawSolidRect(graphics, previewX + 1, previewY + 1, previewW - 2, previewH - 2, fillColor);
		DrawerHelper.drawBorder(graphics, previewX + 1, previewY + 1, previewW - 2, previewH - 2, borderColor, 1);
	}

	private boolean isBoolean(int hoveredCol, int hoveredRow) {
		return hoveredCol < 0 || hoveredCol >= cols || hoveredRow < 0 || hoveredRow >= rows;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (keyCode == GLFW.GLFW_KEY_R && gui != null) {
			ItemStack carried = gui.getModularUIContainer().getCarried();
			if (!carried.isEmpty()) {
				CargoGridDimensions dims = CargoGridRules.resolve(carried);
				if (dims.width() != dims.height()) {
					CmiNetwork.CHANNEL.sendToServer(new RotateCarriedItemPacket());
					return true;
				}
			}
		}
		return false;
	}

	private void drawSlotBackgrounds(GuiGraphics graphics) {
		Position pos = getPosition();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int sx = pos.x + col * SLOT_SIZE;
				int sy = pos.y + row * SLOT_SIZE;
				DrawerHelper.drawSolidRect(graphics, sx + 1, sy + 1, SLOT_SIZE - 2, SLOT_SIZE - 2, SLOT_INNER);
				DrawerHelper.drawSolidRect(graphics, sx, sy, SLOT_SIZE - 1, 1, SLOT_SHADOW);
				DrawerHelper.drawSolidRect(graphics, sx, sy, 1, SLOT_SIZE - 1, SLOT_SHADOW);
				DrawerHelper.drawSolidRect(graphics, sx + 1, sy + SLOT_SIZE - 1, SLOT_SIZE - 1, 1, SLOT_HIGHLIGHT);
				DrawerHelper.drawSolidRect(graphics, sx + SLOT_SIZE - 1, sy + 1, 1, SLOT_SIZE - 1, SLOT_HIGHLIGHT);
			}
		}
	}

	private static void drawScaledItem(GuiGraphics graphics, ItemStack stack, int x, int y, int boxW, int boxH) {
		int shortSide = Math.min(boxW, boxH);
		float scale = Math.max((shortSide - 2 * ICON_PADDING) / 16.0f, 1.0f);
		float drawnSize = 16.0f * scale;
		float iconX = x + (boxW - drawnSize) / 2.0f;
		float iconY = y + (boxH - drawnSize) / 2.0f;
		boolean rotated = CargoGridRules.isRotated(stack);

		PoseStack pose = graphics.pose();
		pose.pushPose();
		if (rotated) {
			pose.translate(x + boxW / 2.0f, y + boxH / 2.0f, 0);
			pose.mulPose(Axis.ZP.rotationDegrees(90));
			pose.scale(scale, scale, 1.0f);
			pose.translate(-8.0f, -8.0f, 0);
		} else {
			pose.translate(iconX, iconY, 0);
			pose.scale(scale, scale, 1.0f);
		}
		graphics.renderItem(stack, 0, 0);
		pose.popPose();

		pose.pushPose();
		pose.translate(x + boxW - 17, y + boxH - 17, 0);
		graphics.renderItemDecorations(Minecraft.getInstance().font, stack, 0, 0);
		pose.popPose();
	}

	private static int fillColorFor(ItemStack stack) {
		Integer custom = CargoGridRules.resolveColor(stack);
		if (custom != null) {
			return darken(custom);
		}
		return rarityFill(stack.getRarity());
	}

	private static int borderColorFor(ItemStack stack) {
		Integer custom = CargoGridRules.resolveColor(stack);
		if (custom != null) return custom;
		return rarityBorder(stack.getRarity());
	}

	private static int rarityFill(Rarity rarity) {
		return switch (rarity) {
			case UNCOMMON -> 0xFF6B6432;
			case RARE -> 0xFF324B6B;
			case EPIC -> 0xFF503264;
			default -> 0xFF606060;
		};
	}

	private static int rarityBorder(Rarity rarity) {
		return switch (rarity) {
			case UNCOMMON -> 0xFFFFD000;
			case RARE -> 0xFF22AAFF;
			case EPIC -> 0xFFCC22EE;
			default -> 0xFFBBBBBB;
		};
	}

	private static int darken(int argb) {
		int a = (argb >>> 24) & 0xFF;
		int r = (((argb >> 16) & 0xFF) << 1) / 5;
		int g = (((argb >> 8) & 0xFF) << 1) / 5;
		int b = ((argb & 0xFF) << 1) / 5;
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}