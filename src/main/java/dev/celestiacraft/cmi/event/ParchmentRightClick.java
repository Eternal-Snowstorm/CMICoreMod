package dev.celestiacraft.cmi.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import dev.celestiacraft.cmi.Cmi;
import top.nebula.libs.NebulaLibs;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ParchmentRightClick {
	record PlayerPos(double x, double y, double z) {
	}

	public static final Lazy<Item> PARCHMEMT = Lazy.of(() -> {
		return BuiltInRegistries.ITEM.get(Cmi.loadResource("parchment"));
	});

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack item = player.getItemInHand(event.getHand());
		BlockPos pos = event.getPos();

		if (level.isClientSide()) {
			return;
		}

		ServerLevel sl = (ServerLevel) level;

		// 只能主手触发
		if (item.is(PARCHMEMT.get()) && event.getHand() == InteractionHand.MAIN_HAND) {
			// 定位玩家眼睛坐标(Pos)
			Vec3 eyePos = player.getEyePosition();

			// 获取坐标具体数据
			PlayerPos playerPos = new PlayerPos(
					eyePos.x(),
					eyePos.y(),
					eyePos.z()
			);

			// 给予玩家幸运buff(10分钟, 2级)
			player.addEffect(new MobEffectInstance(
					MobEffects.LUCK,
					20 * 600,
					1,
					false,
					true,
					true
			));

			// 播放音效
			level.playSound(
					null,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.EXPERIENCE_ORB_PICKUP,
					SoundSource.PLAYERS,
					1.0f,
					1.0f
			);

			// 释放粒子效果
			for (int i = 0; i < 30; i++) {
				double offsetX = (Math.random() - 0.5) * 3;
				double offsetY = (Math.random() - 0.5) * 2;
				double offsetZ = (Math.random() - 0.5) * 3;

				// 粒子效果
				sl.sendParticles(
						ParticleTypes.HEART,
						playerPos.x() + offsetX,
						playerPos.y() + offsetY,
						playerPos.z() + offsetZ,
						1,
						0,
						0,
						0,
						0
				);
				// 调用动画
				NebulaLibs.useTotemAnimation(PARCHMEMT.get().getDefaultInstance());
				// 挥手
				player.swing(event.getHand());
				// 消耗物品
				if (!player.isCreative()) {
					item.shrink(1);
				}
			}
		}
	}
}