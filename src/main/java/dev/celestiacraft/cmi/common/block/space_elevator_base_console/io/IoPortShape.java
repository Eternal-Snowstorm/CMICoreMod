package dev.celestiacraft.cmi.common.block.space_elevator_base_console.io;

import java.util.EnumMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum IoPortShape implements StringRepresentable {
	FULL(
			"full",
			Shapes.block()
	),
	TOP_BORDER(
			"top_border",
			Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 3.0D / 16.0D, 1.0D)
	),
	TOP_CENTER(
			"top_center",
			Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D / 16.0D, 1.0D)
	),
	IO_RIGHT(
			"io_right",
			Shapes.or(
					Shapes.box(14.0D / 16.0D, 4.0D / 16.0D, 4.0D / 16.0D, 1.0D, 12.0D / 16.0D, 12.0D / 16.0D),
					Shapes.box(8.0D / 16.0D, 5.0D / 16.0D, 5.0D / 16.0D, 14.0D / 16.0D, 11.0D / 16.0D, 11.0D / 16.0D)
			)
	),
	IO_LEFT(
			"io_left",
			Shapes.or(
					Shapes.box(0.0D, 4.0D / 16.0D, 4.0D / 16.0D, 2.0D / 16.0D, 12.0D / 16.0D, 12.0D / 16.0D),
					Shapes.box(2.0D / 16.0D, 5.0D / 16.0D, 5.0D / 16.0D, 7.0D / 16.0D, 11.0D / 16.0D, 11.0D / 16.0D)
			)
	),
	SCREEN_CENTER(
			"screen_center",
			Shapes.box(1.0D / 16.0D, 1.0D / 16.0D, 13.0D / 16.0D, 15.0D / 16.0D, 11.0D / 16.0D, 15.0D / 16.0D),
			Shapes.block()
	),
	INVISIBLE(
			"invisible",
			Shapes.block()
	);

	private static final Map<Direction, Map<IoPortShape, VoxelShape>> OUTLINE_SHAPES = new EnumMap<>(Direction.class);
	private static final Map<Direction, Map<IoPortShape, VoxelShape>> SOLID_SHAPES = new EnumMap<>(Direction.class);

	static {
		for (Direction facing : Direction.Plane.HORIZONTAL) {
			Map<IoPortShape, VoxelShape> outlines = new EnumMap<>(IoPortShape.class);
			Map<IoPortShape, VoxelShape> solids = new EnumMap<>(IoPortShape.class);
			for (IoPortShape shape : values()) {
				outlines.put(shape, rotate(shape.outline, facing));
				solids.put(shape, rotate(shape.solid, facing));
			}
			OUTLINE_SHAPES.put(facing, outlines);
			SOLID_SHAPES.put(facing, solids);
		}
	}

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

	// 形状随控制器水平朝向旋转, 基准朝向为南
	public VoxelShape shape(@NotNull Direction facing) {
		return OUTLINE_SHAPES.get(horizontal(facing)).get(this);
	}

	public VoxelShape solidShape(@NotNull Direction facing) {
		return SOLID_SHAPES.get(horizontal(facing)).get(this);
	}

	private static Direction horizontal(@NotNull Direction facing) {
		if (facing.getAxis().isHorizontal()) {
			return facing;
		}
		return Direction.SOUTH;
	}

	private static VoxelShape rotate(VoxelShape shape, @NotNull Direction facing) {
		if (facing == Direction.SOUTH) {
			return shape;
		}
		VoxelShape rotated = Shapes.empty();
		for (AABB box : shape.toAabbs()) {
			rotated = Shapes.or(rotated, Shapes.create(rotate(box, facing)));
		}
		return rotated;
	}

	// 绕方块中心旋转包围盒, 与端口偏移的旋转方向保持一致
	private static AABB rotate(AABB box, @NotNull Direction facing) {
		switch (facing) {
			case WEST:
				return new AABB(1.0D - box.maxZ, box.minY, box.minX, 1.0D - box.minZ, box.maxY, box.maxX);
			case NORTH:
				return new AABB(1.0D - box.maxX, box.minY, 1.0D - box.maxZ, 1.0D - box.minX, box.maxY, 1.0D - box.minZ);
			case EAST:
				return new AABB(box.minZ, box.minY, 1.0D - box.maxX, box.maxZ, box.maxY, 1.0D - box.minX);
			default:
				return box;
		}
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}
}
