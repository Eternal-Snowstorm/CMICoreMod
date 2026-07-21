package dev.celestiacraft.cmi.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;

import dev.celestiacraft.cmi.config.common.BacktankConfig;

import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BacktankBlockEntity.class)
public abstract class BacktankBlockEntityMixin {
	@Redirect(
			method = {"tick", "getComparatorOutput", "read"},
			at = @At(
					value = "INVOKE",
					target = "Lcom/simibubi/create/content/equipment/armor/BacktankUtil;maxAir(I)I",
					remap = false
			),
			remap = false
	)
	private int cmi$maxAir(int enchantLevel) {
		boolean netherite = AllBlocks.NETHERITE_BACKTANK.has(((BlockEntity) (Object) this).getBlockState());
		return BacktankConfig.maxAir(netherite, enchantLevel);
	}
}
