package dev.celestiacraft.cmi.api.mbd2;

import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;

/**
 * 放大版玩家物品栏。
 *
 * <p>为什么不用 LDLib 的 {@code PlayerInventoryWidget}: 它是 172x86 / 18 槽距硬编码的, 整体放大时
 * 槽位和间距不会跟着变。这份按 scale 生成同样布局的槽位 (0-8 快捷栏, 9-35 背包, 与原版顺序一致,
 * 所以 index 能直接对上玩家背包槽)。
 *
 * <p>放在顶层而不是 {@code UISpec$Builder} 里: 嵌套两层会被一些工具/编译器嫌弃。
 */
public class ScaledPlayerInventory extends WidgetGroup {
	public ScaledPlayerInventory(float scale) {
		super(0, 0, Math.round(UISpec.PLAYER_INV_WIDTH * scale), Math.round(UISpec.PLAYER_INV_HEIGHT * scale));

		int size = Math.round(UISpec.SLOT * scale);

		for (int i = 0; i < 36; i++) {
			SlotWidget slot = new SlotWidget();

			slot.initTemplate();
			slot.setSize(size, size);
			slot.setSelfPosition(new Position(
					Math.round((5 + (i % 9) * UISpec.SLOT) * scale),
					Math.round((i < 9 ? 63 : 5 + ((float) i / 9 - 1) * UISpec.SLOT) * scale)
			));
			slot.setId("player_inv_" + i);

			addWidget(slot);
		}
	}

	@Override
	public void initWidget() {
		super.initWidget();

		for (int i = 0; i < widgets.size(); i++) {
			if (widgets.get(i) instanceof SlotWidget slot) {
				slot.setContainerSlot(gui.entityPlayer.getInventory(), i);
				slot.setLocationInfo(true, i < 9);
			}
		}
	}
}