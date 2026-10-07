package dev.celestiacraft.cmi.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端当前维度访问器。
 * <p>
 * 单独成类的原因: {@code Minecraft#level} 的静态类型是 {@code ClientLevel}, 若在服务端会被加载的类里
 * 直接写 {@code Level level = Minecraft.getInstance().level;} 赋值语句, 字节码校验器为做可赋值性检查
 * 会去加载 {@code net.minecraft.client.multiplayer.ClientLevel}, 而 RuntimeDistCleaner 会以
 * "Attempted to load class ... for invalid dist DEDICATED_SERVER" 中断加载。
 * 这里把该访问折叠为一个返回 {@code Level} 的方法, 调用方 (UIFactory) 的字节码里只出现 {@code Level},
 * 方法本体只在客户端真正执行时才加载。
 */
public final class ClientLevelAccess {
	private ClientLevelAccess() {
	}

	@Nullable
	public static Level currentLevel() {
		return Minecraft.getInstance().level;
	}
}
