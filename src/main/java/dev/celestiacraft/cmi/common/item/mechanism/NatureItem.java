package dev.celestiacraft.cmi.common.item.mechanism;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class NatureItem extends MechanismItem {
	public NatureItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}

	@Override
	protected InteractionResult onMechanismUseOn(UseOnContext context) {
		return useOtherItem(ACItemRegistry.FERTILIZER.get(), context);
	}

	@Override
	protected int getCooldownTicks() {
		return 5 + RANDOM.nextInt(6);
	}
}