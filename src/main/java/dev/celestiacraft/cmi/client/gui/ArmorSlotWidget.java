package dev.celestiacraft.cmi.client.gui;

import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ArmorSlotWidget extends SlotWidget {
	private static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
	private static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
	private static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
	private static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
	private static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_shield");

	private final EquipmentSlot equipmentSlot;
	private final ResourceLocation emptyIcon;

	public ArmorSlotWidget(Inventory inventory, int slotIndex, int xPosition, int yPosition, EquipmentSlot equipmentSlot) {
		super(inventory, slotIndex, xPosition, yPosition, true, true);
		this.equipmentSlot = equipmentSlot;
		this.emptyIcon = resolveEmptyIcon(equipmentSlot);
	}

	private static ResourceLocation resolveEmptyIcon(EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> EMPTY_ARMOR_SLOT_HELMET;
			case CHEST -> EMPTY_ARMOR_SLOT_CHESTPLATE;
			case LEGS -> EMPTY_ARMOR_SLOT_LEGGINGS;
			case FEET -> EMPTY_ARMOR_SLOT_BOOTS;
			case OFFHAND -> EMPTY_ARMOR_SLOT_SHIELD;
			default -> null;
		};
	}

	@Override
	public boolean canPutStack(ItemStack stack) {
		if (!super.canPutStack(stack)) {
			return false;
		}
		if (equipmentSlot == EquipmentSlot.OFFHAND) {
			return true;
		}
		if (stack.isEmpty()) {
			return true;
		}
		return Mob.getEquipmentSlotForItem(stack) == equipmentSlot;
	}

	@Override
	public boolean canMergeSlot(ItemStack stack) {
		if (!super.canMergeSlot(stack)) {
			return false;
		}
		if (equipmentSlot == EquipmentSlot.OFFHAND) {
			return true;
		}
		return Mob.getEquipmentSlotForItem(stack) == equipmentSlot;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
		if (emptyIcon == null) {
			return;
		}
		if (slotReference == null || !slotReference.getItem().isEmpty()) {
			return;
		}
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(emptyIcon);
		graphics.blit(getPosition().x + 1, getPosition().y + 1, 0, 16, 16, sprite);
	}
}
