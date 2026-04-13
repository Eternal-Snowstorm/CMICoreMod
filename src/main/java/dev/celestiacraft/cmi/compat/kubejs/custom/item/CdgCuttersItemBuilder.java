package dev.celestiacraft.cmi.compat.kubejs.custom.item;

import com.jesz.createdieselgenerators.content.tools.wire_cutters.WireCuttersItem;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CdgCuttersItemBuilder extends ItemBuilder {
	public CdgCuttersItemBuilder(ResourceLocation location) {
		super(location);
	}

	public Item createObject() {
		return new WireCuttersItem(createItemProperties());
	}
}