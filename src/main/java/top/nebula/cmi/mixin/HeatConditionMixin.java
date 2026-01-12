package top.nebula.cmi.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = HeatCondition.class, remap = false)
public abstract class HeatConditionMixin implements StringRepresentable {
	@Shadow
	@Final
	@Mutable
	private static HeatCondition[] $VALUES;

	@Shadow
	public abstract String getTranslationKey();

	@Unique
	private static final HeatCondition CMI$GRILLED = cmi$addVariant("GRILLED", 16747520);

	@Invoker("<init>")
	public static HeatCondition cmi$invokeInit(String name, int id, int color) {
		throw new AssertionError();
	}

	@Unique
	private static HeatCondition cmi$addVariant(String name, int color) {
		ArrayList<HeatCondition> variants = new ArrayList<>(Arrays.asList(HeatConditionMixin.$VALUES));
		if ($VALUES != null) {
			variants = new ArrayList<>(Arrays.asList($VALUES));
		}
		HeatCondition heatCondition = cmi$invokeInit(name, variants.get(variants.size() - 1).ordinal() + 1, color);
		variants.add(heatCondition);
		$VALUES = variants.toArray(new HeatCondition[0]);
		return heatCondition;
	}

	// TODO 需要修复一下热量检测, 现在的热量检测被我们mixin爆了
	@Inject(method = "testBlazeBurner", at = @At("HEAD"), remap = false, cancellable = true)
	public void testBlazeBurner(BlazeBurnerBlock.HeatLevel level, CallbackInfoReturnable<Boolean> cir) {
		if (this.equals(HeatCondition.SUPERHEATED)) {
			cir.setReturnValue(level == BlazeBurnerBlock.HeatLevel.SEETHING);
			return;
		}

		if (this.equals(HeatCondition.HEATED)) {
			cir.setReturnValue(level == BlazeBurnerBlock.HeatLevel.FADING || level == BlazeBurnerBlock.HeatLevel.KINDLED || level == BlazeBurnerBlock.HeatLevel.SEETHING);
			return;
		}

		if (this.equals(CMI$GRILLED)) {
			cir.setReturnValue(level == BlazeBurnerBlock.HeatLevel.valueOf("GRILLED") || level == BlazeBurnerBlock.HeatLevel.FADING ||
					level == BlazeBurnerBlock.HeatLevel.KINDLED || level == BlazeBurnerBlock.HeatLevel.SEETHING);
		}
	}

	@Inject(method = "visualizeAsBlazeBurner", at = @At("HEAD"), remap = false, cancellable = true)
	public void visualizeAsBlazeBurner(CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
		if ((HeatCondition) (Object) this == CMI$GRILLED) {
			cir.setReturnValue(BlazeBurnerBlock.HeatLevel.valueOf("GRILLED"));
		}
	}
}