package dev.celestiacraft.cmi.compat.ldlib;

import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import dev.latvian.mods.rhino.util.RemapForJS;
import lombok.experimental.UtilityClass;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@UtilityClass
public class LDLibHelpers {
	@RemapForJS("ofItemStackTextures")
	public ItemStackTexture ofItemTextures(ItemStack... stack) {
		return new ItemStackTexture(stack);
	}

	public ItemStackTexture ofItemTextures(Item... item) {
		return new ItemStackTexture(item);
	}
}