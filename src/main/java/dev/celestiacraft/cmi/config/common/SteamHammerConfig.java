package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SteamHammerConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue STEAM_CONSUMPTION;
	public static ForgeConfigSpec.IntValue STEAM_CAPACITY;
	public static ForgeConfigSpec.DoubleValue STRESS_IMPACT;

	public SteamHammerConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "steam_hammer", "Steam Hammer settings");
	}

	@Override
	protected void register() {
		STEAM_CONSUMPTION = builder
				.comment("Steam consumption per run of steam hammer (mB)")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("steam_consumption", 1000, 0, 1000000);

		STEAM_CAPACITY = builder
				.comment("Steam capacity in steam hammer (mB)")
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("steam_capacity", 10000, 1000, 1000000);

		STRESS_IMPACT = builder
				.comment("Stress impact of steam hammer (su)")
				.comment("type: double")
				.comment("default: 16.0")
				.defineInRange("stress_impact", 16.0, 0.0, 64);
	}
}