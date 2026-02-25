package dev.celestiacraft.cmi.event;

import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Citron {
	/**
	 * 获取世界种子
	 *
	 * @param level
	 * @return
	 */
	private long getWorldSeed(Level level) {
		if (level instanceof ServerLevel server) {
			return server.getSeed();
		}
		return 0L;
	}

	/**
	 * 获取世界种子的指定部分
	 *
	 * @param level
	 * @param segmentIndex
	 * @return
	 */
	private int getSeedSegment(Level level, int segmentIndex) {
		String seedStr = String.valueOf(getWorldSeed(level));

		// 确保长度够 segmentIndex*3 + 3
		int requiredLength = (segmentIndex + 1) * 3;

		StringBuilder builder = new StringBuilder(seedStr);
		while (builder.length() < requiredLength) {
			builder.append(seedStr);
		}

		String expanded = builder.toString();

		int start = segmentIndex * 3;
		String part = expanded.substring(start, start + 3);

		return Integer.parseInt(part);
	}


	/**
	 * 读取value.txt文件
	 *
	 * @return
	 */
	private String readValueFile() {
		Path path = FMLPaths.CONFIGDIR.get().resolve("nebula/value.txt");

		try {
			if (Files.exists(path)) {
				return Files.readString(path);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return "";
	}

	/**
	 * 解析value.txt文件中的值
	 *
	 * @param key
	 * @return
	 */
	private String getValue(String key) {
		String content = readValueFile();
		if (content.isEmpty()) {
			return "0";
		}

		String[] lines = content.split("\\R");
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith(key)) {
				String[] parts = line.split("\\s+");
				if (parts.length >= 2) {
					return parts[1];
				}
			}
		}
		return "0";
	}

	public WidgetGroup createUI(Level level) {
		WidgetGroup group = new WidgetGroup();
		group.setSize(180, 120);
		group.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);

		LabelWidget label = new LabelWidget();
		label.setSelfPosition(10, 10);

		String text = String.format(
				"""
						1
						x : %d + %s
						y : %d - %s
				
						2
						x : %d + %s
						y : %d - %s
				
						3
						x : %d + %s
						y : %d - %s
				""",

				getSeedSegment(level, 0), getValue("%VALUE0%"),
				getSeedSegment(level, 1), getValue("%VALUE1%"),

				getSeedSegment(level, 2), getValue("%VALUE2%"),
				getSeedSegment(level, 3), getValue("%VALUE3%"),

				getSeedSegment(level, 4), getValue("%VALUE4%"),
				getSeedSegment(level, 5), getValue("%VALUE5%")
		);

		label.setText(Component.literal(text).getString());
		group.addWidget(label);

		return group;
	}
}