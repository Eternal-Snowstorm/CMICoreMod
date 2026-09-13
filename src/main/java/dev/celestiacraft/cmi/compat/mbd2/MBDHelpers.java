package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.mbd2.api.recipe.ingredient.FluidIngredient;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.function.Function;

public class MBDHelpers {
	public static final String traitNameFilter = "traitNameFilter";

	// 占位界面尺寸, 与 mb2 默认机器界面一致
	private static final int UI_WIDTH = 176;
	private static final int UI_HEIGHT = 166;

	public static FluidIngredient withFluidTag(ResourceLocation tag, long amount, CompoundTag nbt) {
		return MBDFluidIngredient.ofTagId(tag, amount, nbt);
	}

	public static FluidIngredient withFluidTag(ResourceLocation tag, long amount) {
		return withFluidTag(tag, amount, null);
	}

	public static FluidIngredient withFluidTag(ResourceLocation tag) {
		return withFluidTag(tag, 1000, null);
	}

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