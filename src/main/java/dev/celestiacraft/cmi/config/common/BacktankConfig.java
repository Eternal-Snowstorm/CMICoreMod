package dev.celestiacraft.cmi.config.common;

import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class BacktankConfig extends ConfigModule {
	public static final int TICKS_PER_SECOND = 20;

	public static ForgeConfigSpec.IntValue COPPER_SECONDS;
	public static ForgeConfigSpec.IntValue NETHERITE_SECONDS;

	public BacktankConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "backtank", "Backtank");
	}

	public static int maxAir(boolean netherite, int enchantLevel) {
		int base = netherite ? NETHERITE_SECONDS.get() : COPPER_SECONDS.get();
		return base + AllConfigs.server().equipment.enchantedBacktankCapacity.get() * enchantLevel;
	}

	@Override
	protected void addConfigs() {
		COPPER_SECONDS = builder.comment("Copper backtank usable time in seconds (1 air unit is consumed every " + TICKS_PER_SECOND + " ticks, so 1 second)")
				.comment("type: int")
				.comment("default: 2000")
				.defineInRange("copper_seconds", 2000, 1, 1000000);

		NETHERITE_SECONDS = builder.comment("Netherite backtank usable time in seconds (1 air unit is consumed every " + TICKS_PER_SECOND + " ticks, so 1 second)")
				.comment("type: int")
				.comment("default: 4000")
				.defineInRange("netherite_seconds", 4000, 1, 1000000);
	}
}
