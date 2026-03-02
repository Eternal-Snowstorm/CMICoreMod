package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoilItem extends MechanismItem {
	public CoilItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);

		if (!(stack.getItem() instanceof CoilItem item)) {
			if (state.is(CmiBlock.ACCELERATOR_BLOCK.get())) {
				LightningBolt LIGHTNING = EntityType.LIGHTNING_BOLT.create(level);

				LIGHTNING.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				level.addFreshEntity(LIGHTNING);
			}
		}
	}
}
