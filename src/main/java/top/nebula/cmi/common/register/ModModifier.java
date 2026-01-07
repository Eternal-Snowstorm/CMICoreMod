package top.nebula.cmi.common.register;

import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.compat.tconstruct.modifier.Acupoint;
import top.nebula.cmi.compat.tconstruct.modifier.Frenzy;

public class ModModifier {
	public static final ModifierDeferredRegister MODIFIERS;
	public static final StaticModifier<Acupoint> ACUPOINT;
	public static final StaticModifier<Frenzy> FRENZY;

	static {
		MODIFIERS = ModifierDeferredRegister.create(Cmi.MODID);

		ACUPOINT = MODIFIERS.register("acupoint", Acupoint::new);
		FRENZY = MODIFIERS.register("frenzy", Frenzy::new);
	}
}