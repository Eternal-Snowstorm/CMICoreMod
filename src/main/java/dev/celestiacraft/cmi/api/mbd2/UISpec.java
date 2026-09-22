package dev.celestiacraft.cmi.api.mbd2;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextTextureWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.MBDMultiblockMachine;
import com.lowdragmc.mbd2.common.trait.IUIProviderTrait;
import com.lowdragmc.mbd2.common.trait.TraitDefinition;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.forgeenergy.ForgeEnergyCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.forgeenergy.ForgeEnergyCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.item.ItemSlotCapabilityTrait;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.mbd2.steam.AbstractSteamMachine;
import dev.celestiacraft.cmi.compat.mbd2.MBDHelpers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

/**
 * 机器 GUI 的链式 DSL。
 *
 * <p>两种用法:
 * <ul>
 *     <li>总线/接口: {@link #bus(MBDMachine)} —— 全自动, 所有 trait 自己排 + 底部玩家物品栏, 高度按内容算。</li>
 *     <li>控制器/单方块: {@link #create} + {@link Builder#screen} (大屏幕) + {@link Builder#playerInventory}。</li>
 * </ul>
 *
 * <p>坐标系: {@code create} 给的坐标是相对根组; {@code screen(x, y, w, h, content)} 里 content
 * 收到的 builder 坐标相对屏幕面板左上角 (屏幕自己带边框, 内容想内缩就自己加偏移)。
 *
 * <p>接线: {@link Builder#build()} 会调 {@link MBDHelpers#bindUI}, 把 id 约定
 * ({@code ui:machine_name} / {@code ui:progress_bar} / {@code ui:<trait>_<i>} ...) 交给 MBD2 自动绑定,
 * 所以两端 (服务端建树 / 客户端重建) 行为一致, 不用自己写同步。
 *
 * <pre>{@code
 * // JS (startup_scripts)
 * MBDUI.register(Cmi.loadResource("electrolyzer"), (machine) => UISpec.create(machine, 176, 180, (ui) => {
 *     ui.background("ldlib:textures/gui/background.png");
 *
 *     ui.screen(4, 4, 168, 84, (screen) => {
 *         screen.text(8, 8, 150, 12, (m) => UISpec.machineName(m));
 *         screen.text(8, 24, 150, 12, (m) => UISpec.status(m));
 *     });
 *
 *     ui.slots("electrolyzer_input_item", 8, 96, 3, 1);
 *     ui.playerInventory();
 * }));
 * }</pre>
 */
public class UISpec {
	/**
	 * 默认界面宽度 (原版箱子/机器通用宽度)
	 */
	public static final int DEFAULT_WIDTH = 200;

	/**
	 * 玩家物品栏 (LDLib PlayerInventoryWidget) 尺寸
	 */
	public static final int PLAYER_INV_WIDTH = 172;
	public static final int PLAYER_INV_HEIGHT = 86;
	/** 物品栏里第一格离它自己左边缘的距离 (LDLib 写死的 5) */
	public static final int PLAYER_INV_MARGIN = 5;

	/**
	 * 单个格子的边长
	 */
	public static final int SLOT = 18;

	/**
	 * trait 之间的垂直间距
	 */
	public static final int GAP = 4;

	/**
	 * 总线界面默认列数 (物品槽一行几个)
	 */
	public static final int BUS_COLUMNS = 9;

	/**
	 * 控制器自动版式里屏幕的高度
	 */
	public static final int CONTROLLER_SCREEN_HEIGHT = 96;

	/**
	 * 屏幕里文字的左/上边距与行高 (一行行往下排)
	 */
	public static final int SCREEN_TEXT_X = 18;
	public static final int SCREEN_TEXT_Y = 12;
	public static final int SCREEN_TEXT_GAP = 16;
	public static final int SCREEN_TEXT_WIDTH = DEFAULT_WIDTH - 8 - 2 * SCREEN_TEXT_X;
	public static final int SCREEN_TEXT_HEIGHT = 14;

	/**
	 * 屏幕默认贴图: cmi:textures/gui/machine/basic_machine_ui.png (195x136)。
	 * 换贴图只改这一行, 或用带贴图参数的重载。
	 */
	public static final ResourceLocation DEFAULT_SCREEN_TEXTURE =
			Cmi.loadResource("textures/gui/machine/basic_machine_ui.png");

	public static final int SCREEN_TEXTURE_WIDTH = 195;
	public static final int SCREEN_TEXTURE_HEIGHT = 136;

