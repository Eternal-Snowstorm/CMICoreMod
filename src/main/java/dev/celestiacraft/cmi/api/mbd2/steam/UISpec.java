package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.item.ItemSlotCapabilityTrait;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

/**
 * 机器 GUI 的链式 DSL：坐标 + 按 trait 名自动绑定，常用件自动接 machine。
 *
 * <pre>{@code
 * UISpec.create(machine, 176, 166, (builder) -> {
 *     builder.background("ldlib:textures/gui/background.png")
 *             .title(70, 5)
 *             .slot("input", 40, 42)
 *             .tank("steam", 141, 22, 18, 58)
 *             .progressBar(79, 42)
 *             .steamBar(60, 20, 18, 52)
 *             .button(78, 42, 18, 18, (click) -> {
 *
 *             });
 * });
 * }</pre>
 */
public class UISpec {
	public static WidgetGroup create(MBDMachine machine, int width, int height, Consumer<Builder> config) {
		Builder builder = new Builder(machine, width, height);
		config.accept(builder);
		return builder.build();
	}

	public static class Builder {
		private final MBDMachine machine;
		private final WidgetGroup group;

		private Builder(MBDMachine machine, int width, int height) {
			this.machine = machine;
			group = new WidgetGroup(0, 0, width, height);
		}

		/**
		 * 整体背景贴图
		 *
		 * @param texture
		 * @return
		 */
		public Builder background(String texture) {
			group.setBackground(new ResourceTexture(texture));
			return this;
		}

		/**
		 * 整体背景贴图
		 *
		 * @param texture
		 * @return
		 */
		public Builder background(ResourceLocation texture) {
			group.setBackground(new ResourceTexture(texture));
			return this;
		}

		/**
		 * 机器名标题 (文本由 MB2 自动填, id 挂载 ui:machine_name)
		 *
		 * @param x
		 * @param y
		 * @return
		 */
		public Builder title(int x, int y) {
			group.addWidget(new TextTextureWidget(x, y, 40, 20)
					.setId("ui:machine_name"));
			return this;
		}

		/**
		 * 物品槽: 按 trait 名自动绑 storage
		 *
		 * @param traitName
		 * @param x
		 * @param y
		 * @return
		 */
		public Builder slot(String traitName, int x, int y) {
			return slot(traitName, x, y, 18, 18);
		}

		public Builder slot(String traitName, int x, int y, int widget, int height) {
			ItemSlotCapabilityTrait trait = trait(machine, ItemSlotCapabilityTrait.class, traitName);
			SlotWidget slot = new SlotWidget(trait.storage, 0, x, y);
			slot.setSize(widget, height);
			group.addWidget(slot);
			return this;
		}

		/**
		 * 流体槽: 按 trait 名自动绑 storages[0]
		 *
		 * @param traitName
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 * @return
		 */
		public Builder tank(String traitName, int x, int y, int width, int height) {
			FluidTankCapabilityTrait trait = trait(machine, FluidTankCapabilityTrait.class, traitName);
			group.addWidget(new TankWidget(trait.storages[0], x, y, width, height, true, true));
			return this;
		}

		/**
		 * 配方进度条 (自动 getProgressPercent)
		 *
		 * @param x
		 * @param y
		 * @return
		 */
		public Builder progressBar(int x, int y) {
			return progressBar(x, y, 18, 18);
		}

		public Builder progressBar(int x, int y, int width, int height) {
			group.addWidget(new ProgressWidget(() -> {
				return machine.getRecipeLogic().getProgressPercent();
			}, x, y, width, height));
			return this;
		}

		/**
		 * 蒸汽水位条 (默认读 "steam" trait)
		 *
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 * @return
		 */
		public Builder steamBar(int x, int y, int width, int height) {
			return steamBar(AbstractSteamMachine.STEAM_TRAIT_NAME, x, y, width, height);
		}

		/** 按控制器自身 trait 名显示水位 (单方块用; 多方块控制器没有 steam trait 会抛异常) */
		public Builder steamBar(String tankTraitName, int x, int y, int width, int height) {
			FluidTankCapabilityTrait trait = trait(machine, FluidTankCapabilityTrait.class, tankTraitName);
			group.addWidget(new ProgressWidget(() -> {
				return (double) trait.storages[0].getFluidAmount() / Math.max(1, trait.storages[0].getCapacity());
			}, x, y, width, height));
			return this;
		}

		/** 自定义水位 supplier (多方块聚合水位等场景) */
		public Builder steamBar(DoubleSupplier fillRatio, int x, int y, int width, int height) {
			group.addWidget(new ProgressWidget(fillRatio, x, y, width, height));
			return this;
		}

		public Builder button(int x, int y, int widget, int height, Consumer<ClickData> onClick) {
			group.addWidget(new ButtonWidget(x, y, widget, height, onClick));
			return this;
		}

		public Builder image(int x, int y, int widget, int height, String texture) {
			group.addWidget(new ImageWidget(x, y, widget, height, new ResourceTexture(texture)));
			return this;
		}

		public Builder image(int x, int y, int widget, int height, ResourceLocation texture) {
			group.addWidget(new ImageWidget(x, y, widget, height, new ResourceTexture(texture)));
			return this;
		}

		/**
		 * 自定义 widget
		 *
		 * @param widget
		 * @return
		 */
		public Builder widget(Widget widget) {
			group.addWidget(widget);
			return this;
		}

		public WidgetGroup build() {
			return group;
		}

		private static <T> T trait(MBDMachine machine, Class<T> type, String name) {
			T trait = machine.getTraitByName(type, name);
			if (trait == null) {
				String errorMessage = String.format(
						"UI: trait not found: %s (%s) on %s",
						name,
						type.getSimpleName(),
						machine.getDefinition().id()
				);
				throw new IllegalArgumentException(errorMessage);
			}
			return trait;
		}
	}
}