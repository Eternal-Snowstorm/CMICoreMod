package dev.celestiacraft.cmi.common.item.tool.crafting_table;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.NotNull;

public class CustomCraftingTableContainer extends CraftingMenu {
	public CustomCraftingTableContainer(int id, Inventory inventory, ContainerLevelAccess access) {
		super(id, inventory, access);
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
}