	/**
	 * 九宫格边框厚度 (取整张图的外框宽度, 上/下/左/右各切这么多像素按原尺寸画)。
	 * <p>
	 * 9 是这张贴图外框的实际厚度 (黑1+白1+灰4+白1+深灰1+黑1)。边框切厚一点没坏处:
	 * 多切进去的那几像素也是按原尺寸画的, 画面照样连续。
	 */
	public static final int SCREEN_BORDER = 9;

	/**
	 * 屏幕默认九宫格模式:
	 * <ul>
	 *     <li>{@code tile} (默认): 边框原尺寸, 内部 1:1 平铺 —— 屏幕比贴图小的时候就是原样裁切,
	 *     斜纹不会被拉变形; 比贴图大时内部会重复 (会有接缝)。</li>
	 *     <li>{@code fit}: 内部拉伸填满 (屏幕尺寸一变, 斜纹角度/粗细就变)。</li>
	 *     <li>{@code stretch}: 边框也一起拉。</li>
	 * </ul>
	 */
	public static final String DEFAULT_SCREEN_MODE = "tile";

	/**
	 * 默认界面底: ldlib 的 16x16 九宫格底纹 (原版那种 1px 黑边 + 白线)。
	 * <p>
	 * 注意别再用 background(贴图) 直接铺这张 16x16 —— 那是整张拉伸, 1px 边会变成十几像素的粗白边。
	 */
	public static final ResourceBorderTexture BACKGROUND = ResourceBorderTexture.BORDERED_BACKGROUND;

	/** 总线上的流体格贴图 (18x18, 九宫格边 1) */
	public static final ResourceLocation FLUID_SLOT_TEXTURE = Cmi.loadResource("textures/gui/slot/fluid_slot.png");

	/** 总线上的气体格贴图 (18x18, 九宫格边 1) */
	public static final ResourceLocation GAS_SLOT_TEXTURE = Cmi.loadResource("textures/gui/slot/gas_slot.png");

	/** 上面两张格贴图的九宫格边宽 */
	public static final int SLOT_TEXTURE_BORDER = 1;

	/**
	 * 进度条默认贴图 (ldlib 的箭头条: 上半是空, 下半是填充)
	 */
	public static final ResourceTexture PROGRESS_TEXTURE =
			new ResourceTexture(LDLib.location("textures/gui/progress_bar_arrow.png"));

	/**
	 * 控制器版式里机器槽位区的 y (屏幕起点 4 + 屏幕高 + 间隔 6)。
	 */
	public static int controllerSlotY(int screenHeight) {
		return 4 + screenHeight + 6;
	}

	/**
	 * 控制器版式的整界面高度: 上边距 4 + 屏幕 + 间隔 6 + 槽位行 + 间隔 8 + 物品栏 86 + 下边距 4。
	 * <p>
	 * JS: {@code UISpec.create(machine, 176, UISpec.controllerHeight(84, 1), ...)}
	 */
	public static int controllerHeight(int screenHeight, int slotRows) {
		return controllerSlotY(screenHeight) + slotRows * SLOT + 8 + PLAYER_INV_HEIGHT + 4;
	}

	/**
	 * 建一棵界面树。
	 *
	 * @param machine 机器 (供应函数里会用到)
	 * @param width   宽
	 * @param height  高
	 * @param config  布局回调
	 */
	public static WidgetGroup create(MBDMachine machine, int width, int height, Consumer<Builder> config) {
		Builder builder = new Builder(machine, new WidgetGroup(0, 0, width, height));
		config.accept(builder);
		return builder.build();
	}

	/**
	 * 总线/接口界面: 该机器身上所有 trait 的默认 widget 自动排版 + 底部玩家物品栏。
	 * <p>
	 * 机器没有 trait 时只剩一个物品栏; trait 变了 (加槽 / 加罐) 界面自动跟着变, 不用改代码。
	 */
	public static WidgetGroup bus(MBDMachine machine) {
		return bus(machine, DEFAULT_WIDTH, BUS_COLUMNS);
	}

	public static WidgetGroup bus(MBDMachine machine, int width, int columns) {
		// 左边距跟玩家物品栏对齐: 物品栏是自己居中的, 它内部第一格离自己左边缘 5px。
		// 用 8 这种随手数会和物品栏错开一格多一点, 看起来就是"歪的"。
		int margin = Math.max(4, (width - PLAYER_INV_WIDTH) / 2 + PLAYER_INV_MARGIN);

		Builder builder = new Builder(machine, new WidgetGroup(0, 0, width, 16));
		builder.background(BACKGROUND);
		builder.autoTraits(margin, margin, columns, false);   // false: 不排能量条 (有 Jade 了)

		// 注意是相加: 内容底部 + 间隔 + 物品栏 + 下边距。
		// 写成 Math.max(...) 会让物品栏直接压在内容上面 (槽位被盖住, 面板还矮一截)。
		int height = Math.max(builder.cursorY(), margin) + GAP + PLAYER_INV_HEIGHT + 8;
		builder.size(width, height);
		builder.playerInventory();

		return builder.build();
	}

