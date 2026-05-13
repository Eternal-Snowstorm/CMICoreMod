package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import net.minecraft.core.Vec3i;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class SpaceElevatorBaseStructure {
	public static final Map<Vec3i, IoPortShape> STRUCTURE_OFFSETS;
	public static final Map<IoPortType, Vec3i> IO_PORT_OFFSETS;

	static {
		Map<Vec3i, IoPortShape> offsets = new HashMap<>();

		for (int y = 0; y <= 1; y++) {
			for (int x = -2; x <= 2; x++) {
				for (int z = -2; z <= 2; z++) {
					offsets.put(new Vec3i(x, y, z), IoPortShape.FULL);
				}
			}
		}

		for (int z = -2; z <= 2; z++) {
			offsets.put(new Vec3i(2, 2, z), IoPortShape.TOP_BORDER);
			offsets.put(new Vec3i(-2, 2, z), IoPortShape.TOP_BORDER);
		}
		for (int x = -1; x <= 1; x++) {
			offsets.put(new Vec3i(x, 2, 2), IoPortShape.TOP_BORDER);
			offsets.put(new Vec3i(x, 2, -2), IoPortShape.TOP_BORDER);
		}

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				offsets.put(new Vec3i(x, 2, z), IoPortShape.TOP_CENTER);
			}
		}

		offsets.put(new Vec3i(2, 0, 0), IoPortShape.IO_RIGHT);
		offsets.put(new Vec3i(2, 1, 0), IoPortShape.IO_RIGHT);
		offsets.put(new Vec3i(-2, 0, 0), IoPortShape.IO_LEFT);
		offsets.put(new Vec3i(-2, 1, 0), IoPortShape.IO_LEFT);

		STRUCTURE_OFFSETS = Map.copyOf(offsets);

		Map<IoPortType, Vec3i> ports = new EnumMap<>(IoPortType.class);
		ports.put(IoPortType.INPUT_FLUID, new Vec3i(-2, 1, 0));
		ports.put(IoPortType.INPUT_ITEM, new Vec3i(-2, 0, 0));
		ports.put(IoPortType.OUTPUT_FLUID, new Vec3i(2, 1, 0));
		ports.put(IoPortType.OUTPUT_ITEM, new Vec3i(2, 0, 0));
		ports.put(IoPortType.ENERGY_IN, new Vec3i(0, 0, -2));
		IO_PORT_OFFSETS = Map.copyOf(ports);
	}

	private SpaceElevatorBaseStructure() {
	}
}
