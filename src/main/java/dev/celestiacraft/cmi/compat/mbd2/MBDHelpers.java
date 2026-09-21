package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.lowdraglib.gui.widget.TextTextureWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.trait.ITrait;
import com.lowdragmc.mbd2.common.trait.IUIProviderTrait;
import com.lowdragmc.mbd2.common.trait.TraitDefinition;
import com.lowdragmc.mbd2.utils.WidgetUtils;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public class MBDHelpers {
	public static final String traitNameFilter = "traitNameFilter";

	// 占位界面尺寸, 与 mb2 默认机器界面一致
	private static final int UI_WIDTH = 176;
	private static final int UI_HEIGHT = 166;

	public static boolean isMachine(MBDMachine machine, ResourceLocation name) {
		MBDMachineDefinition definition = machine.getDefinition();
		ResourceLocation id = definition.id();

		return name.equals(id);
	}

	/**
	 * 给 definition 注入 uiCreator。
	 * <p>
	 * 纯代码机器 (不是可视化编辑器/NBT 产物) 的 uiCreator 是 null, 而 hasUI 默认为 true,
	 * MBDMachine.createUI 会先调用 uiCreator 再 post MachineUIEvent -> 必须先注入, 否则 NPE。
	 * <p>
	 * 走 Java 方法参数 (Function) 而不是让 JS 直接 setPrivateField:
	 * Field.set(Object, Object) 没有目标类型, Rhino 不会把 JS 箭头函数适配成 Function,
	 * 调用时才炸 ClassCastException。
	 *
	 * @param creator 可以为 null, 此时只注入一个占位 WidgetGroup
	 */
	public static <D extends MBDMachineDefinition> D attachUI(D definition, Function<MBDMachine, WidgetGroup> creator) {
		setPrivateField(definition, "uiCreator", (Function<MBDMachine, WidgetGroup>) (machine) -> {
			WidgetGroup group = creator == null ? null : creator.apply(machine);
			return group != null ? group : new WidgetGroup(0, 0, UI_WIDTH, UI_HEIGHT);
		});
		return definition;
	}

	/**
	 * 只注入占位界面 (真实界面交给 MachineUIEvent / WidgetGroup.setRoot 接管)。
	 */
	public static <D extends MBDMachineDefinition> D attachUIPlaceholder(D definition) {
		return attachUI(definition, null);
	}

	/**
	 * 按 MBD2 的 id 约定接线整棵界面树 (等价 {@code MBDMachineDefinition#bindMachineUI})。
	 * <p>
	 * 为什么需要它: {@code bindMachineUI} 只在"从工程文件反序列化 UI"那条路上被调用, 而纯代码机器
	 * (KubeJS / Java 注入 uiCreator 的) 走不到那段 -> ui:machine_name / ui:progress_bar /
	 * ui:&lt;trait&gt;_&lt;下标&gt; 这些 id 就没人认领, 槽位会是空的。
	 * <p>
	 * 优先反射调 MBD2 本体 (这样 part:xxx@ui:xxx、xei_lookup 这些特殊约定跟版本一起走),
	 * 失败则退回自己接线 (机器名 + 所有 trait)。
	 */
	public static void bindUI(MBDMachine machine, WidgetGroup group) {
		MBDMachineDefinition definition = machine.getDefinition();

		try {
			Method method = findMethod(definition.getClass(), "bindMachineUI", MBDMachine.class, WidgetGroup.class);

			method.setAccessible(true);
			method.invoke(definition, machine, group);
			return;
		} catch (ReflectiveOperationException | RuntimeException exception) {
			Cmi.LOGGER.warn("MBDHelpers.bindUI: bindMachineUI 调用失败, 退回手动接线 ({})", exception.toString());
		}

		bindFallback(machine, group);
	}

	/**
	 * 手动接线 (MBD2 的 bindMachineUI 够不着时的兜底): 机器名 + 每个 trait 的 initTraitUI。
	 */
	private static void bindFallback(MBDMachine machine, WidgetGroup group) {
		WidgetUtils.widgetByIdForEach(group, "^ui:machine_name$", TextTextureWidget.class, widget -> widget.setText(() -> {
			Component name = machine.getCustomName();

			return name != null ? name : machine.getDefinition().block().getName();
		}));

		for (TraitDefinition definition : machine.getDefinition().machineSettings().traitDefinitions()) {
			if (definition instanceof IUIProviderTrait provider) {
				ITrait trait = machine.getTraitByDefinition(definition);

				if (trait != null) {
					provider.initTraitUI(trait, group);
				}
			}
		}
	}

	/**
	 * 沿父类链找方法 (bindMachineUI 是 protected, Class#getMethod 找不到)。
	 */
	private static Method findMethod(Class<?> type, String name, Class<?>... parameters) throws NoSuchMethodException {
		Class<?> current = type;

		while (current != null) {
			try {
				return current.getDeclaredMethod(name, parameters);
			} catch (NoSuchMethodException exception) {
				current = current.getSuperclass();
			}
		}

		throw new NoSuchMethodException(String.format("%s#%s", type.getName(), name));
	}

	/**
	 * 读 private 字段 (沿父类链查找), 读不到返回 null。
	 * <p>
	 * 用途: 自检某台机器的 uiCreator 到底挂上没有 —— 它是 null 的话点开界面会 NPE。
	 */
	@Nullable
	public static Object getPrivateField(Object target, String fieldName) {
		Class<?> type = target.getClass();

		while (type != null) {
			try {
				Field field = type.getDeclaredField(fieldName);

				field.setAccessible(true);
				return field.get(target);
			} catch (NoSuchFieldException exception) {
				type = type.getSuperclass();
			} catch (ReflectiveOperationException exception) {
				return null;
			}
		}

		return null;
	}

	/**
	 * 设置 private 字段, 沿父类链查找。
	 * <p>
	 * 例: uiCreator 声明在 {@link MBDMachineDefinition} 上, 而 MultiblockMachineDefinition
	 * 实例拿到的类是子类, 只用 getDeclaredField 会 NoSuchFieldException。
	 */
	public static void setPrivateField(Object target, String fieldName, Object value) {
		Class<?> type = target.getClass();

		while (type != null) {
			try {
				Field field = type.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(target, value);
				return;
			} catch (NoSuchFieldException exception) {
				type = type.getSuperclass();
			} catch (ReflectiveOperationException exception) {
				throw new RuntimeException(String.format(
						"Failed to set private field %s on %s: %s",
						fieldName,
						target.getClass().getName(),
						exception
				), exception);
			}
		}

		throw new RuntimeException(String.format(
				"Failed to set private field %s on %s: no such field in the class hierarchy",
				fieldName,
				target.getClass().getName()
		));
	}
}