	/**
	 * 控制器 (多方块) 版式: 上半大屏幕 (机器名 / 状态 / 进度 / 卡住的原因), 下半玩家物品栏。
	 * <p>
	 * 整界面 176x174 —— 机器自己的槽位不在这里 (按设计: 储物在总线/接口上), 需要的话用
	 * {@link #controllerWithSlots}。
	 */
	public static WidgetGroup controller(MBDMachine machine) {
		return controller(machine, CONTROLLER_SCREEN_HEIGHT, null, false);
	}

	/**
	 * 控制器版式 + 机器自己的槽位 (屏幕 -> 自动排的槽位 -> 玩家物品栏)。
	 */
	public static WidgetGroup controllerWithSlots(MBDMachine machine) {
		return controller(machine, CONTROLLER_SCREEN_HEIGHT, null, true);
	}

	/**
	 * 控制器版式, 屏幕里追加自定义内容 (坐标相对屏幕)。
	 * <p>
	 * 例: 蒸汽机把聚合水位条塞进屏幕 —— {@code UISpec.controller(machine, 84, s -> s.steamBar(..., 14, 68, 140, 12))}
	 */
	public static WidgetGroup controller(MBDMachine machine, int screenHeight, Consumer<Builder> extraScreenContent) {
		return controller(machine, screenHeight, extraScreenContent, false);
	}

	/**
	 * 控制器版式 + 机器自己的槽位, 屏幕里追加自定义内容
	 */
	public static WidgetGroup controllerWithSlots(MBDMachine machine, int screenHeight, Consumer<Builder> extraScreenContent) {
		return controller(machine, screenHeight, extraScreenContent, true);
	}

	private static WidgetGroup controller(MBDMachine machine, int screenHeight, Consumer<Builder> extraScreenContent, boolean withSlots) {
		Builder builder = new Builder(machine, new WidgetGroup(0, 0, DEFAULT_WIDTH, 16));

		builder.background(BACKGROUND);

		builder.screen(4, 4, DEFAULT_WIDTH - 8, screenHeight, (screen) -> {
			screen.text(SCREEN_TEXT_X, SCREEN_TEXT_Y, SCREEN_TEXT_WIDTH, SCREEN_TEXT_HEIGHT, UISpec::machineName);
			screen.text(SCREEN_TEXT_X, SCREEN_TEXT_Y + SCREEN_TEXT_GAP, SCREEN_TEXT_WIDTH, SCREEN_TEXT_HEIGHT, UISpec::status);
			screen.text(SCREEN_TEXT_X, SCREEN_TEXT_Y + SCREEN_TEXT_GAP * 2, SCREEN_TEXT_WIDTH, SCREEN_TEXT_HEIGHT, UISpec::progressText);
			screen.text(SCREEN_TEXT_X, SCREEN_TEXT_Y + SCREEN_TEXT_GAP * 3, SCREEN_TEXT_WIDTH, SCREEN_TEXT_HEIGHT, UISpec::energyLine);
			screen.text(SCREEN_TEXT_X, SCREEN_TEXT_Y + SCREEN_TEXT_GAP * 4, SCREEN_TEXT_WIDTH, SCREEN_TEXT_HEIGHT, UISpec::waitingReason);

			if (extraScreenContent != null) {
				extraScreenContent.accept(screen);
			}
		});

		int height = controllerSlotY(screenHeight);

		if (withSlots) {
			builder.autoTraits(8, height, BUS_COLUMNS);
			height = Math.max(builder.cursorY(), height);
		}

		height += 6 + PLAYER_INV_HEIGHT + 4;

		builder.size(DEFAULT_WIDTH, height);
		builder.playerInventory();

		return builder.build();
	}
	// ------------------------------------------------------------------
	// 文案 / 数据 (给 JS 用, 也可以自己用 supplier 拼)
	// ------------------------------------------------------------------

	/**
	 * 机器名 (自定义名优先)
	 */
	public static Component machineName(MBDMachine machine) {
		Component name = machine.getCustomName();
		return name != null ? name : machine.getDefinition().block().getName();
	}

