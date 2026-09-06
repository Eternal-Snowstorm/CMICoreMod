package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.modifier.diving.DivingModifier;
import dev.celestiacraft.cmi.common.modifier.extendo.ExtendoModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class CmiModifier {
	private static final ModifierDeferredRegister MODIFIERS;

	public static final StaticModifier<ExtendoModifier> EXTENDO;
	public static final StaticModifier<DivingModifier> DIVING;

	static {
		MODIFIERS = ModifierDeferredRegister.create(Cmi.MODID);

		EXTENDO = MODIFIERS.register("extendo", ExtendoModifier::new);
		DIVING = MODIFIERS.register(DivingModifier.ID, DivingModifier::new);
	}

	public static void register(IEventBus bus) {
		MODIFIERS.register(bus);
		Cmi.LOGGER.info("{} TCon Modifiers Registered!", Cmi.NAME);
	}
}