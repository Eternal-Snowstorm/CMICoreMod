package dev.celestiacraft.cmi.mixin.create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = BlazeBurnerBlock.HeatLevel.class, remap = false)
public abstract class HeatLevelMixin {
	@SuppressWarnings("target")
	@Shadow
	@Final
	@Mutable
	private static BlazeBurnerBlock.HeatLevel[] $VALUES;

	@Unique
	private static final BlazeBurnerBlock.HeatLevel CMI$GRILLED = cmi$addVariant("GRILLED");

	@Invoker("<init>")
	public static BlazeBurnerBlock.HeatLevel cmi$invokeInit(String name, int id) {
		throw new AssertionError();
	}

	@Unique
	private static BlazeBurnerBlock.HeatLevel cmi$addVariant(String name) {
		ArrayList<BlazeBurnerBlock.HeatLevel> variants = new ArrayList<>(Arrays.asList(HeatLevelMixin.$VALUES));
		BlazeBurnerBlock.HeatLevel heatLevel = cmi$invokeInit(name, variants.get(variants.size() - 1).ordinal() + 1);
		variants.add(heatLevel);
		HeatLevelMixin.$VALUES = variants.toArray(new BlazeBurnerBlock.HeatLevel[0]);
		return heatLevel;
	}

	/**
	 * 修掉 "追加枚举常量" 带来的循环错位
	 *
	 * <p>
	 * 原版实现是 {@code byIndex(ordinal() % (values().length - 1) + 1)}, 把 GRILLED 追加到末尾后
	 * {@code values().length} 从 5 变成 6, 于是 SEETHING(4) 会算到 {@code byIndex(5) = GRILLED},
	 * 本该循环回 SMOULDERING 的 "满火 -> 熄火" 断了. 触发点在
	 * {@code BlazeBurnerBlockEntity.applyCreativeFuel()} (创造模式喂烈焰蛋糕时).
	 * </p>
	 *
	 * <p>
	 * 这里把参与循环的档位数钉死为原版的 4 档(SMOULDERING / FADING / KINDLED / SEETHING);
	 * 追加出来的常量(GRILLED)不参与循环, 直接回到最弱燃烧档.
	 * </p>
	 */
	@Inject(method = "nextActiveLevel", at = @At("HEAD"), remap = false, cancellable = true)
	private void cmi$nextActiveLevel(CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
		BlazeBurnerBlock.HeatLevel self = (BlazeBurnerBlock.HeatLevel) (Object) this;
		int ordinal = self.ordinal();
		int vanillaActiveLevels = 4;

		if (ordinal > vanillaActiveLevels) {
			cir.setReturnValue(BlazeBurnerBlock.HeatLevel.SMOULDERING);
			return;
		}

		cir.setReturnValue(BlazeBurnerBlock.HeatLevel.values()[ordinal % vanillaActiveLevels + 1]);
	}
}