	/**
	 * 状态码: idle / working / waiting / suspend / unformed
	 * <p>
	 * 文案由 JS 自己 map, 或直接用 {@link #status(MBDMachine)}。
	 */
	public static String statusKey(MBDMachine machine) {
		if (machine instanceof MBDMultiblockMachine multiblock && !multiblock.isFormed()) {
			return "unformed";
		}

		RecipeLogic logic = machine.getRecipeLogic();

		return switch (logic.getStatus()) {
			case WORKING -> "working";
			case WAITING -> "waiting";
			case SUSPEND -> "suspend";
			default -> "idle";
		};
	}

	/**
	 * 状态 (中文文案在 lang: cmi.mbd.ui.status.*)
	 */
	public static Component status(MBDMachine machine) {
		return Component.translatable("cmi.mbd.ui.status." + statusKey(machine));
	}

	/**
	 * 卡住的原因 (MBD2 给的具体原因, 缺料/缺电…), 没有则为 null
	 */
	public static Component waitingReason(MBDMachine machine) {
		return machine.getRecipeLogic().getWaitingReason();
	}

	/**
	 * 配方进度百分比 (0~100 整数文案)
	 */
	public static Component progressText(MBDMachine machine) {
		// 注意: LDLib 的文本会过一次 I18n 的 String.format, 裸 % 会让客户端显示 "Format error: xx%"
		// -> 用全角 ％ (U+FF05), 既不触发格式化, 看起来也还是百分号
		return Component.literal(Math.round(machine.getRecipeLogic().getProgressPercent() * 100) + "％");
	}

	/**
	 * 能量行: "能量: 1.2k/4.0M FE" —— 这台机器上第一个能量 trait (没有就是空行)。
	 */
	public static Component energyLine(MBDMachine machine) {
		for (TraitDefinition definition : machine.getDefinition().machineSettings().traitDefinitions()) {
			if (definition instanceof ForgeEnergyCapabilityTraitDefinition) {
				return line(Component.translatable("cmi.mbd.ui.energy"), energyText(machine, definition.getName()));
			}
		}

		return null;
	}

	/**
	 * 能量文案: "1.2k/4.0M FE" (trait 不存在则返回 null)
	 */
	public static Component energyText(MBDMachine machine, String traitName) {
		ForgeEnergyCapabilityTrait trait = machine.getTraitByName(ForgeEnergyCapabilityTrait.class, traitName);

		if (trait == null) {
			return null;
		}

		return Component.literal(format(trait.storage.getEnergyStored()) + "/" + format(trait.storage.getMaxEnergyStored()) + " FE");
	}

	/**
	 * 流体文案: "160/4000 mB" (trait 不存在则返回 null)
	 */
	public static Component fluidText(MBDMachine machine, String traitName, int index) {
		FluidTankCapabilityTrait trait = machine.getTraitByName(FluidTankCapabilityTrait.class, traitName);

		if (trait == null || index < 0 || index >= trait.storages.length) {
			return null;
		}

		return Component.literal(trait.storages[index].getFluidAmount() + "/" + trait.storages[index].getCapacity() + " mB");
	}

	/**
	 * "标签: 值" 一行
	 */
	public static Component line(Component label, Component value) {
		return Component.empty().append(label).append(": ").append(value == null ? Component.literal("-") : value);
	}

	public static Component literal(String text) {
		return Component.literal(text);
	}

	public static Component translatable(String key, Object... args) {
		return Component.translatable(key, args);
	}

	/**
	 * 12345 -> "12.3k"
	 */
	public static String format(long value) {
		if (value < 1000) {
			return Long.toString(value);
		}

		String[] units = {"k", "M", "G", "T"};
		double scaled = value;
		int unit = -1;

		while (scaled >= 1000 && unit < units.length - 1) {
			scaled /= 1000;
			unit++;
		}

		return String.format("%.1f%s", scaled, units[unit]);
	}

	// ------------------------------------------------------------------

	public static class Builder {
		private final MBDMachine machine;
		private final WidgetGroup group;

		/**
		 * autoTraits 系列用到的游标 (内容底部 y)
		 */
		private int cursorY;

		/**
		 * 整体缩放: 所有坐标 / 尺寸都乘它, 文字和 MBD2 模板槽位一起放大 (1 = 原尺寸)
		 */
		private float scale = 1.0f;

		/**
		 * 根组的逻辑尺寸, 按 scale 落成实际尺寸
		 */
		private int logicalWidth;
		private int logicalHeight;

		private Builder(MBDMachine machine, WidgetGroup group) {
			this.machine = machine;
			this.group = group;
			cursorY = group.getSizeHeight();
			logicalWidth = group.getSizeWidth();
			logicalHeight = group.getSizeHeight();
		}

