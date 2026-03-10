package dev.celestiacraft.cmi.compat.create;

import lombok.Getter;

@Getter
public enum CmiHeatLevel {
	GRILLED("grilled"),
	HEATED("heated"),
	SUPERHEATED("superheated");

	private final String id;

	CmiHeatLevel(String id) {
		this.id = id;
	}

	public String toString() {
		return id;
	}
}