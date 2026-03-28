package dev.celestiacraft.cmi.common.entity.space_elevator;

import earth.terrarium.adastra.common.registry.ModSoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class SpaceElevatorTravelSoundInstance extends AbstractTickableSoundInstance {
	private final SpaceElevatorEntity elevator;

	public SpaceElevatorTravelSoundInstance(SpaceElevatorEntity elevator) {
		super(ModSoundEvents.ROCKET.get(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
		this.elevator = elevator;
		this.looping = true;
		this.delay = 0;
	}

	@Override
	public float getVolume() {
		return 10.0F;
	}

	@Override
	public void tick() {
		if (elevator.isRemoved() || !elevator.isFlightSoundActive()) {
			stop();
			return;
		}
		x = elevator.getX();
		y = elevator.getY();
		z = elevator.getZ();
	}
}
