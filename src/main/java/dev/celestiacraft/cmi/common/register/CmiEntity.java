package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.dev.qi_month.QiMonthEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraft.world.entity.MobCategory;

public class CmiEntity {
	public static final EntityEntry<QiMonthEntity> QI_MONTH;
	public static final EntityEntry<SpaceElevatorEntity> SPACE_ELEVATOR;

	static {
		QI_MONTH = Cmi.REGISTRATE.entity("qi_month", QiMonthEntity::new, MobCategory.CREATURE)
				.properties((builder) -> {
					builder.sized(0.6f, 1.8f);
				})
				.register();
		SPACE_ELEVATOR = Cmi.REGISTRATE.entity("space_elevator", SpaceElevatorEntity::new, MobCategory.MISC)
				.properties((builder) -> {
					builder.sized(3, 1.5f);
				})
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Entities Registered!");
	}
}