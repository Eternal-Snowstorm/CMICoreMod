package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StoneItem extends MechanismItem {
	public StoneItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);

		if (level.isClientSide()) {
			return;
		}

		if (stack.getItem() instanceof PotionItem item) {
			if (event.getHand() != InteractionHand.MAIN_HAND && !state.is(Blocks.COBBLESTONE)) {
				level.setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
				player.swing(event.getHand());
			}
		}
	}
}