		/**
		 * 整体放大: 之后给的坐标 / 尺寸都按逻辑像素写, 内部乘这个倍数。
		 * <p>
		 * 文字是点阵字体, 放大时连字一起放大 (TextTexture 的 transform scale); MBD2 模板生成的
		 * 槽位 / 罐也会跟着放大, 所以 autoTraits 这类自动排版不用改。
		 * <p>
		 * 根组尺寸传逻辑值就行: {@code UISpec.create(machine, 176, 210, ui -> ui.scale(2))}
		 * 实际面板会自动变成 352x420, 别自己再乘一遍。
		 */
		public Builder scale(float scale) {
			this.scale = scale <= 0 ? 1.0f : scale;
			group.setSize(px(logicalWidth), px(logicalHeight));

			return this;
		}

		/**
		 * 逻辑像素 -> 实际像素
		 */
		private int px(int value) {
			return Math.round(value * scale);
		}

		/**
		 * 改根组尺寸 (bus 这种先排内容再定高的场景用)
		 */
		public Builder size(int width, int height) {
			logicalWidth = width;
			logicalHeight = height;
			group.setSize(px(width), px(height));
			return this;
		}

		public Builder background(ResourceLocation texture) {
			group.setBackground(new ResourceTexture(texture));
			return this;
		}

		public Builder background(IGuiTexture texture) {
			group.setBackground(texture);
			return this;
		}

		public Builder backgroundBordered(ResourceLocation texture, int imageWidth, int imageHeight, int border) {
			group.setBackground(new ResourceBorderTexture(texture.toString(), imageWidth, imageHeight, border, border));
			return this;
		}

		public Builder title(int x, int y) {
			return title(x, y, 40, 20);
		}

		public Builder title(int x, int y, int width, int height) {
			TextTextureWidget widget = textWidget(x, y, width, height);

			widget.setId("ui:machine_name");
			widget.setText(() -> machineName(machine));
			group.addWidget(widget);
			return this;
		}

		public Builder screen(int x, int y, int width, int height, Consumer<Builder> content) {
			return screen(x, y, width, height, DEFAULT_SCREEN_MODE, content);
		}

		public Builder screen(int x, int y, int width, int height) {
			return screen(x, y, width, height, DEFAULT_SCREEN_MODE, null);
		}

		public Builder screen(int x, int y, int width, int height, String mode, Consumer<Builder> content) {
			return screen(x, y, width, height, DEFAULT_SCREEN_TEXTURE, SCREEN_TEXTURE_WIDTH, SCREEN_TEXTURE_HEIGHT, SCREEN_BORDER, mode, content);
		}

		public Builder screen(int x, int y, int width, int height, ResourceLocation texture, int imageWidth, int imageHeight, int border, String mode, Consumer<Builder> content) {
			ResourceBorderTexture background = new ResourceBorderTexture(texture.toString(), imageWidth, imageHeight, border, border);

			background.setSliceMode(sliceMode(mode));

			WidgetGroup panel = new WidgetGroup(px(x), px(y), px(width), px(height));

			panel.setBackground(background);
			group.addWidget(panel);

			if (content != null) {
				Builder child = new Builder(machine, panel);

				child.scale = scale;
				child.logicalWidth = width;
				child.logicalHeight = height;

				content.accept(child);
			}

			return this;
		}

		private static ResourceBorderTexture.NineSliceMode sliceMode(String mode) {
			if (mode == null) {
				return ResourceBorderTexture.NineSliceMode.TILE;
			}

			return switch (mode.toLowerCase(Locale.ROOT)) {
				case "fit" -> ResourceBorderTexture.NineSliceMode.FIT;
				case "stretch" -> ResourceBorderTexture.NineSliceMode.STRETCH;
				default -> ResourceBorderTexture.NineSliceMode.TILE;
			};
		}

		public Builder playerInventory() {
			WidgetGroup inventory = scale == 1.0f ? new PlayerInventoryWidget() : new ScaledPlayerInventory(scale);

			inventory.setSelfPosition(new Position(
					Math.max(0, (group.getSizeWidth() - px(PLAYER_INV_WIDTH)) / 2),
					Math.max(0, group.getSizeHeight() - px(2) - px(PLAYER_INV_HEIGHT))
			));

			group.addWidget(inventory);
			return this;
		}

		private TextTextureWidget textWidget(int x, int y, int width, int height) {
			TextTextureWidget widget = new TextTextureWidget(px(x), px(y), px(width), px(height));

			scaleText(widget);
			return widget;
		}

		private void scaleText(TextTextureWidget widget) {
			if (scale != 1.0f) {
				widget.textureStyle((text) -> {
					text.scale(scale);
				});
			}
		}

