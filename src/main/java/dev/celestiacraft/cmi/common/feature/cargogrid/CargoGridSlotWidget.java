package dev.celestiacraft.cmi.common.feature.cargogrid;

import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class CargoGridSlotWidget extends SlotWidget {
	private static final int SLOT_CENTER = 9;
	private static boolean isForwarding = false;

	private final Container container;
	private final int slotIndex;
	private final CargoGridWidget owner;

	public CargoGridSlotWidget(CargoGridWidget owner, Container container, int slotIndex, int x, int y) {
		super(container, slotIndex, x, y, true, true);
		this.owner = owner;
		this.container = container;
		this.slotIndex = slotIndex;
		setItemHook(this::hideLargeAnchor);
	}

	private ItemStack hideLargeAnchor(ItemStack stack) {
		if (stack.isEmpty()) return stack;
		CargoGridDimensions dims = CargoGridRules.resolve(stack);
		if (dims.width() > 1 || dims.height() > 1) {
			return ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public boolean canPutStack(ItemStack stack) {
		if (!super.canPutStack(stack)) {
			return false;
		}
		if (stack.isEmpty()) {
			return true;
		}
		CargoGridLayout layout = owner.snapshotLayout();
		int currentOwner = layout.ownerOf(slotIndex);
		if (currentOwner != CargoGridLayout.EMPTY && currentOwner != slotIndex) {
			return false;
		}

		CargoGridDimensions dims = CargoGridRules.resolve(stack);
		return layout.canPlace(slotIndex, dims, slotIndex);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!isMouseOverElement(mouseX, mouseY)) {
			return false;
		}
		if (isForwarding) {
			return super.mouseClicked(mouseX, mouseY, button);
		}

		CargoGridSlotWidget anchorSlot = findOccupyingAnchor();
		if (anchorSlot != null) {
			return forwardClick(anchorSlot, button);
		}

		CargoGridSlotWidget redirect = findCarriedPlacementAnchor();
		if (redirect != null && redirect != this) {
			return forwardClick(redirect, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (!isMouseOverElement(mouseX, mouseY)) {
			return false;
		}
		if (isForwarding) {
			return super.mouseReleased(mouseX, mouseY, button);
		}

		CargoGridSlotWidget anchorSlot = findOccupyingAnchor();
		if (anchorSlot != null) {
			return forwardRelease(anchorSlot, button);
		}

		CargoGridSlotWidget redirect = findCarriedPlacementAnchor();
		if (redirect != null && redirect != this) {
			return forwardRelease(redirect, button);
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void drawInForeground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (isMouseOverElement(mouseX, mouseY)) {
			CargoGridSlotWidget anchorSlot = findOccupyingAnchor();
			if (anchorSlot != null) {
				Position p = anchorSlot.getPosition();
				anchorSlot.drawInForeground(graphics, p.x + SLOT_CENTER, p.y + SLOT_CENTER, partialTicks);
				return;
			}
		}
		super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
	}

	private boolean forwardClick(CargoGridSlotWidget target, int button) {
		Position p = target.getPosition();
		isForwarding = true;
		try {
			return target.mouseClicked(p.x + SLOT_CENTER, p.y + SLOT_CENTER, button);
		} finally {
			isForwarding = false;
		}
	}

	private boolean forwardRelease(CargoGridSlotWidget target, int button) {
		Position p = target.getPosition();
		isForwarding = true;
		try {
			return target.mouseReleased(p.x + SLOT_CENTER, p.y + SLOT_CENTER, button);
		} finally {
			isForwarding = false;
		}
	}

	private CargoGridSlotWidget findOccupyingAnchor() {
		CargoGridLayout layout = owner.snapshotLayout();
		int ownerIdx = layout.ownerOf(slotIndex);
		if (ownerIdx == CargoGridLayout.EMPTY || ownerIdx == slotIndex) {
			return null;
		}
		return owner.slotAt(ownerIdx);
	}

	private CargoGridSlotWidget findCarriedPlacementAnchor() {
		if (gui == null) {
			return null;
		}
		ItemStack carried = gui.getModularUIContainer().getCarried();
		if (carried.isEmpty()) {
			return null;
		}
		CargoGridDimensions dims = CargoGridRules.resolve(carried);
		if (dims.width() <= 1 && dims.height() <= 1) {
			return null;
		}
		CargoGridLayout layout = owner.snapshotLayout();
		int target = layout.findAnchor(slotIndex, dims);
		if (target < 0 || target == slotIndex) {
			return null;
		}
		return owner.slotAt(target);
	}
}