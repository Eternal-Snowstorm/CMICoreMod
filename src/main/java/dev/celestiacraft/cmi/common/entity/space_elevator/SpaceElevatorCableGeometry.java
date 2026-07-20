package dev.celestiacraft.cmi.common.entity.space_elevator;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class SpaceElevatorCableGeometry {
	public static final double GROUND_DOCK_Y_OFFSET = 2.01D;
	public static final double ORBIT_DOCK_Y_OFFSET = -3.75D;

	private static final double CABLE_X_OFFSET = 33.24264D / 16.0D;
	private static final double CABLE_CONNECTION_Y_OFFSET = 4.0D / 16.0D;
	private static final double CABLE_Z_OFFSET = 34.24264D / 16.0D;
	private static final Vec3[] CABLE_OFFSETS = new Vec3[] {
			new Vec3(-CABLE_X_OFFSET, CABLE_CONNECTION_Y_OFFSET, -CABLE_Z_OFFSET),
			new Vec3(-CABLE_X_OFFSET, CABLE_CONNECTION_Y_OFFSET, CABLE_Z_OFFSET),
			new Vec3(CABLE_X_OFFSET, CABLE_CONNECTION_Y_OFFSET, CABLE_Z_OFFSET),
			new Vec3(CABLE_X_OFFSET, CABLE_CONNECTION_Y_OFFSET, -CABLE_Z_OFFSET)
	};

	private SpaceElevatorCableGeometry() {
	}

	public static int cableCount() {
		return CABLE_OFFSETS.length;
	}

	public static Vec3 cableOffset(int index) {
		return CABLE_OFFSETS[Mth.clamp(index, 0, CABLE_OFFSETS.length - 1)];
	}
}
