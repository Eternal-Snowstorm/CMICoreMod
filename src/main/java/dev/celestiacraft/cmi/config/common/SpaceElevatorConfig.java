package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SpaceElevatorConfig extends ConfigModule {
	public static ForgeConfigSpec.BooleanValue ENABLE_CARGO_GRID;

	public SpaceElevatorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "space_elevator", "Space Elevator");
	}

	@Override
	protected void addConfigs() {
		ENABLE_CARGO_GRID = builder.comment("Enable the multi-slot cargo grid in the space elevator storage UI")
				.comment("type: boolean")
				.comment("default: false")
				.define("Enable cargo grid", false);
	}
}
