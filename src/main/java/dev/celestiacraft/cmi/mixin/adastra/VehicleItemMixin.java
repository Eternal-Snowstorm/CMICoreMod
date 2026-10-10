package dev.celestiacraft.cmi.mixin.adastra;

import earth.terrarium.adastra.common.items.vehicles.RoverItem;
import earth.terrarium.adastra.common.items.vehicles.VehicleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(VehicleItem.class)
public class VehicleItemMixin {
	@ModifyConstant(
			method = "getFluidContainer(Lnet/minecraft/world/item/ItemStack;)Learth/terrarium/botarium/common/fluid/impl/WrappedItemFluidContainer;",
			constant = @Constant(longValue = 3000L),
			remap = false
	)
	private long cmi$modifyFuelCapacity(long amount) {
		if ((Object) this instanceof RoverItem) {
			return 10000;
		}
		return amount;
	}
}