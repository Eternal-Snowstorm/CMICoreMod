package top.nebula.cmi.mixin;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = BlazeBurnerBlock.HeatLevel.class, remap = false)
public abstract class HeatLevelMixin {
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
}