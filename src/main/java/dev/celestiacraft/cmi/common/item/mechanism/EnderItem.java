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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
		BlockPos positionClicked = event.getPos();

		if (level.isClientSide()) {
			return;
		}

		if (stack.getItem() instanceof EnderItem item) {
			ServerLevel serverLevel = (ServerLevel) level;

			if (level.getBlockState(positionClicked) == CmiBlock.ACCELERATOR_BLOCK.getDefaultState()) {
				CompoundTag tag = stack.getTag();
				if (tag != null) {
					float x = tag.getInt("x");
					float y = tag.getInt("y");
					float z = tag.getInt("z");
					String dim = tag.getString("dim");
					if (serverLevel.dimension().location().toString() == dim) {

						player.teleportTo(x, y, z);
						serverLevel.playSound(
								null,
								x,
								y,
								z,
								SoundEvents.PORTAL_TRAVEL,
								SoundSource.PLAYERS,
								1.0F,
								1.0F
						);
						serverLevel.sendParticles(
								ParticleTypes.DRAGON_BREATH,
								x,
								y,
								z,
								50,
								0.5,
								0.5,
								0.5,
								0.1
						);

						tag.remove("x");
						tag.remove("y");
						tag.remove("z");
						tag.remove("dim");
					} else {
						player.sendSystemMessage(Component.translatable("promp.cmi.ender_mechanism.different_dimension"));
					}
				}
			}

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
}
