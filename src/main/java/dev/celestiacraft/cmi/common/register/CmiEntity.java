package dev.celestiacraft.cmi.common.register;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.dev.qi_month.QiMonthEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketTier;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.EnumMap;
import java.util.Map;

public class CmiEntity {
	public static final EntityEntry<QiMonthEntity> QI_MONTH;
	public static final EntityEntry<SpaceElevatorEntity> SPACE_ELEVATOR;
	private static final Map<ProspectingRocketTier, EntityEntry<ProspectingRocketEntity>> PROSPECTING_ROCKETS = new EnumMap<>(ProspectingRocketTier.class);

	public static EntityEntry<ProspectingRocketEntity> prospectingRocket(ProspectingRocketTier tier) {
		return PROSPECTING_ROCKETS.get(tier);
	}

	static {
		for (ProspectingRocketTier tier : ProspectingRocketTier.values()) {
			EntityEntry<ProspectingRocketEntity> entry = Cmi.REGISTRATE
					.<ProspectingRocketEntity>entity(tier.registryName(), (type, level) -> {
						return new ProspectingRocketEntity(type, level, tier);
					}, MobCategory.MISC)
					.properties((builder) -> {
						builder.sized(tier.width(), tier.height())
								.clientTrackingRange(10)
								.updateInterval(3);
					})
					.register();
			PROSPECTING_ROCKETS.put(tier, entry);
		}

		QI_MONTH = Cmi.REGISTRATE.entity("qi_month", QiMonthEntity::new, MobCategory.CREATURE)
				.properties((builder) -> {
					builder.sized(0.6f, 1.8f);
				})
				.loot((tables, entity) -> {
					tables.add(entity, LootTable.lootTable());
				})
				.register();
		SPACE_ELEVATOR = Cmi.REGISTRATE.entity("space_elevator", SpaceElevatorEntity::new, MobCategory.MISC)
				.properties((builder) -> {
					builder.sized(0.95f, 2.15f)
							.clientTrackingRange(256)
							.updateInterval(1);
				})
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("CMI Core Entities Registered!");
	}
}
