package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum IoPortShape implements StringRepresentable {
	FULL("full", Shapes.block()),
	TOP_BORDER("top_border", Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 3.0D / 16.0D, 1.0D)),
	TOP_CENTER("top_center", Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D / 16.0D, 1.0D)),
	IO_RIGHT("io_right", Shapes.or(
			Shapes.box(14.0D / 16.0D, 4.0D / 16.0D, 4.0D / 16.0D, 16.0D / 16.0D, 12.0D / 16.0D, 12.0D / 16.0D),
			Shapes.box(8.0D / 16.0D, 5.0D / 16.0D, 5.0D / 16.0D, 14.0D / 16.0D, 11.0D / 16.0D, 11.0D / 16.0D)
	)),
	IO_LEFT("io_left", Shapes.or(
			Shapes.box(0.0D, 4.0D / 16.0D, 4.0D / 16.0D, 2.0D / 16.0D, 12.0D / 16.0D, 12.0D / 16.0D),
			Shapes.box(2.0D / 16.0D, 5.0D / 16.0D, 5.0D / 16.0D, 7.0D / 16.0D, 11.0D / 16.0D, 11.0D / 16.0D)
	));

	private final String name;
	private final VoxelShape shape;

	IoPortShape(String name, VoxelShape shape) {
		this.name = name;
		this.shape = shape;
	}

	public VoxelShape shape() {
		return shape;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
