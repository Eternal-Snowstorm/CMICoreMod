package dev.celestiacraft.cmi.mixin.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import dev.celestiacraft.cmi.common.grill.CmiGrillSources;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让工作盆认 GRILLED 热源
 *
 * <p>
 * 判定顺序是这里唯一重要的东西:
 * </p>
 *
 * <ol>
 *   <li>方块自带 {@code HEAT_LEVEL} 属性(烈焰人燃烧室) —— 直接交回原版逻辑, 由属性决定 KINDLED / FADING / SEETHING;
 *       原来的实现是先判 {@code cmi:grill_sources} 标签, 而该标签里含 {@code create:blaze_burner},
 *       于是燃烧室永远返回 GRILLED, 原版 HEATED / SUPERHEATED 配方全部失效.</li>
 *   <li>配置里声明为炙烤热源的方块 —— 返回 GRILLED</li>
 *   <li>其余交回原版逻辑(PASSIVE_BOILER_HEATERS / NONE)</li>
 * </ol>
 */
@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {
	@Inject(method = "getHeatLevelOf", at = @At("HEAD"), remap = false, cancellable = true)
	private static void cmi$getHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
		// 原版热源优先: 燃烧室的热等级必须从它自己的属性读, 不能被 GRILLED 截胡
		if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
			return;
		}

		if (CmiGrillSources.isGrillSource(state)) {
			cir.setReturnValue(BlazeBurnerBlock.HeatLevel.valueOf("GRILLED"));
		}
	}
}