		public Builder slots(String traitName, int x, int y, int columns, int rows) {
			for (int i = 0; i < columns * rows; i++) {
				SlotWidget slot = new SlotWidget();

				slot.initTemplate();
				slot.setSize(px(SLOT), px(SLOT));
				slot.setSelfPosition(new Position(px(x) + (i % columns) * px(SLOT), px(y) + (i / columns) * px(SLOT)));
				slot.setId("ui:" + traitName + "_" + i);

				group.addWidget(slot);
			}

			return this;
		}

		public Builder slot(String traitName, int x, int y) {
			return slot(traitName, x, y, SLOT, SLOT);
		}

		public Builder slot(String traitName, int x, int y, int width, int height) {
			ItemSlotCapabilityTrait trait = trait(machine, ItemSlotCapabilityTrait.class, traitName);
			SlotWidget slot = new SlotWidget(trait.storage, 0, px(x), px(y));

			slot.setSize(px(width), px(height));
			group.addWidget(slot);

			return this;
		}

		public Builder tank(String traitName, int x, int y, int width, int height) {
			FluidTankCapabilityTrait trait = trait(machine, FluidTankCapabilityTrait.class, traitName);

			group.addWidget(new TankWidget(trait.storages[0], px(x), px(y), px(width), px(height), true, true));
			return this;
		}

		public Builder autoTraits(int x, int y, int columns) {
			return autoTraits(x, y, columns, false);
		}

		/**
		 * @param withEnergy 是否把能量 trait 的条也排进来 (默认不排: 信息用 Jade / 屏幕文字看就够了)
		 */
		public Builder autoTraits(int x, int y, int columns, boolean withEnergy) {
			List<Widget> items = new ArrayList<>();
			List<Widget> tanks = new ArrayList<>();
			List<List<Widget>> groups = new ArrayList<>();

			for (TraitDefinition definition : machine.getDefinition().machineSettings().traitDefinitions()) {
				if (!(definition instanceof IUIProviderTrait provider)) {
					continue;
				}

				if (!withEnergy && definition instanceof ForgeEnergyCapabilityTraitDefinition) {
					continue;
				}

				List<Widget> widgets = templateWidgets(provider);

				if (widgets.isEmpty()) {
					continue;
				}

				// 流体 / 气体: 不摆那根 20x58 的竖罐, 换成和物品一样的 18x18 格子 (贴图见上面的常量)
				boolean gas = definition.getClass().getName().contains("ChemicalTank");

				if (gas || definition instanceof FluidTankCapabilityTraitDefinition) {
					for (Widget widget : widgets) {
						styleTankSlot(widget, gas);
					}
				}

				if (allOf(widgets, SlotWidget.class)) {
					items.addAll(widgets);
				} else if (allIndexed(widgets)) {
					tanks.addAll(widgets);
				} else {
					groups.add(widgets);
				}
			}

			int cursor = px(y);

			cursor = grid(items, px(x), cursor, columns);
			cursor = grid(tanks, px(x), cursor, 0);   // 0 = 按可用宽度自动算列数

			for (List<Widget> widgets : groups) {
				cursor = placeWidgets(widgets, px(x), cursor, columns);
			}

			cursorY = cursor;
			return this;
		}

		/**
		 * 在 (x, y) 摆一个 trait 的槽位, 并换成指定贴图的格子。
		 * <p>
		 * 给"只有一个格、但有专属外观"的槽用 (例: 电解机的石墨电极)。
		 * 只取 MBD2 模板生成的第一个 widget; 罐类会顺手清掉罐身叠图。
		 */
		public Builder traitSlot(String traitName, int x, int y, ResourceLocation texture, int size, int border) {
			for (TraitDefinition definition : machine.getDefinition().machineSettings().traitDefinitions()) {
				if (!traitName.equals(definition.getName())) {
					continue;
				}

				if (!(definition instanceof IUIProviderTrait provider)) {
					return this;
				}

				List<Widget> widgets = templateWidgets(provider);

				if (widgets.isEmpty()) {
					return this;
				}

				Widget widget = widgets.get(0);

				widget.setSize(size, size);
				widget.setBackground(new ResourceBorderTexture(texture.toString(), size, size, border, border));

				if (widget instanceof TankWidget tank) {
					tank.setOverlay(IGuiTexture.EMPTY);
				} else {
					clearOverlay(widget);
				}

				widget.setSelfPosition(new Position(px(x), px(y)));
				group.addWidget(widget);

				cursorY = px(y) + px(size) + px(GAP);
				return this;
			}

			throw new IllegalArgumentException(String.format(
					"UI: trait not found: %s on %s",
					traitName,
					machine.getDefinition().id()
			));
		}

