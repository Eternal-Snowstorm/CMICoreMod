package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiTextures;
import dev.celestiacraft.cmi.common.item.MysticPomeloItem;
import dev.celestiacraft.cmi.common.item.TestBrushItem;

public class CmiItem {
	public static final ItemEntry<TestBrushItem> TEST_BRUSH;
	public static final ItemEntry<MysticPomeloItem> MYSTIC_POMELO;

	static {
		TEST_BRUSH = Cmi.REGISTRATE.item("test_brush", TestBrushItem::new)
				.register();
		MYSTIC_POMELO = Cmi.REGISTRATE.item("mystic_pomelo", MysticPomeloItem::new)
				.model(CmiTextures.Items.setTexture("item/mystic_pomelo"))
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Items Registered!");
	}
}