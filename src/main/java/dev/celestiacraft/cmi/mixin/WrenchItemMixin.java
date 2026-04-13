package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.tag.ModItemTags;
import earth.terrarium.adastra.common.items.WrenchItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WrenchItem.class)
public class WrenchItemMixin {
	@Inject(
			method = "useOn",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cmi$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> returnable) {
		Player player = context.getPlayer();
		if (player == null) {
			return;
		}

		ItemStack stack = player.getMainHandItem();

		if (!stack.is(ModItemTags.WRENCHES)) {
			returnable.setReturnValue(InteractionResult.PASS);
		}
	}
}