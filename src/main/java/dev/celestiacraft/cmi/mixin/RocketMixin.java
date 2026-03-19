package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.compat.adastra.AdAstraSpaceElevatorCompat;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Rocket.class, remap = false)
public class RocketMixin {
	@Inject(method = "launch", at = @At("HEAD"), remap = false)
	private void cmi$recordLaunchOrigin(CallbackInfo ci) {
		Rocket rocket = (Rocket) (Object) this;
		if (!(rocket.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!(rocket.getControllingPassenger() instanceof ServerPlayer player)) {
			return;
		}

		AdAstraSpaceElevatorCompat.clearLastLaunchOrigin(player);
		AdAstraSpaceElevatorCompat.recordLaunchOrigin(player, rocket.blockPosition(), serverLevel.dimension());
	}
}
