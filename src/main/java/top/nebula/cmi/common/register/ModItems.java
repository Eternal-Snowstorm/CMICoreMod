package top.nebula.cmi.common.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.common.item.NuclearMechanismItem;
import top.nebula.cmi.common.item.TestBrushItem;

public class ModItems {
	public static final ItemEntry<NuclearMechanismItem> NUCLEAR_MECHANISM;
	public static final ItemEntry<TestBrushItem> TEST_BRUSH;

	static {
		Cmi.setCreativeTab(ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				ResourceLocation.parse("kubejs:tab")
		));

		TEST_BRUSH = Cmi.REGISTRATE.item("test_brush", TestBrushItem::new)
				.register();
		NUCLEAR_MECHANISM = Cmi.REGISTRATE.item("nuclear_mechanism", NuclearMechanismItem::new)
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Items Registered!");
	}
}