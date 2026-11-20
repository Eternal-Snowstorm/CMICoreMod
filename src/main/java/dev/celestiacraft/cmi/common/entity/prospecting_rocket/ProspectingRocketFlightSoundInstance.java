package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

import earth.terrarium.adastra.common.registry.ModSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProspectingRocketFlightSoundInstance extends AbstractTickableSoundInstance {
	private final ProspectingRocketEntity rocket;

	public ProspectingRocketFlightSoundInstance(ProspectingRocketEntity rocket) {
		super(ModSoundEvents.ROCKET.get(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
		this.rocket = rocket;
		looping = true;
		delay = 0;
	}

	@Override
	public float getVolume() {
		return 10.0F;
	}

	@Override
	public void tick() {
		if (rocket.isRemoved() || !rocket.isRocketSoundActive()) {
			stop();
			return;
		}
		x = rocket.getX();
		y = rocket.getY();
		z = rocket.getZ();
	}
}