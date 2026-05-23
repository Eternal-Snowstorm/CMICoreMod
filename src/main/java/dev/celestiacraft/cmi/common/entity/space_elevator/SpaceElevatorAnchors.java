package dev.celestiacraft.cmi.common.entity.space_elevator;

import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class SpaceElevatorAnchors {
	private SpaceElevatorAnchors() {
	}

	public static boolean isValidAnchor(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(CmiBlock.SPACE_ELEVATOR_BASE_CONSOLE.get())
				|| level.getBlockState(pos).is(CmiBlock.SPACE_ELEVATOR_TOP.get());
	}

	public static boolean isTopAnchor(Level level, BlockPos pos) {
		return level.getBlockState(pos).is(CmiBlock.SPACE_ELEVATOR_TOP.get());
	}

	@Nullable
	public static ElevatorEnergyAnchor findEnergySource(ServerLevel level, BlockPos anchorPos) {
		BlockEntity be = level.getBlockEntity(anchorPos);
		return be instanceof ElevatorEnergyAnchor anchor ? anchor : null;
	}

	public static int getEnergyStored(ElevatorEnergyAnchor source) {
		return source.getEnergyStored();
	}

	public static boolean consumeLaunchEnergy(ElevatorEnergyAnchor source) {
		return source.consumeLaunchEnergy();
	}

	public static int getLaunchEnergyCost(ElevatorEnergyAnchor source) {
		return source.getLaunchEnergyCost();
	}

	public static void onElevatorArrived(ServerLevel level, BlockPos anchorPos) {
		if (level.getBlockEntity(anchorPos) instanceof SpaceElevatorTopBlockEntity top) {
			top.playCloseDoor();
		}
	}

	public static void onElevatorDeparting(ServerLevel level, BlockPos anchorPos) {
		if (level.getBlockEntity(anchorPos) instanceof SpaceElevatorTopBlockEntity top) {
			top.playOpenDoor();
		}
	}
}
