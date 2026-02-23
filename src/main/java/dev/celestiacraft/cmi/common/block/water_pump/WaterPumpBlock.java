package dev.celestiacraft.cmi.common.block.water_pump;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.block.IBE;
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
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;
import dev.celestiacraft.cmi.common.register.CmiBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WaterPumpBlock extends Block implements IBE<WaterPumpBlockEntity> {
	public WaterPumpBlock(Properties properties) {
		super(Properties.copy(Blocks.OAK_PLANKS));
	}

	@Override
	public Class<WaterPumpBlockEntity> getBlockEntityClass() {
		return WaterPumpBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends WaterPumpBlockEntity> getBlockEntityType() {
		return CmiBlockEntityTypes.WATER_PUMP.get();
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		Player player = event.getEntity();
		InteractionHand hand = event.getHand();
		ItemStack item = player.getItemInHand(hand);
		BlockState state = level.getBlockState(pos);

		if (!level.isClientSide()) {
			return;
		}

		boolean isBlockAndItem = state.is(CmiBlocks.WATER_PUMP.get())
				&& item.is(AllTags.AllItemTags.WRENCH.tag);

		if (isBlockAndItem && hand == InteractionHand.MAIN_HAND && !player.isShiftKeyDown()) {
			WaterPumpBlockEntity entity = (WaterPumpBlockEntity) level.getBlockEntity(pos);
			if (entity != null) {
				player.swing(hand);
				entity.showMultiblock();
			}
		}
	}
}