		public Builder traitWidgets(String traitName, int x, int y, int columns) {
			for (TraitDefinition definition : machine.getDefinition().machineSettings().traitDefinitions()) {
				if (!traitName.equals(definition.getName())) {
					continue;
				}

				if (definition instanceof IUIProviderTrait provider) {
					cursorY = placeWidgets(templateWidgets(provider), px(x), px(y), columns);
				}

				return this;
			}

			throw new IllegalArgumentException(String.format(
					"UI: trait not found: %s on %s",
					traitName,
					machine.getDefinition().id()
			));
		}

		private static List<Widget> templateWidgets(IUIProviderTrait provider) {
			WidgetGroup template = new WidgetGroup(0, 0, 0, 0);

			provider.createTraitUITemplate(template);

			List<Widget> widgets = new ArrayList<>(template.widgets);

			template.widgets.clear();

			return widgets;
		}

		/**
		 * 把 MBD2 模板给的竖罐 (20x58) 换成一张 18x18 的格子, 这样流体 / 气体总线看起来就是"一箱格子"。
		 * <p>
		 * 尺寸写逻辑值 (SLOT), 由 grid 统一乘 scale; 罐身叠图必须清掉, 否则会拉伸成一条线。
		 */
		private static void styleTankSlot(Widget widget, boolean gas) {
			ResourceLocation texture = gas ? GAS_SLOT_TEXTURE : FLUID_SLOT_TEXTURE;

			widget.setSize(SLOT, SLOT);
			widget.setBackground(new ResourceBorderTexture(texture.toString(), SLOT, SLOT, SLOT_TEXTURE_BORDER, SLOT_TEXTURE_BORDER));

			if (widget instanceof TankWidget tank) {
				tank.setOverlay(IGuiTexture.EMPTY);
			} else {
				// 气体用的是 MBD2 的 ChemicalTankWidget (不继承 TankWidget), 但它也有 setOverlay。
				// 不清掉的话那张 20x58 的罐身贴图 (里面全是红色刻度) 会被压进 18x18 的格子里。
				clearOverlay(widget);
			}
		}

		/** 反射清掉罐身叠图 (避免为了一个 setter 去 import Mekanism 的类) */
		private static void clearOverlay(Widget widget) {
			try {
				Method method = widget.getClass().getMethod("setOverlay", IGuiTexture.class);

				method.invoke(widget, IGuiTexture.EMPTY);
			} catch (ReflectiveOperationException exception) {
				// 没有这个方法就随它去, 只是外观问题
			}
		}

		private int grid(List<Widget> widgets, int x, int y, int columns) {
			if (widgets.isEmpty()) {
				return y;
			}

			for (Widget widget : widgets) {
				widget.setSize(px(widget.getSizeWidth()), px(widget.getSizeHeight()));
			}

			int pitchX = Math.max(px(SLOT), widgets.get(0).getSizeWidth());
			int pitchY = Math.max(px(SLOT), widgets.get(0).getSizeHeight());

			if (columns <= 0) {
				columns = Math.max(1, (px(DEFAULT_WIDTH) - 2 * x) / Math.max(1, pitchX));
			}

			int rows = (widgets.size() + columns - 1) / columns;

			for (int i = 0; i < widgets.size(); i++) {
				Widget widget = widgets.get(i);

				widget.setSelfPosition(new Position(x + (i % columns) * pitchX, y + (i / columns) * pitchY));
				group.addWidget(widget);
			}

			return y + rows * pitchY + px(GAP);
		}

		private int placeWidgets(List<Widget> widgets, int x, int y, int columns) {
			List<Widget> indexed = new ArrayList<>();
			List<Widget> blocks = new ArrayList<>();

			for (Widget widget : widgets) {
				if (indexOf(widget.getId()) >= 0) {
					indexed.add(widget);
				} else {
					blocks.add(widget);
				}
			}

			indexed.sort(Comparator.comparingInt((widget) -> {
				return indexOf(widget.getId());
			}));

			int cursor = grid(indexed, x, y, columns);

			if (blocks.isEmpty()) {
				return cursor;
			}

			for (Widget widget : blocks) {
				widget.setSize(px(widget.getSizeWidth()), px(widget.getSizeHeight()));

				if (widget instanceof TextTextureWidget textWidget) {
					scaleText(textWidget);
				}
			}

			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;

			for (Widget widget : blocks) {
				minX = Math.min(minX, widget.getPositionX());
				minY = Math.min(minY, widget.getPositionY());
				maxY = Math.max(maxY, widget.getPositionY() + widget.getSizeHeight());
			}

			for (Widget widget : blocks) {
				widget.setSelfPosition(new Position(
						x + widget.getPositionX() - minX,
						cursor + widget.getPositionY() - minY
				));

				group.addWidget(widget);
			}

			return cursor + (maxY - minY) + px(GAP);
		}

