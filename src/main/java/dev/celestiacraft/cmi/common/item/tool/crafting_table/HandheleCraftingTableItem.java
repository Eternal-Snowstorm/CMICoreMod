package dev.celestiacraft.cmi.common.item.tool.crafting_table;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HandheleCraftingTableItem extends Item {
	public HandheleCraftingTableItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide()) {
			player.openMenu(createScreenHandlerFactory(level, player.getOnPos()));
		}
		return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
	}

	private MenuProvider createScreenHandlerFactory(Level level, BlockPos pos) {
		return new SimpleMenuProvider((id, inventory, player) -> {
			return new CustomCraftingTableContainer(
					id,
					inventory,
					ContainerLevelAccess.create(level, pos)
			);
		}, Component.translatable("container.crafting"));
	}
}