package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.libs.compat.curios.CuriosContext;
import dev.celestiacraft.libs.compat.curios.ICuriosHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class NetherItem extends MechanismItem implements ICuriosHelper {
	public NetherItem(Properties properties) {
		super(properties);
	}

	@Override
	public void curiosTick(CuriosContext context) {
		context.player.addEffect(new MobEffectInstance(
				MobEffects.FIRE_RESISTANCE,
				20 * 20,
				0,
				false,
				false
		));
	}

	@Override
	public int tickCheck() {
		return 20 * 10;
	}
}