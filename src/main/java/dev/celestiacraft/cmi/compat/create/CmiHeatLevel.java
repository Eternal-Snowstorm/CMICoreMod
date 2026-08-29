package dev.celestiacraft.cmi.compat.create;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CmiHeatLevel {
	GRILLED("grilled"),
	HEATED("heated"),
	SUPERHEATED("superheated");

	private final String id;

	@Override
	public String toString() {
		return id;
	}
}