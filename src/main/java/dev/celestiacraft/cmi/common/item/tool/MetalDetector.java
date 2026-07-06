package dev.celestiacraft.cmi.common.item.tool;

import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.item.BasicItem;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
		context.addTranslatable(CmiLang.translateDirect("metal_detector").toString());
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		ItemStack itemInHand = context.getItemInHand();
		Player player = context.getPlayer();
		Level level = context.getLevel();

		if (!level.isClientSide()) {
			BlockPos positionClicked = context.getClickedPos();
			boolean foundBlock = true;

			for (int i = 0; i <= positionClicked.getY() + 64; i++) {
				BlockState state = level.getBlockState(positionClicked.below(i));
				Block block = state.getBlock();
				BlockPos foundPos = positionClicked.below(i);

				if (isValuableBlock(state)) {
					foundBlock = false;
					outputValuableCoordinates(foundPos, player, block);
					spawnFoundParticles(level, player);
					break;
				}
			}

			if (foundBlock) {
				player.sendSystemMessage(Component.translatable("info.cmi.metal_detector.not"));
			}
		}

		brokenItem(player, context.getHand());

		return InteractionResult.SUCCESS;
	}

	/**
	 * 损坏物品
	 *
	 * @param player
	 * @param hand
	 */
	private void brokenItem(Player player, InteractionHand hand) {
		ItemStack item = player.getMainHandItem();

		if (player.isCreative()) {
			return;
		}

		if (item.getMaxDamage() > item.getDamageValue()) {
			EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
					? EquipmentSlot.MAINHAND
					: EquipmentSlot.OFFHAND;
			item.hurtAndBreak(1, player, (entity) -> {
				entity.broadcastBreakEvent(entity.getUsedItemHand());
			});
		}
	}

	/**
	 * 打印信息
	 *
	 * @param pos
	 * @param player
	 * @param block
	 */
	private void outputValuableCoordinates(BlockPos pos, Player player, Block block) {
		player.sendSystemMessage(Component.translatable(
				"info.cmi.metal_detector.have",
				pos.getX(),
				pos.getY(),
				pos.getZ(),
				I18n.get(block.getDescriptionId())
		));
	}

	/**
	 * 生成粒子效果
	 *
	 * @param level
	 * @param player
	 */
	private void spawnFoundParticles(Level level, Player player) {
		BlockPos playerPos = player.blockPosition();
		double nextDouble = level.getRandom().nextDouble();

		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		// 玩家腰部高度约为1.0格
		for (int j = 0; j < 12; j++) {
			double offsetX = nextDouble * 0.5D - 0.25D;
			double offsetZ = nextDouble * 0.5D - 0.25D;
			double offsetY = nextDouble * 0.2D - 0.1D;

			serverLevel.sendParticles(
					ParticleTypes.GLOW,
					playerPos.getX() + 0.5D + offsetX,
					playerPos.getY() + 1.0D + offsetY,
					playerPos.getZ() + 0.5D + offsetZ,
					1,
					0.0D, 0.0D, 0.0D,
					0.0D
			);
		}
	}

	/**
	 * 检测方块
	 *
	 * @param state
	 * @return
	 */
	private boolean isValuableBlock(BlockState state) {
		return state.is(Tags.Blocks.ORES);
	}
}