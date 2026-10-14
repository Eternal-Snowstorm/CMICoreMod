package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SteamHammerConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue STEAM_CONSUMPTION;
	public static ForgeConfigSpec.IntValue STEAM_CAPACITY;

	public SteamHammerConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "steam_hammer", "Steam Hammer");
	}

	@Override
	protected void addConfigs() {
		STEAM_CONSUMPTION = builder.comment("Steam consumption per run of steam hammer (mB)")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("steam_consumption", 1000, 0, 1000000);

		STEAM_CAPACITY = builder.comment("Steam capacity in steam hammer (mB)")
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("steam_capacity", 10000, 1000, 1000000);
	}
}