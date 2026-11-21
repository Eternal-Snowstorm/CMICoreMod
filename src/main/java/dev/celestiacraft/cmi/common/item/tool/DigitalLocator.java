package dev.celestiacraft.cmi.common.item.tool;

import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlock;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.PlatformBindingHandler;
import dev.celestiacraft.libs.api.client.context.TooltipContext;
import dev.celestiacraft.libs.api.register.item.BasicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DigitalLocator extends BasicItem {
	private static final String TAG_RECORDED = "RecordedPlatform";

	public DigitalLocator(Properties properties) {
		super(properties.stacksTo(1)
				.rarity(Rarity.UNCOMMON));
	}

	@Override
	public void addTooltips(TooltipContext context) {
		context.add(CmiLang.translateDirect("tooltip.digital_locator")
				.withStyle(ChatFormatting.GRAY));
	}

	// 潜行右键清除已录入的平台坐标
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null || !player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		return clearAndNotify(context.getItemInHand(), player);
	}

	// 平台被右键时由方块转交的处理入口
	public static InteractionResult useOnPlatform(Level level, BlockPos pos, Player player, ItemStack stack) {
		if (player.isShiftKeyDown()) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			return clearAndNotify(stack, player);
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.PASS;
		}
		BlockPos platformPos = GeneralAstronauticPlatformBlock.findPlatform(level, pos);
		if (platformPos == null) {
			return InteractionResult.PASS;
		}
		GlobalPos target = GlobalPos.of(level.dimension(), platformPos);
		GlobalPos recorded = getRecorded(stack);
		if (recorded == null) {
			setRecorded(stack, target);
			player.displayClientMessage(CmiLang.textDirect("digital_locator.recorded"), true);
			return InteractionResult.CONSUME;
		}
		if (recorded.equals(target)) {
			player.displayClientMessage(CmiLang.textDirect("digital_locator.same_platform"), true);
			return InteractionResult.CONSUME;
		}
		if (!PlatformBindingHandler.bind(serverLevel, platformPos, recorded)) {
			player.displayClientMessage(CmiLang.textDirect("digital_locator.source_missing"), true);
			return InteractionResult.CONSUME;
		}
		clearRecorded(stack);
		player.displayClientMessage(CmiLang.textDirect("digital_locator.bound"), true);
		return InteractionResult.CONSUME;
	}

	// 清除录入信息并提示玩家
	private static InteractionResult clearAndNotify(ItemStack stack, Player player) {
		if (!isRecorded(stack)) {
			return InteractionResult.PASS;
		}
		clearRecorded(stack);
		player.displayClientMessage(CmiLang.textDirect("digital_locator.cleared"), true);
		return InteractionResult.CONSUME;
	}

	// 是否已录入平台坐标
	public static boolean isRecorded(ItemStack stack) {
		return getRecorded(stack) != null;
	}

	// 读取已录入的平台坐标
	@Nullable
	public static GlobalPos getRecorded(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return null;
		}
		return PlatformBindingHandler.read(tag, TAG_RECORDED);
	}

	// 写入平台坐标
	public static void setRecorded(ItemStack stack, GlobalPos pos) {
		PlatformBindingHandler.write(stack.getOrCreateTag(), TAG_RECORDED, pos);
	}

	// 清除已录入的平台坐标
	public static void clearRecorded(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return;
		}
		tag.remove(TAG_RECORDED);
	}
}