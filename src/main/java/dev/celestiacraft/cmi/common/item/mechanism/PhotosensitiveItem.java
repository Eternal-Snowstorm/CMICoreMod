package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.api.register.item.MechanismItem;
import dev.celestiacraft.libs.compat.curios.CuriosContext;
import dev.celestiacraft.libs.compat.curios.ICuriosHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class PhotosensitiveItem extends MechanismItem implements ICuriosHelper {
	public PhotosensitiveItem(Properties properties) {
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
				MobEffects.NIGHT_VISION,
				30 * 20,
				0,
				false,
				false
		));
	}
}