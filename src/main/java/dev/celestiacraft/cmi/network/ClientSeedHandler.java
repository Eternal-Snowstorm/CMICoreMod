package dev.celestiacraft.cmi.network;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientSeedHandler {
	private static long CLIENT_SEED = 0L;

	public static void writeValue(long seed) {
		CLIENT_SEED = seed;

		Path path = FMLPaths.CONFIGDIR.get().resolve("nebula/value.txt");

		try {
			Files.createDirectories(path.getParent());

			String currentSeedLine = "SEED " + seed;

			if (Files.exists(path)) {
				String old = Files.readString(path);
				if (old.contains(currentSeedLine)) {
					return;
				}
			}

			long abs = Math.abs(seed);
			String content = getFileString(abs);

			Files.writeString(path, content);

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private static String getFileString(long abs) {
		int[] segments = buildSegments(abs);

		return """
            $VALUE1$ %s
            $VALUE2$ %s
            
            $VALUE3$ %s
            $VALUE4$ %s
            
            $VALUE5$ %s
            $VALUE6$ %s""".formatted(

				// 奇数位：十进制
				segments[0],

				// 偶数位：十六进制
				Integer.toHexString(segments[1]).toUpperCase(),

				segments[2],
				Integer.toHexString(segments[3]).toUpperCase(),

				segments[4],
				Integer.toHexString(segments[5]).toUpperCase()
		);
	}

	private static int[] buildSegments(long abs) {
		String seedStr = String.valueOf(abs);

		StringBuilder builder = new StringBuilder(seedStr);
		while (builder.length() < 18) {
			builder.append(seedStr);
		}

		String expanded = builder.toString();

		int[] segments = new int[6];

		for (int i = 0; i < 6; i++) {
			int start = i * 3;
			segments[i] = Integer.parseInt(expanded.substring(start, start + 3));
		}

		return segments;
	}

	@Info("最小值: 1, 最高值: 6\n\nMin: 1, Max: 6")
	public static int getValue(int index) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		if (server == null) {
			return 0;
		}

		long seed = server.overworld().getSeed();

		if (index < 1 || index > 6) {
			return 0;
		}

		int[] segments = buildSegments(Math.abs(seed));
		int value = segments[index - 1];

		// 奇数位: 直接返回
		if (index % 2 == 1) {
			return value;
		}

		// 偶数位: 16进制再解密
		String hex = Integer.toHexString(value);
		return Integer.parseInt(hex, 16);
	}
}