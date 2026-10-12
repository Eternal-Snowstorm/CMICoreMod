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
		if (state.is(CmiBlockTags.GRILL_SOURCES)) {
			cir.setReturnValue(BlazeBurnerBlock.HeatLevel.valueOf("GRILLED"));
		}

		// TODO: 此 if 会覆盖上面的 GRILLED 分支, 烈焰人燃烧室 (create:blaze_burner / create:lit_blaze_burner) 是否也该算 GRILLED 待主开发人员确认
		if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
			cir.setReturnValue(state.getValue(BlazeBurnerBlock.HEAT_LEVEL));
		}
	}
}