package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.item.MechanismItem;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import dev.celestiacraft.libs.compat.curios.ICuriosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GoldItem extends MechanismItem {
	public GoldItem(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useAfterConsume() {
		return false;
	}


	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		if (!(event.getSource().getEntity() instanceof Player player)) {
			return;
		}

		if (!ICuriosHelper.hasItem(player, CmiMechanism.GOLD.get())) {
			return;
		}

		DamageSource source = event.getSource();

		// 判断是否是箭
		if (source.getDirectEntity() instanceof AbstractArrow) {
			LivingEntity target = event.getEntity();

			// 清除无敌帧
			target.invulnerableTime = 0;
		}
	}

	@Override
	protected InteractionResult onMechanismUseOn(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);

		if (level.isClientSide()) {
			return InteractionResult.PASS;
		}

		if (state.is(Tags.Blocks.STONE) || state.is(Tags.Blocks.COBBLESTONE)) {
			player.swing(hand);
			if (level.random.nextFloat() < 0.01F) {
				level.setBlockAndUpdate(pos, Blocks.GOLD_BLOCK.defaultBlockState());
			}
		}
		return InteractionResult.SUCCESS;
	}
}