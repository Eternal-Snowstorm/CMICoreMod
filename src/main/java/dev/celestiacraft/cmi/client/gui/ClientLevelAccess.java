package dev.celestiacraft.cmi.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientLevelAccess {
	@Nullable
	public static Level currentLevel() {
		return Minecraft.getInstance().level;
	}
}