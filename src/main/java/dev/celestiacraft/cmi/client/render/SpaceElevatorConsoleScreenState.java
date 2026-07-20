package dev.celestiacraft.cmi.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class SpaceElevatorConsoleScreenState {
	private static final int ACTIVE_DISPLAY_TICKS = 22;
	private static final int FINISHED_DISPLAY_TICKS = 100;
	private static final int CONSTRUCTION_DISPLAY_TICKS = 40;
	private static final Map<BlockPos, TransferState> TRANSFER_STATES = new HashMap<>();
	private static final Map<BlockPos, Long> CONSTRUCTION_STATES = new HashMap<>();

	private SpaceElevatorConsoleScreenState() {
	}

	public static void markTransfer(BlockPos pos) {
		long gameTime = getClientGameTime();
		TRANSFER_STATES.put(pos.immutable(), new TransferState(gameTime + ACTIVE_DISPLAY_TICKS, gameTime + ACTIVE_DISPLAY_TICKS + FINISHED_DISPLAY_TICKS));
	}

	public static boolean isTransferActive(BlockPos pos) {
		TransferState state = TRANSFER_STATES.get(pos);
		return state != null && getClientGameTime() < state.activeUntilTick;
	}

	public static boolean isTransferFinished(BlockPos pos) {
		TransferState state = TRANSFER_STATES.get(pos);
		if (state == null) {
			return false;
		}
		long gameTime = getClientGameTime();
		if (gameTime >= state.finishedUntilTick) {
			cleanupExpired(gameTime);
			return false;
		}
		return gameTime >= state.activeUntilTick;
	}

	public static void markConstruction(BlockPos pos) {
		long gameTime = getClientGameTime();
		CONSTRUCTION_STATES.put(pos.immutable(), gameTime + CONSTRUCTION_DISPLAY_TICKS);
	}

	public static boolean isConstructionActive(BlockPos pos) {
		Long activeUntilTick = CONSTRUCTION_STATES.get(pos);
		if (activeUntilTick == null) {
			return false;
		}
		if (getClientGameTime() < activeUntilTick) {
			return true;
		}
		CONSTRUCTION_STATES.remove(pos);
		return false;
	}

	private static long getClientGameTime() {
		if (Minecraft.getInstance().level == null) {
			return 0L;
		}
		return Minecraft.getInstance().level.getGameTime();
	}

	private static void cleanupExpired(long gameTime) {
		Iterator<Map.Entry<BlockPos, TransferState>> iterator = TRANSFER_STATES.entrySet().iterator();
		while (iterator.hasNext()) {
			if (gameTime >= iterator.next().getValue().finishedUntilTick) {
				iterator.remove();
			}
		}
	}

	private record TransferState(long activeUntilTick, long finishedUntilTick) {
	}
}
