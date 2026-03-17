package dev.celestiacraft.cmi.api.register.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class UseContext {
	public final Level level;
	public final BlockPos pos;
	public final Player player;
	public final InteractionHand hand;
	public final BlockState state;
	public final BlockHitResult hit;

	public UseContext(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		this.state = state;
		this.level = level;
		this.pos = pos;
		this.player = player;
		this.hand = hand;
		this.hit = result;
	}

	public boolean isClient() {
		return level.isClientSide();
	}

	public ItemStack getItem() {
		return player.getItemInHand(hand);
	}
}