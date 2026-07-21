package dev.celestiacraft.cmi.mixin;

import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import dev.celestiacraft.cmi.config.common.BacktankConfig;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BacktankUtil.class, remap = false)
public class BacktankUtilMixin {
	@Inject(
			method = "maxAir(Lnet/minecraft/world/item/ItemStack;)I",
			at = @At("HEAD"),
			remap = false,
			cancellable = true
	)
	private static void cmi$maxAir(ItemStack backtank, CallbackInfoReturnable<Integer> cir) {
		boolean netherite = backtank.getItem() == AllItems.NETHERITE_BACKTANK.get();
		int enchantLevel = backtank.getEnchantmentLevel(AllEnchantments.CAPACITY.get());
		cir.setReturnValue(BacktankConfig.maxAir(netherite, enchantLevel));
	}

	@Inject(
			method = "maxAirWithoutEnchants()I",
			at = @At("HEAD"),
			remap = false,
			cancellable = true
	)
	private static void cmi$maxAirWithoutEnchants(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(BacktankConfig.maxAir(false, 0));
	}
}
