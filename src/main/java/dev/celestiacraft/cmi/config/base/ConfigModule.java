package dev.celestiacraft.cmi.config.base;

import net.minecraftforge.common.ForgeConfigSpec;

public abstract class ConfigModule {
	protected final ForgeConfigSpec.Builder builder;

	public ConfigModule(ForgeConfigSpec.Builder builder, String path, String comment) {
		this.builder = builder;

		builder.comment(comment + " settings")
				.push(path);

		register();

		builder.pop();
	}

	protected abstract void register();
}