package dev.celestiacraft.cmi.common.entity.space_elevator;

import lombok.Getter;

@Getter
public enum SpaceElevatorConsoleDisplayState {
	READY("ready"),
	COUNTDOWN("countdown"),
	ASCENDING("ascending"),
	APPROACHING_STATION("approaching_station"),
	DOCKED("docked");

	private final String serializedName;

	SpaceElevatorConsoleDisplayState(String serializedName) {
		this.serializedName = serializedName;
	}

	public static SpaceElevatorConsoleDisplayState fromSerializedName(String serializedName) {
		for (SpaceElevatorConsoleDisplayState state : values()) {
			if (state.serializedName.equals(serializedName)) {
				return state;
			}
		}
		return READY;
	}
}
