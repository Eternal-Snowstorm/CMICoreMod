package dev.celestiacraft.cmi.event.encase;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BeltEncaseHandler {

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		Player player = event.getEntity();

		if (level.isClientSide()) {
			return;
		}
		if (player == null) {
			return;
		}
		if (player.isShiftKeyDown()) {
			return;
		}

		ItemStack stack = event.getItemStack();
		BlockState state = level.getBlockState(event.getPos());
		Object part = state.getValue(BeltBlock.PART);
		Object horizontalFacing = state.getValue(BeltBlock.HORIZONTAL_FACING);
		Object slope = state.getValue(BeltBlock.SLOPE);
		boolean yBelt = slope == BeltSlope.VERTICAL;
		boolean yAxis = slope != BeltSlope.HORIZONTAL;
		Object beltCasingType = BeltBlockEntity.CasingType.NONE;

	}
}
