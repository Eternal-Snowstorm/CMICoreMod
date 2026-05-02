package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.tags.ModItemTags;
import earth.terrarium.adastra.common.blocks.base.Wrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
	@Inject(
			method = "useOn",
			at = @At("HEAD"),
			cancellable = true
	)
	private void cmi$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> returnable) {
		Player player = context.getPlayer();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		ItemStack stack = player.getMainHandItem();

		if (player == null) {
			return;
		}

		if (!stack.is(ModItemTags.WRENCHES)) {
			return;
		}

		Block block = state.getBlock();

		if (block instanceof Wrenchable wrenchable) {
			wrenchable.onWrench(
					level,
					pos,
					state,
					context.getClickedFace(),
					player,
					context.getClickLocation()
			);

			returnable.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}