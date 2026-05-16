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
	)),
	SCREEN_CENTER("screen_center",
			Shapes.box(1.0D / 16.0D, 1.0D / 16.0D, 13.0D / 16.0D, 15.0D / 16.0D, 11.0D / 16.0D, 15.0D / 16.0D),
			Shapes.block());

	private final String name;
	private final VoxelShape outline;
	private final VoxelShape solid;

	IoPortShape(String name, VoxelShape shape) {
		this(name, shape, shape);
	}

	IoPortShape(String name, VoxelShape outline, VoxelShape solid) {
		this.name = name;
		this.outline = outline;
		this.solid = solid;
	}

	public VoxelShape shape() {
		return outline;
	}

	public VoxelShape solidShape() {
		return solid;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
