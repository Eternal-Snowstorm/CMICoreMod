package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import net.minecraft.core.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SpaceElevatorBaseConsoleRenderer extends GeoBlockRenderer<SpaceElevatorBaseConsoleBlockEntity> {
	public SpaceElevatorBaseConsoleRenderer() {
		super(new SpaceElevatorBaseConsoleModel());
	}

	@Override
	protected Direction getFacing(SpaceElevatorBaseConsoleBlockEntity block) {
		return Direction.SOUTH;
	}
}
