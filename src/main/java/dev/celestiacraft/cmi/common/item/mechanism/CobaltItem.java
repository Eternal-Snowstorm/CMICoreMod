package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.api.register.item.MechanismItem;
import dev.celestiacraft.libs.compat.curios.CuriosContext;
import dev.celestiacraft.libs.compat.curios.ICuriosHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class CobaltItem extends MechanismItem implements ICuriosHelper {
	public CobaltItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}

	@Override
	public void onCuriosTick(CuriosContext context) {
		Player player = context.getPlayer();

		player.addEffect(new MobEffectInstance(
				MobEffects.MOVEMENT_SPEED,
				20 * 20,
				1,
				false,
				false
		));
		player.addEffect(new MobEffectInstance(
				MobEffects.DIG_SPEED,
				20 * 20,
				1,
				false,
				false
		));
	}

	@Override
	public int tickCheck() {
		return 20 * 10;
	}
}