		private static boolean allOf(List<Widget> widgets, Class<?> type) {
			for (Widget widget : widgets) {
				if (!type.isInstance(widget)) {
					return false;
				}
			}

			return true;
		}

		private static boolean allIndexed(List<Widget> widgets) {
			for (Widget widget : widgets) {
				if (indexOf(widget.getId()) < 0) {
					return false;
				}
			}

			return true;
		}

		public int cursorY() {
			return cursorY;
		}

		public Builder progress(int x, int y) {
			return progress(x, y, SLOT, SLOT);
		}

		public Builder progress(int x, int y, int width, int height) {
			ProgressWidget widget = new ProgressWidget(() -> {
				return machine.getRecipeLogic().getProgressPercent();
			}, px(x), px(y), px(width), px(height), PROGRESS_TEXTURE);

			widget.setId("ui:progress_bar");
			group.addWidget(widget);

			return this;
		}

		public Builder progressBar(int x, int y) {
			return progressBar(x, y, SLOT, SLOT);
		}

		public Builder progressBar(int x, int y, int width, int height) {
			group.addWidget(new ProgressWidget(() -> {
				return machine.getRecipeLogic().getProgressPercent();
			}, px(x), px(y), px(width), px(height), PROGRESS_TEXTURE));
			return this;
		}

		public Builder xeiButton(int x, int y) {
			ButtonWidget widget = new ButtonWidget(px(x), px(y), px(SLOT), px(SLOT), null);

			widget.setId("ui:xei_lookup");
			group.addWidget(widget);

			return this;
		}

		public Builder text(int x, int y, int width, int height, Function<MBDMachine, Component> factory) {
			TextTextureWidget widget = textWidget(x, y, width, height);

			widget.setText(() -> {
				Component text = factory.apply(machine);

				return text == null ? Component.empty() : text;
			});

			group.addWidget(widget);

			return this;
		}

		public Builder label(int x, int y, int width, int height, Component text) {
			TextTextureWidget widget = textWidget(x, y, width, height);

			widget.setText(text);
			group.addWidget(widget);

			return this;
		}

		public Builder steamBar(int x, int y, int width, int height) {
			return steamBar(AbstractSteamMachine.STEAM_TRAIT_NAME, x, y, width, height);
		}

		public Builder steamBar(String tankTraitName, int x, int y, int width, int height) {
			FluidTankCapabilityTrait trait = trait(machine, FluidTankCapabilityTrait.class, tankTraitName);

			group.addWidget(new ProgressWidget(() -> {
				return (double) trait.storages[0].getFluidAmount() / Math.max(1, trait.storages[0].getCapacity());
			}, px(x), px(y), px(width), px(height)));
			return this;
		}

		public Builder steamBar(DoubleSupplier fillRatio, int x, int y, int width, int height) {
			group.addWidget(new ProgressWidget(fillRatio, px(x), px(y), px(width), px(height)));
			return this;
		}

		public Builder button(int x, int y, int width, int height, Consumer<ClickData> onClick) {
			group.addWidget(new ButtonWidget(px(x), px(y), px(width), px(height), onClick));
			return this;
		}

		public Builder image(int x, int y, int width, int height, ResourceLocation texture) {
			group.addWidget(new ImageWidget(px(x), px(y), px(width), px(height), new ResourceTexture(texture)));
			return this;
		}

		public Builder widget(Widget widget) {
			group.addWidget(widget);
			return this;
		}

		public WidgetGroup build() {
			group.setSize(px(logicalWidth), px(logicalHeight));
			MBDHelpers.bindUI(machine, group);
			return group;
		}

		private static int indexOf(String id) {
			if (id == null) {
				return -1;
			}

			int split = id.lastIndexOf('_');

			if (split < 0 || split == id.length() - 1) {
				return -1;
			}

			try {
				return Integer.parseInt(id.substring(split + 1));
			} catch (NumberFormatException exception) {
				return -1;
			}
		}

		private static <T> T trait(MBDMachine machine, Class<T> type, String name) {
			T trait = machine.getTraitByName(type, name);

			if (trait == null) {
				throw new IllegalArgumentException(String.format(
						"UI: trait not found: %s (%s) on %s",
						name,
						type.getSimpleName(),
						machine.getDefinition().id()
				));
			}

			return trait;
		}
	}
}