package dev.celestiacraft.cmi.client.gui;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidTransferWrapper;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ProspectingRocketCargoUI {
	private static final int SLOT_SIZE = 18;
	private static final int MARGIN = 8;
	private static final int TANK_WIDTH = 16;
	private static final int TANK_GAP = 8;
	private static final int CARGO_Y = 20;
	private static final int PLAYER_COLS = 9;
	private static final int PLAYER_WIDTH = PLAYER_COLS * SLOT_SIZE;
	private static final int LABEL_COLOR = 0x404040;
	private static final ColorBorderTexture TANK_BORDER = new ColorBorderTexture(1, 0xFF373737);

	private ProspectingRocketCargoUI() {}

	public static ModularUI create(ProspectingRocketEntity rocket, Player player) {
		ProspectingRocketTier tier = rocket.getTier();
		int cols = tier.cargoColumns();
		int rows = tier.cargoRows();

		int cargoWidth = cols * SLOT_SIZE;
		int cargoHeight = rows * SLOT_SIZE;
		int topWidth = cargoWidth + TANK_GAP + TANK_WIDTH;
		int innerWidth = Math.max(topWidth, PLAYER_WIDTH);

		int topX = MARGIN + (innerWidth - topWidth) / 2;
		int playerX = MARGIN + (innerWidth - PLAYER_WIDTH) / 2;

		int playerInvY = CARGO_Y + cargoHeight + 16;
		int hotbarY = playerInvY + 3 * SLOT_SIZE + 4;

		int guiWidth = (MARGIN << 1) + innerWidth;
		int guiHeight = hotbarY + SLOT_SIZE + MARGIN;

		WidgetGroup root = new WidgetGroup(0, 0, guiWidth, guiHeight);
		root.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

		root.addWidget(new LabelWidget(topX, 8, Component.translatable("gui.cmi.prospecting_rocket.cargo"))
				.setTextColor(LABEL_COLOR)
				.setDropShadow(false));

		int fuelX = topX + cargoWidth + TANK_GAP;
		root.addWidget(new LabelWidget(fuelX - 1, 8, Component.translatable("gui.cmi.prospecting_rocket.fuel"))
				.setTextColor(LABEL_COLOR)
				.setDropShadow(false));

		addCargoSlots(root, rocket.getCargoItems(), cols, rows, topX, CARGO_Y);
		addFuelTank(root, rocket.getFuelTank(), fuelX, CARGO_Y, cargoHeight);

		root.addWidget(new LabelWidget(playerX, playerInvY - 10, Component.translatable("container.inventory"))
				.setTextColor(LABEL_COLOR)
				.setDropShadow(false));
		addPlayerInventory(root, player.getInventory(), playerX, playerInvY, hotbarY);

		return new ModularUI(guiWidth, guiHeight, rocket, player).widget(root);
	}

	private static void addCargoSlots(WidgetGroup root, Container container, int cols, int rows, int x, int y) {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int index = row * cols + col;
				SlotWidget slot = new SlotWidget(container, index, x + col * SLOT_SIZE, y + row * SLOT_SIZE, true, true);
				slot.setLocationInfo(false, false);
				root.addWidget(slot);
			}
		}
	}

	private static void addFuelTank(WidgetGroup root, FluidTank fuelTank, int x, int y, int height) {
		TankWidget tank = new ScaledTankWidget(new FluidTransferWrapper(fuelTank), 0, x, y, TANK_WIDTH, height, true, true);
		tank.setShowAmount(false);
		tank.setDrawHoverTips(true);
		tank.setFillDirection(ProgressTexture.FillDirection.DOWN_TO_UP);
		tank.setBackground(TANK_BORDER);
		root.addWidget(tank);
	}

	private static void addPlayerInventory(WidgetGroup root, Inventory inventory, int x, int invY, int hotbarY) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < PLAYER_COLS; col++) {
				int index = PLAYER_COLS + row * PLAYER_COLS + col;
				SlotWidget slot = new SlotWidget(inventory, index, x + col * SLOT_SIZE, invY + row * SLOT_SIZE, true, true);
				slot.setLocationInfo(true, false);
				root.addWidget(slot);
			}
		}
		for (int col = 0; col < PLAYER_COLS; col++) {
			SlotWidget slot = new SlotWidget(inventory, col, x + col * SLOT_SIZE, hotbarY, true, true);
			slot.setLocationInfo(true, true);
			root.addWidget(slot);
		}
	}
}
