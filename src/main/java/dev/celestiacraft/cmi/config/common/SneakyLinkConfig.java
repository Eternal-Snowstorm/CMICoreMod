package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SneakyLinkConfig extends ConfigModule {
	public static ForgeConfigSpec.ConfigValue<Double> COLLECTION_RADIUS;
	public static ForgeConfigSpec.ConfigValue<Double> COLLECTION_DEPTH;
	public static ForgeConfigSpec.ConfigValue<Double> COLLECTION_HEIGHT;
	public static ForgeConfigSpec.ConfigValue<Boolean> INSTANT_PICKUP;

	public SneakyLinkConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "sneak_link", "SneakyLink settings");
	}

	@Override
	protected void register() {
		COLLECTION_RADIUS  = builder
				.comment("Horizontal radius within which the player picks up item entities")
				.define("Collection radius", 3.0);

		COLLECTION_DEPTH = builder
				.comment("Amount of blocks below the player where sneaking picks up item entities")
				.define("Collection depth", 2.0);

		COLLECTION_HEIGHT = builder
				.comment("Amount of blocks above the player where sneaking picks up item entities")
				.defineInRange("Collection height", 0.0, 0.0, 8.0);

		INSTANT_PICKUP = builder
				.comment("Pick up item entities regardless of their cooldown after being dropped from a player's inventory")
				.define("Ignore pickup delay", false);
	}
}