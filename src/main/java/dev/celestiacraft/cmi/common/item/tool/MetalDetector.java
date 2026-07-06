package dev.celestiacraft.cmi.common.item.tool;

import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.register.CmiSound;
import dev.celestiacraft.cmi.config.common.MetalDetectorConfig;
import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.item.BasicItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class MetalDetector extends BasicItem {
	public MetalDetector(Properties properties) {
		super(properties.durability(2048)
				.rarity(Rarity.EPIC));
	}

	@Override
	public void addTooltips(TooltipContext context) {
		context.add(CmiLang.translateDirect("tooltip.metal_detector"));
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level level = context.getLevel();

		if (!level.isClientSide()) {
			BlockPos clickedPos = context.getClickedPos();
			boolean found = false;

			for (int y = clickedPos.getY(); y >= level.getMinBuildHeight(); y--) {
				BlockPos pos = new BlockPos(clickedPos.getX(), y, clickedPos.getZ());
				BlockState state = level.getBlockState(pos);

				if (isValuableBlock(state)) {
					found = true;
					outputValuableCoordinates(pos, player, state);
					spawnFoundParticles(context);

					if (MetalDetectorConfig.PLAY_SOUND.get()) {
						level.playSound(
								null,
								player.blockPosition(),
								CmiSound.DING.get(),
								SoundSource.PLAYERS,
								0.5F,
								1.0F
						);
					}
					break;
				}
			}

			if (!found) {
				player.sendSystemMessage(Component.translatable("info.cmi.metal_detector.not"));
			}
		}

		brokenItem(player, context.getHand());

		return InteractionResult.SUCCESS;
	}

	/**
	 * 损坏物品
	 *
	 * @param player 玩家
	 * @param hand   手
	 */
	private void brokenItem(Player player, InteractionHand hand) {
		if (player.isCreative()) {
			return;
		}

		ItemStack item = player.getItemInHand(hand);

		if (item.getDamageValue() >= item.getMaxDamage()) {
			return;
		}

		EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
				? EquipmentSlot.MAINHAND
				: EquipmentSlot.OFFHAND;

		item.hurtAndBreak(1, player, (entity) -> {
			entity.broadcastBreakEvent(slot);
		});
	}

	/**
	 * 打印信息
	 *
	 * @param pos    方块坐标
	 * @param player 玩家
	 * @param state  方块状态
	 */
	private void outputValuableCoordinates(BlockPos pos, Player player, BlockState state) {
		player.sendSystemMessage(Component.translatable(
				"info.cmi.metal_detector.have",
				pos.getX(),
				pos.getY(),
				pos.getZ(),
				state.getBlock().getName()
		));
	}

	/**
	 * 生成粒子效果
	 *
	 * @param context
	 */
	private void spawnFoundParticles(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos clickedPos = context.getClickedPos();

		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		BlockPos playerPos = player.blockPosition();

		for (int i = 0; i < 12; i++) {
			double offsetX = level.getRandom().nextDouble() * 0.5D - 0.25D;
			double offsetY = level.getRandom().nextDouble() * 0.2D - 0.1D;
			double offsetZ = level.getRandom().nextDouble() * 0.5D - 0.25D;

			serverLevel.sendParticles(
					ParticleTypes.GLOW,
					clickedPos.getX() + 0.5D + offsetX,
					clickedPos.getY() + 1.0D + offsetY,
					clickedPos.getZ() + 0.5D + offsetZ,
					1,
					0.0D,
					0.0D,
					0.0D,
					0.0D
			);
		}
	}

	/**
	 * 检测方块
	 *
	 * @param state 方块状态
	 * @return 是否为矿石
	 */
	private boolean isValuableBlock(BlockState state) {
		return state.is(Tags.Blocks.ORES);
	}
}