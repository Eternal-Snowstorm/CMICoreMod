package dev.celestiacraft.cmi.common.feature.cargogrid;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class CargoGridLayout {
	public static final int EMPTY = -1;

	@Getter
	private final int cols;
	@Getter
	private final int rows;
	@Getter
	private final int[] occupiedBy;

	public CargoGridLayout(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		occupiedBy = new int[cols * rows];
	}

	public int ownerOf(int slot) {
		return slot < 0 || slot >= occupiedBy.length ? EMPTY : occupiedBy[slot];
	}

	public int indexOf(int col, int row) {
		return row * cols + col;
	}

	public int colOf(int slot) {
		return slot % cols;
	}

	public int rowOf(int slot) {
		return slot / cols;
	}

	public void recompute(Container container) {
		java.util.Arrays.fill(occupiedBy, EMPTY);
		int total = Math.min(container.getContainerSize(), occupiedBy.length);
		for (int slot = 0; slot < total; slot++) {
			ItemStack stack = container.getItem(slot);
			if (stack.isEmpty()) {
				continue;
			}
			CargoGridDimensions dims = CargoGridRules.resolve(stack);
			markFootprint(slot, dims);
		}
	}

	private void markFootprint(int anchorSlot, CargoGridDimensions dims) {
		int anchorCol = colOf(anchorSlot);
		int anchorRow = rowOf(anchorSlot);
		for (int dy = 0; dy < dims.height(); dy++) {
			int row = anchorRow + dy;
			if (row >= rows) {
				break;
			}
			for (int dx = 0; dx < dims.width(); dx++) {
				int col = anchorCol + dx;
				if (col >= cols) break;
				int idx = indexOf(col, row);
				if (occupiedBy[idx] == EMPTY) {
					occupiedBy[idx] = anchorSlot;
				}
			}
		}
	}

	public boolean canPlace(int anchorSlot, CargoGridDimensions dims, int ignoreOwner) {
		int anchorCol = colOf(anchorSlot);
		int anchorRow = rowOf(anchorSlot);
		if (anchorCol + dims.width() > cols) return false;
		if (anchorRow + dims.height() > rows) return false;
		for (int dy = 0; dy < dims.height(); dy++) {
			for (int dx = 0; dx < dims.width(); dx++) {
				int idx = indexOf(anchorCol + dx, anchorRow + dy);
				int owner = occupiedBy[idx];
				if (owner == EMPTY) {
					continue;
				}
				if (owner == anchorSlot) {
					continue;
				}
				if (owner == ignoreOwner) {
					continue;
				}
				return false;
			}
		}
		return true;
	}

	public int findAnchor(int clickedSlot, CargoGridDimensions dims) {
		int clickedCol = colOf(clickedSlot);
		int clickedRow = rowOf(clickedSlot);
		int prefCol = clamp(clickedCol - (dims.width() - 1) / 2, 0, cols - dims.width());
		int prefRow = clamp(clickedRow - (dims.height() - 1) / 2, 0, rows - dims.height());
		int preferred = indexOf(prefCol, prefRow);
		if (canPlace(preferred, dims, EMPTY)) {
			return preferred;
		}

		int best = -1;
		int bestDist = Integer.MAX_VALUE;
		int maxCol = cols - dims.width();
		int maxRow = rows - dims.height();
		for (int r = 0; r <= maxRow; r++) {
			for (int c = 0; c <= maxCol; c++) {
				int idx = indexOf(c, r);
				if (canPlace(idx, dims, EMPTY)) {
					int dist = Math.abs(c - prefCol) + Math.abs(r - prefRow);
					if (dist < bestDist) {
						bestDist = dist;
						best = idx;
					}
				}
			}
		}
		return best;
	}

	private static int clamp(int v, int lo, int hi) {
		if (hi < lo) return lo;
		return Math.max(lo, Math.min(hi, v));
	}
}