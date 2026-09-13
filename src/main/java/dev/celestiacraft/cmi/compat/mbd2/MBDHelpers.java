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

	public static <D extends MBDMachineDefinition> D attachUI(D definition, Function<MBDMachine, WidgetGroup> uiCreator) {
		setPrivateField(definition, "uiCreator", (Function<MBDMachine, WidgetGroup>) (machine) -> {
			WidgetGroup group = uiCreator.apply(machine);
			return group != null ? group : new WidgetGroup(0, 0, 176, 166);
		});
		return definition;
	}

	public static <D extends MBDMachineDefinition> D attachUIPlaceholder(D definition) {
		return attachUI(definition, (machine) -> {
			return new WidgetGroup(0, 0, 176, 166);
		});
	}

	public static void setPrivateField(Object target, String fieldName, Object value) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (ReflectiveOperationException exception) {
			String message = String.format(
					"Failed to set private field %s on %s %s",
					fieldName,
					target.getClass(),
					exception
			);
			throw new RuntimeException(message);
		}
	}
}