package dev.celestiacraft.cmi.common.block.space_elevator_base_console.render;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockEntity;
import net.minecraft.core.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SpaceElevatorBaseConsoleRenderer extends GeoBlockRenderer<SpaceElevatorBaseConsoleBlockEntity> {
	public SpaceElevatorBaseConsoleRenderer() {
		super(new SpaceElevatorBaseConsoleModel());
		addRenderLayer(new SpaceElevatorScreenLayer(this));
	}

	@Override
	protected Direction getFacing(SpaceElevatorBaseConsoleBlockEntity block) {
		return Direction.SOUTH;
	}
}