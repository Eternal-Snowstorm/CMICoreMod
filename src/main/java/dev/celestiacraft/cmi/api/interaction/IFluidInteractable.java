package dev.celestiacraft.cmi.api.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public interface IFluidInteractable {
	InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result);

	default boolean useFluidInteraction() {
		return false;
	}

	default boolean creativeUseFluidInteraction() {
		return false;
	}

	default boolean canUseFluidInteraction(Player player) {
		if (player == null) {
			return false;
		}

		return useFluidInteraction() || (player.isCreative() && creativeUseFluidInteraction());
	}

	default InteractionResult tryFluidInteraction(
			Player player,
			InteractionHand hand,
			Level level,
			BlockPos pos,
			BlockHitResult result
	) {
		if (player == null) {
			return InteractionResult.PASS;
		}

		if (canUseFluidInteraction(player) && FluidUtil.interactWithFluidHandler(
				player,
				hand,
				level,
				pos,
				result.getDirection()
		)) {
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}