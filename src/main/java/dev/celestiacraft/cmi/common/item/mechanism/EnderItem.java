package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnderItem extends MechanismItem {
	public EnderItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();
		BlockPos pos = player.blockPosition();

		if (level.isClientSide()) {
			return;
		}

		if (stack.getItem() instanceof EnderItem item) {
			ServerLevel serverLevel = (ServerLevel) level;

			if (player.isCrouching()) {
				if (stack.hasTag()) {
					return;
				}

				// 写入NBT
				CompoundTag tag = new CompoundTag();
				tag.putInt("x", pos.getX());
				tag.putInt("y", pos.getY());
				tag.putInt("z", pos.getZ());
				tag.putString("dim", serverLevel.dimension().location().toString());

				stack.setTag(tag);

				player.swing(InteractionHand.MAIN_HAND, true);

				// 提示玩家已存储坐标
				player.sendSystemMessage(Component.translatable("promp.cmi.ender_mechanism.location_stored"));

				return;
			}

			if (stack.hasTag()) {
				event.setCanceled(true);
				return;
			}

			int range = 2;
			RandomSource random = player.getRandom();

			double targetX = player.getX() + random.nextInt(range * 2 + 1) - range;
			double targetY = player.getY();
			double targetZ = player.getZ() + random.nextInt(range * 2 + 1) - range;

			player.teleportTo(targetX, targetY, targetZ);
			player.swing(InteractionHand.MAIN_HAND, true);

			serverLevel.playSound(
					null,
					targetX,
					targetY,
					targetZ,
					SoundEvents.ENDERMAN_TELEPORT,
					SoundSource.PLAYERS,
					1.0F,
					1.0F
			);

			serverLevel.sendParticles(
					ParticleTypes.DRAGON_BREATH,
					targetX,
					targetY,
					targetZ,
					50,
					0.5,
					0.5,
					0.5,
					0.1
			);

			player.getCooldowns().addCooldown(stack.getItem(), 20);
		}
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		ItemStack mechanism = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		boolean hasTag = mechanism.hasTag();
		BlockState state = context.getLevel().getBlockState(pos);

		if (!context.getLevel().isClientSide()) {
			Level level = null;
			level = context.getPlayer().level();
			if (hasTag && isAccelerator(state)) {
				CompoundTag tag = mechanism.getTag();
				double destinationX = tag.getInt("x");
				double destinationY = tag.getInt("y");
				double destinationZ = tag.getInt("z");
				String destinationDim = tag.getString("dim");


				if (player != null) {
					if (level.dimension().location().toString().equals(destinationDim)) {
						level.playSound(
								null,
								pos.getX(),
								pos.getY(),
								pos.getZ(),
								SoundEvents.PORTAL_TRAVEL,
								SoundSource.PLAYERS,
								0.3F,
								1.0F
						);
						level.addParticle(
								ParticleTypes.DRAGON_BREATH,
								pos.getX(),
								pos.getY() + 1,
								pos.getZ(),
								0.5,
								0.5,
								0.5
						);
						player.teleportTo(destinationX + 0.5, destinationY, destinationZ + 0.5);
						player.getCooldowns().addCooldown(mechanism.getItem(), 40);
						mechanism.setTag(null);
						return InteractionResult.SUCCESS;
					} else {
						player.sendSystemMessage(Component.translatable("promp.cmi.ender_mechanism.teleport_failed"));
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	private boolean isAccelerator(BlockState self) {
		return self.is(CmiBlock.ACCELERATOR.getDefaultState().getBlock());
	}

}