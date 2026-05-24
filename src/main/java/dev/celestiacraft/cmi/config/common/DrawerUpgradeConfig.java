package dev.celestiacraft.cmi.config.common;

import dev.celestiacraft.cmi.config.base.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class DrawerUpgradeConfig extends ConfigModule {
	public static ForgeConfigSpec.BooleanValue SAME_UPGRADE_ONLY_ENABLED;
	public static ForgeConfigSpec.BooleanValue ADDITIVE_UPGRADE_ENABLED;

	public DrawerUpgradeConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "drawer_upgrade", "Functional Storage Drawer Upgrade");
	}

	@Override
	protected void register() {
		SAME_UPGRADE_ONLY_ENABLED = builder
				.comment("Whether a drawer can only accept upgrades of the same item type at once")
				.comment("type: boolean")
				.comment("default: true")
				.define("same_upgrade_only_enabled", true);

		ADDITIVE_UPGRADE_ENABLED = builder
				.comment("Whether drawer storage upgrades should stack additively instead of multiplicatively")
				.comment("type: boolean")
				.comment("default: true")
				.define("additive_upgrade_enabled", true);
	}
}
