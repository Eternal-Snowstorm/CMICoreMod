package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import net.minecraft.util.StringRepresentable;

public enum IoPortType implements StringRepresentable {
	NONE("none"),
	INPUT_FLUID("input_fluid"),
	INPUT_ITEM("input_item"),
	OUTPUT_FLUID("output_fluid"),
	OUTPUT_ITEM("output_item"),
	ENERGY_IN("energy_in");

	private final String name;

	IoPortType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
