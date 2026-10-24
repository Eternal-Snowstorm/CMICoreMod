package dev.celestiacraft.cmi.mixin.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.celestiacraft.cmi.tags.CmiBlockTags;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {
	@Inject(method = "getHeatLevelOf", at = @At("HEAD"), remap = false, cancellable = true)
	private static void getHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
		// 烈焰人燃烧室也算 GRILLED, 优先于下面按方块状态取值
		if (state.is(CmiBlockTags.GRILL_SOURCES)) {
			cir.setReturnValue(BlazeBurnerBlock.HeatLevel.valueOf("GRILLED"));
			return;
		}

		if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
			cir.setReturnValue(state.getValue(BlazeBurnerBlock.HEAT_LEVEL));
		}
	}
}