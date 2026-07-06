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
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.config.common.SpaceElevatorConfig;
import dev.celestiacraft.cmi.config.CommonConfig;
import dev.celestiacraft.cmi.feature.cargogrid.CargoGridWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class SpaceElevatorCargoUI {
	private static final int SLOT_SIZE = 18;
	private static final int CARGO_COLS = 10;
	private static final int CARGO_ROWS = 6;

	private static final int CARGO_X = 30;
	private static final int CARGO_Y = 22;

	private static final int TANK_WIDTH = 16;
	private static final int TANK_HEIGHT = CARGO_ROWS * SLOT_SIZE;
	private static final int TANK_X = CARGO_X + CARGO_COLS * SLOT_SIZE + 10;
	private static final int TANK_Y = CARGO_Y;

	private static final int ARMOR_X = 8;
	private static final int OFFHAND_X_OFFSET = 4;

	private static final int PLAYER_COLS = 9;
	private static final int CARGO_GRID_CENTER = CARGO_X + (CARGO_COLS * SLOT_SIZE) / 2;
	private static final int PLAYER_INV_X = CARGO_GRID_CENTER - (PLAYER_COLS * SLOT_SIZE) / 2;
	private static final int PLAYER_INV_Y = CARGO_Y + CARGO_ROWS * SLOT_SIZE + 18;
	private static final int PLAYER_HOTBAR_Y = PLAYER_INV_Y + 3 * SLOT_SIZE + 4;

	private static final int ARMOR_Y = PLAYER_INV_Y;
	private static final int OFFHAND_X = PLAYER_INV_X + PLAYER_COLS * SLOT_SIZE + OFFHAND_X_OFFSET;
	private static final int OFFHAND_Y = PLAYER_HOTBAR_Y;

	private static final int GUI_WIDTH = Math.max(TANK_X + TANK_WIDTH, OFFHAND_X + SLOT_SIZE) + 12;
	private static final int GUI_HEIGHT = PLAYER_HOTBAR_Y + SLOT_SIZE + 6;

	private static final int ARMOR_INDEX_BASE = 36;
	private static final int OFFHAND_INDEX = 40;

	private static final int LABEL_COLOR = 0x404040;
	private static final ColorBorderTexture TANK_BORDER = new ColorBorderTexture(1, 0xFF373737);

	private SpaceElevatorCargoUI() {}

	public static ModularUI create(SpaceElevatorEntity elevator, Player player) {
		WidgetGroup root = new WidgetGroup(0, 0, GUI_WIDTH, GUI_HEIGHT);
		root.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

		root.addWidget(new LabelWidget(CARGO_X, 8, Component.translatable("entity.cmi.space_elevator.cargo"))
				.setTextColor(LABEL_COLOR)
				.setDropShadow(false));

		if (CommonConfig.SPACE_ELEVATOR != null && SpaceElevatorConfig.ENABLE_CARGO_GRID.get()) {
			root.addWidget(new CargoGridWidget(CARGO_X, CARGO_Y, CARGO_COLS, CARGO_ROWS, elevator.getCargoItems()));
		} else {
			for (int row = 0; row < CARGO_ROWS; row++) {
				for (int col = 0; col < CARGO_COLS; col++) {
					int index = row * CARGO_COLS + col;
					int x = CARGO_X + col * SLOT_SIZE;
					int y = CARGO_Y + row * SLOT_SIZE;
					SlotWidget cargoSlot = new SlotWidget(elevator.getCargoItems(), index, x, y, true, true);
					cargoSlot.setLocationInfo(false, false);
					root.addWidget(cargoSlot);
				}
			}
		}

		FluidTank cargoFluid = elevator.getCargoFluid();
		TankWidget tank = new ScaledTankWidget(
				new FluidTransferWrapper(cargoFluid),
				0,
				TANK_X, TANK_Y,
				TANK_WIDTH, TANK_HEIGHT,
				false, false
		);
		tank.setShowAmount(false);
		tank.setDrawHoverTips(true);
		tank.setFillDirection(ProgressTexture.FillDirection.DOWN_TO_UP);
		tank.setBackground(TANK_BORDER);
		root.addWidget(tank);

		root.addWidget(new LabelWidget(PLAYER_INV_X, PLAYER_INV_Y - 10, Component.translatable("container.inventory"))
				.setTextColor(LABEL_COLOR)
				.setDropShadow(false));

		Inventory inventory = player.getInventory();

		EquipmentSlot[] armorSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
		for (int i = 0; i < 4; i++) {
			int invIndex = ARMOR_INDEX_BASE + (3 - i);
			ArmorSlotWidget armorSlot = new ArmorSlotWidget(inventory, invIndex, ARMOR_X, ARMOR_Y + i * SLOT_SIZE, armorSlots[i]);
			armorSlot.setLocationInfo(true, false);
			root.addWidget(armorSlot);
		}

		ArmorSlotWidget offhandSlot = new ArmorSlotWidget(inventory, OFFHAND_INDEX, OFFHAND_X, OFFHAND_Y, EquipmentSlot.OFFHAND);
		offhandSlot.setLocationInfo(true, false);
		root.addWidget(offhandSlot);

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				int index = 9 + row * 9 + col;
				int x = PLAYER_INV_X + col * SLOT_SIZE;
				int y = PLAYER_INV_Y + row * SLOT_SIZE;
				SlotWidget slot = new SlotWidget(inventory, index, x, y, true, true);
				slot.setLocationInfo(true, false);
				root.addWidget(slot);
			}
		}
		for (int col = 0; col < 9; col++) {
			int x = PLAYER_INV_X + col * SLOT_SIZE;
			SlotWidget slot = new SlotWidget(inventory, col, x, PLAYER_HOTBAR_Y, true, true);
			slot.setLocationInfo(true, true);
			root.addWidget(slot);
		}

		return new ModularUI(GUI_WIDTH, GUI_HEIGHT, elevator, player)
				.widget(root);
	}
}
