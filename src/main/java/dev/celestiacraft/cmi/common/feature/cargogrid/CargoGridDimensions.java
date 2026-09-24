package dev.celestiacraft.cmi.common.feature.cargogrid;

public record CargoGridDimensions(int width, int height) {
	public static final CargoGridDimensions UNIT = new CargoGridDimensions(1, 1);

	public CargoGridDimensions {
		if (width < 1) width = 1;
		if (height < 1) height = 1;
	}

	public CargoGridDimensions mergeMax(CargoGridDimensions other) {
		if (other == null) return this;
		return new CargoGridDimensions(Math.max(width, other.width), Math.max(height, other.height));
	}

	public int area() {
		return width * height;
	}
}