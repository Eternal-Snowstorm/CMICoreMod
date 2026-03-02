package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CopperItem extends MechanismItem {
	public CopperItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();

		if (level.isClientSide()) {
			return;
		}

		if (stack.getItem() instanceof CopperItem item) {
			// 射线检测
			HitResult hitResult = player.pick(5.0D, 0.0F, false);
			if (hitResult instanceof BlockHitResult blockHit) {
				BlockState state = level.getBlockState(blockHit.getBlockPos());
				if (state.is(CmiBlock.ACCELERATOR_BLOCK.get())) {
					player.swing(event.getHand());
					return;
				}
			}

			// 创建投掷水瓶
			ThrownPotion potion = new ThrownPotion(level, player);

			ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
			PotionUtils.setPotion(potionStack, Potions.WATER);
			potion.setItem(potionStack);

			potion.shootFromRotation(
					player,
					player.getXRot(),
					player.getYRot(),
					0.0F,
					1.0F,
					1.0F
			);

			level.addFreshEntity(potion);

			level.playSound(
					null,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.ARROW_SHOOT,
					SoundSource.PLAYERS,
					0.5F,
					0.3F
			);

			player.swing(event.getHand());

			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}
}
