package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.qi_month.QiMonthEntity;
import net.minecraft.world.entity.MobCategory;

public class CmiEntity {
	public static final EntityEntry<QiMonthEntity> QI_MONTH;

	static {
		QI_MONTH = Cmi.REGISTRATE.entity("qi_month", QiMonthEntity::new, MobCategory.CREATURE)
				.properties((builder) -> {
					builder.sized(0.6F, 1.8F);
				})
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Entities Registered!");
	}
}