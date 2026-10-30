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

	// 钉死原版 4 档循环, 追加的 GRILLED 不参与
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