package dev.celestiacraft.cmi.common.modifier.diving;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.create.CreateOxygenSupport;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

/**
 * 潜水（Diving）匠魂头盔强化。
 * <p>
 * 当玩家头盔上带有该强化时：
 * <ul>
 *     <li>在水中：参照 Create 的 {@link com.simibubi.create.content.equipment.armor.DivingHelmetItem}，
 *     在无法自然呼吸时消耗玩家身上的 Backtank 空气维持呼吸；</li>
 *     <li>在 minecraft:the_nether：由 {@link dev.celestiacraft.cmi.event.NetherBreathingHandler}
 *     识别本强化并消耗 Backtank 空气（下界被视为无氧环境）。</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DivingModifier extends NoLevelsModifier {
	/** 注册名，与 {@link dev.celestiacraft.cmi.common.register.CmiModifier} 保持一致 */
	public static final String ID = "diving";
	private static final ModifierId MODIFIER_ID = new ModifierId(Cmi.MODID, ID);

	/* ---------- 通用检测（服务端 / 客户端共用） ---------- */

	/** 玩家头盔（头部装备槽）是否带有 Diving 强化 */
	public static boolean isWearing(LivingEntity entity) {
		ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
		return ModifierUtil.getModifierLevel(helmet, MODIFIER_ID) > 0;
	}

	/** 玩家身上是否还有带空气的 Backtank */
	public static boolean hasBacktankAir(LivingEntity entity) {
		return !BacktankUtil.getAllWithAir(entity).isEmpty();
	}

	/** 玩家是否满足“Diving 头盔 + 有气 Backtank”的供氧条件 */
	public static boolean isActive(LivingEntity entity) {
		return isWearing(entity) && hasBacktankAir(entity);
	}

	/* ---------- 呼吸处理 ---------- */

	@SubscribeEvent
	public static void onLivingBreathe(LivingBreatheEvent event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}

		Level level = player.level();
		if (level.dimension() == Level.NETHER) {
			// 下界由 NetherBreathingHandler 统一处理（同 Create 潜水头盔/Ad Astra 逻辑）
			return;
		}

		// 只有真正“无法自然呼吸”时才消耗背罐空气，避免与水下呼吸药水等冲突
		if (!player.isEyeInFluid(FluidTags.WATER) || event.canBreathe()) {
			return;
		}
		if (player.isCreative() || player.isSpectator()) {
			return;
		}
		if (!isActive(player)) {
			return;
		}

		event.setCanBreathe(true);
		event.setCanRefillAir(true);
		event.setConsumeAirAmount(0);

		if (!level.isClientSide() && level.getGameTime() % 20 == 0) {
			CreateOxygenSupport.consumeBacktankAir(player, 1);
		}
	}
}
