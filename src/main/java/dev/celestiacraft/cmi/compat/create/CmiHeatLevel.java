package dev.celestiacraft.cmi.compat.create;

public enum CmiHeatLevel {
	GRILLED("grilled"),
	HEATED("heated"),
	SUPERHEATED("superheated");

	private final String id;

	CmiHeatLevel(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	@Override
	public String toString() {
		return id;
	}
}