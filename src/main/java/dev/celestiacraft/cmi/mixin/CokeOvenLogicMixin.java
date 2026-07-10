package dev.celestiacraft.cmi.mixin;

import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.CokeOvenLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CokeOvenLogic.class, remap = false)
public class CokeOvenLogicMixin {
	@Redirect(
			method = "tickServer",
			at = @At(
					value = "INVOKE",
					target = "Lblusunrize/immersiveengineering/common/blocks/multiblocks/logic/CokeOvenLogic;getRecipe(Lblusunrize/immersiveengineering/api/multiblocks/blocks/env/IMultiblockContext;)Lblusunrize/immersiveengineering/api/crafting/CokeOvenRecipe;",
					ordinal = 0
			)
	)
	private CokeOvenRecipe cmi$guardRecipeCompletion(CokeOvenLogic instance, IMultiblockContext<CokeOvenLogic.State> context) {
		if (((CokeOvenStateAccessor) context.getState()).cmi$getProcessMax() <= 0) {
			return null;
		}
		return instance.getRecipe(context);
	}
}
