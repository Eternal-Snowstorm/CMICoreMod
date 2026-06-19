package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class GeothermalGeneratorConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue PRODUCTION_EFFICIENCY;

	public GeothermalGeneratorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "geothermal_generator", "Geothermal Generator");
	}

	@Override
	protected void addConfigs() {
		PRODUCTION_EFFICIENCY = builder
				.comment("""
						Per tick production FE
						20 ticks = 1 second
						type: int
						""")
//				.comment("Per tick production FE")
//				.comment("20 ticks = 1 second")
//				.comment("type: int")
				.defineInRange("production_efficiency", 4096, 1, 409600);
	}
}