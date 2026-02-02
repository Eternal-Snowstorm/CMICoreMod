package top.nebula.cmi.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.nebula.cmi.tag.ModBlockTags;

@Mixin(value = BasinBlockEntity.class, remap = false)
public abstract class BasinBlockEntityMixin {
	@Inject(method = "getHeatLevelOf", at = @At("HEAD"), remap = false, cancellable = true)
	private static void getHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
		if (state.is(ModBlockTags.GRILL_SOURCES)) {
			cir.setReturnValue(BlazeBurnerBlock.HeatLevel.valueOf("GRILLED"));
		}

		if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
			cir.setReturnValue(state.getValue(BlazeBurnerBlock.HEAT_LEVEL));
		}
	}
}