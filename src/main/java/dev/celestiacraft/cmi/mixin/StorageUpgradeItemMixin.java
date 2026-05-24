package dev.celestiacraft.cmi.mixin;

import com.buuz135.functionalstorage.block.config.FunctionalStorageConfig;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.buuz135.functionalstorage.item.UpgradeItem;
import com.hrznstudio.titanium.item.BasicItem;
import dev.celestiacraft.cmi.compat.storage.CmiDrawerUpgradeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

@Mixin(value = StorageUpgradeItem.class, remap = false)
public abstract class StorageUpgradeItemMixin {

	@Shadow
	public abstract StorageUpgradeItem.StorageTier getStorageTier();

	@Inject(method = "addTooltipDetails", at = @At("HEAD"), cancellable = true)
	private void cmi$additiveTooltip(BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced, CallbackInfo ci) {
		if (!CmiDrawerUpgradeHelper.isAdditiveUpgrade()) {
			return;
		}

		tooltip.add(Component.translatable("upgrade.type").withStyle(ChatFormatting.YELLOW)
				.append(Component.translatable("upgrade.type." + UpgradeItem.Type.STORAGE.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.WHITE)));

		StorageUpgradeItem.StorageTier tier = getStorageTier();
		if (tier == StorageUpgradeItem.StorageTier.IRON) {
			tooltip.add(Component.translatable("item.utility.downgrade").withStyle(ChatFormatting.GRAY));
		} else {
			int multValue = FunctionalStorageConfig.getLevelMult(tier.getLevel());
			DecimalFormat formatter = new DecimalFormat();
			tooltip.add(Component.translatable("cmi.tooltip.storageupgrade.desc.item.additive", formatter.format(multValue)).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.translatable("cmi.tooltip.storageupgrade.desc.fluid.additive", formatter.format(multValue / FunctionalStorageConfig.FLUID_DIVISOR)).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.translatable("cmi.tooltip.storageupgrade.desc.range.additive", formatter.format(multValue / FunctionalStorageConfig.RANGE_DIVISOR)).withStyle(ChatFormatting.GRAY));
		}

		ci.cancel();
	}
}
