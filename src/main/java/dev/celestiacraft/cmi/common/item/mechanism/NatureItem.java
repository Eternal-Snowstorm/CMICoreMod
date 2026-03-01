package dev.celestiacraft.cmi.common.item.mechanism;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NatureItem extends MechanismItem {
	public NatureItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		ItemStack item = event.getItemStack();
		Player player = event.getEntity();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);

		if (level.isClientSide()) {
			return;
		}

		if (item.getItem() instanceof NatureItem nature) {
			BlockHitResult result = new BlockHitResult(
					event.getHitVec().getLocation(),
					event.getFace(),
					pos,
					false
			);

			UseOnContext context = new UseOnContext(
					level,
					player,
					event.getHand(),
					nature.getDefaultInstance(),
					result
			);

			ACItemRegistry.FERTILIZER.get().useOn(context);
			player.swing(event.getHand());
		}
	}
}