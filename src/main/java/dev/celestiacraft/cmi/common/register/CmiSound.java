package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CmiSound {
	private static final DeferredRegister<SoundEvent> SOUND_EVENTS;

	public static final Supplier<SoundEvent>
			DING;

	static {
		SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Cmi.MODID);

		DING = register("meme.ding");
	}

	private static Supplier<SoundEvent> register(String name) {
		return SOUND_EVENTS.register(name, () -> {
			return SoundEvent.createVariableRangeEvent(Cmi.loadResource(name));
		});
	}

	public static void register(IEventBus bus) {
		Cmi.LOGGER.info("{} SoundEvents Registered!", Cmi.NAME);
		SOUND_EVENTS.register(bus);
	}
}