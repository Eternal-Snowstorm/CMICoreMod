package dev.celestiacraft.cmi.event.encase;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.belt.*;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BeltEncaseHandler {
	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);

		if (level.isClientSide()) {
			return;
		}
		if (player == null) {
			return;
		}
		if (player.isShiftKeyDown()) {
			return;
		}
		if (!player.getItemInHand(InteractionHand.OFF_HAND).is(CmiMechanism.ANDESITE.get())) {
			return;
		}

		if (!state.is(AllBlocks.BELT.get())) {
			return;
		}

		BeltBlockEntity controller = BeltHelper.getControllerBE(level, pos);
		int beltLength = controller.beltLength;

		if (state.getValue(BeltBlock.SLOPE) == BeltSlope.VERTICAL) {
			BlockPos beltStart = pos;

			for (int i = 0; i < 20; i++) {
				BlockState beltState = level.getBlockState(beltStart);

				if (beltState.getValue(BeltBlock.PART) == BeltPart.START || beltState.getValue(BeltBlock.PART) == BeltPart.END) {
					break;
				}
				beltStart = beltStart.below();
			}
			for (int i = 0; i < beltLength; i++) {
				BeltBlockEntity targetEntity = BeltHelper.getSegmentBE(level, beltStart.above(i));

				if (targetEntity == null) {
					return;
				}
				if (stack.is(AllBlocks.ANDESITE_CASING.asItem())) {
					targetEntity.setCasingType(BeltBlockEntity.CasingType.ANDESITE);
				} else if (stack.is(AllBlocks.BRASS_CASING.asItem())) {
					targetEntity.setCasingType(BeltBlockEntity.CasingType.BRASS);
				} else if (stack.is(AllItems.WRENCH.get())) {
					event.setCanceled(true);
					player.swing(InteractionHand.MAIN_HAND);
					targetEntity.setCasingType(BeltBlockEntity.CasingType.NONE);
				}
			}
		} else {
			for (int i = 0; i < beltLength; i++) {
				BlockPos beltBlock = BeltHelper.getPositionForOffset(controller, i);
				BeltBlockEntity targetEntity = BeltHelper.getSegmentBE(level, beltBlock);

				if (targetEntity == null) {
					return;
				}
				if (stack.is(AllBlocks.ANDESITE_CASING.asItem())) {
					targetEntity.setCasingType(BeltBlockEntity.CasingType.ANDESITE);
				} else if (stack.is(AllBlocks.BRASS_CASING.asItem())) {
					targetEntity.setCasingType(BeltBlockEntity.CasingType.BRASS);
				} else if (stack.is(AllItems.WRENCH.get())) {
					event.setCanceled(true);
					player.swing(InteractionHand.MAIN_HAND);
					targetEntity.setCasingType(BeltBlockEntity.CasingType.NONE);
				}
			}
		}
	}
}
