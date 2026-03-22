package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.textures.Items;
import dev.celestiacraft.cmi.common.item.MysticPomeloItem;
import dev.celestiacraft.cmi.common.item.SimpleBatteryItem;
import dev.celestiacraft.cmi.common.item.TestBrushItem;

public class CmiItem {
	public static final ItemEntry<TestBrushItem> TEST_BRUSH;
	public static final ItemEntry<MysticPomeloItem> MYSTIC_POMELO;
	public static final ItemEntry<SimpleBatteryItem> SIMPLE_BATTERY;

	static {
		TEST_BRUSH = Cmi.REGISTRATE.item("test_brush", TestBrushItem::new)
				.register();
		MYSTIC_POMELO = Cmi.REGISTRATE.item("mystic_pomelo", MysticPomeloItem::new)
				.model(Items.generated("item/mystic_pomelo"))
				.register();
		SIMPLE_BATTERY = Cmi.REGISTRATE.item("simple_battery", SimpleBatteryItem::new)
				.model(Items.generated("item/simple_battery"))
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Items Registered!");
	}
}