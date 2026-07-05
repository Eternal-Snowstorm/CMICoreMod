package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedSpoutConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue FILLING_TIME;
	public static ForgeConfigSpec.IntValue CAPACITY;

	public AdvancedSpoutConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "advanced_spout", "Advanced Spout");
	}

	@Override
	protected void addConfigs() {
		FILLING_TIME = builder.comment("Filling time in ticks")
				.comment("20 ticks = 1 second")
				.comment("Original spout uses 20 ticks")
				.comment("type: int")
				.comment("default: 5")
				.defineInRange("filling_time", 5, 1, 100);

		CAPACITY = builder.comment("Fluid capacity in mB (millibuckets)")
				.comment("1000 mB = 1 bucket")
				.comment("Original spout uses 1000 mB")
				.comment("type: int")
				.comment("default: 5000")
				.defineInRange("capacity", 5000, 2000, 64000);
	}
}