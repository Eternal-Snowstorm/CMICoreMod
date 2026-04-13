package dev.celestiacraft.cmi.compat.kubejs.custom.item;

import com.jesz.createdieselgenerators.content.tools.hammer.HammerItem;
import dev.latvian.mods.kubejs.item.custom.HandheldItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CdgHammerItemBuilder extends HandheldItemBuilder {
	public CdgHammerItemBuilder(ResourceLocation location) {
		super(location, 0.0F, -2.0F);
	}

	@Override
	public Item createObject() {
		return new HammerItem(createItemProperties(), toolTier);
	}
}