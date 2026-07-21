package dev.celestiacraft.cmi.mixin;

import com.simibubi.create.AllBlocks;

import dev.celestiacraft.cmi.config.common.BacktankConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import snownee.jade.addon.create.BacktankProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@Pseudo
@Mixin(value = BacktankProvider.class, remap = false)
public class BacktankJadeProviderMixin {
	@Redirect(
			method = "appendTooltip",
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/equipment/armor/BacktankUtil;maxAir(I)I",
					remap = false
			),
			remap = false
	)
	private int cmi$maxAir(int enchantLevel, ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		boolean netherite = AllBlocks.NETHERITE_BACKTANK.has(accessor.getBlockState());
		return BacktankConfig.maxAir(netherite, enchantLevel);
	}
}
