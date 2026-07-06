package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class MetalDetectorConfig extends ConfigModule {
	public static ForgeConfigSpec.BooleanValue PLAY_SOUND;

	public MetalDetectorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "metal_detector", "Metal Detector");
	}

	@Override
	protected void addConfigs() {
		PLAY_SOUND = builder.comment("Play sound with Metal Detector when turned on")
				.define("play_sound", true);
	}
}