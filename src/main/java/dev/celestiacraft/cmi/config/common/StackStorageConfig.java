package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class StackStorageConfig extends ConfigModule {
	public static ForgeConfigSpec.BooleanValue NO_STACK_UPGRADE_ENABLED;
	public static ForgeConfigSpec.BooleanValue NO_STORAGE_STACK_ENABLED;

	public StackStorageConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "stack_storage", "Stack Storage settings");
	}

	@Override
	protected void register() {
		NO_STACK_UPGRADE_ENABLED = builder
				.comment("Whether the cmi:no_stack_upgrade tag should disable storage stack scaling for matching items")
				.comment("type: boolean")
				.comment("default: true")
				.define("no_stack_upgrade_enabled", true);

		NO_STORAGE_STACK_ENABLED = builder
				.comment("Whether the cmi:no_storage_stack tag should force the in-storage stack limit of matching items to 1")
				.comment("type: boolean")
				.comment("default: true")
				.define("no_storage_stack_enabled", true);
	}
}