package dev.celestiacraft.cmi.common.item;

import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InitialItemKitItem extends Item {
	public InitialItemKitItem(Properties properties) {
		super(properties);
	}

	private static final List<String> ITEM_LIST = List.of(
			"create:wrench",
			"create:goggles",
			"create:super_glue",
			"create:stressometer",

			"tiab:time_in_a_bottle"
	);

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		Player player = context.getPlayer();
		Level level = context.getLevel();

		if (context.getHand() != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}
		if (player == null || !player.isCrouching()) {
			return InteractionResult.PASS;
		}

		ITEM_LIST.forEach((id) -> {
			ResourceLocation resources = ResourceLocation.parse(id);
			Item item = ForgeRegistries.ITEMS.getValue(resources);

			if (item != null) {
				player.addItem(item.getDefaultInstance());
				level.playSound(
						null,
						player.getX(),
						player.getY(),
						player.getZ(),
						SoundEvents.ITEM_PICKUP,
						SoundSource.PLAYERS,
						1,
						1
				);
			}
			context.getItemInHand().shrink(1);
		});

		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			// 使用方法
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					Component.translatable("cmi.tooltip.initial_item_kit.usage").getString(),
					FontHelper.Palette.STANDARD_CREATE
			));
			// 需要的格子数
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					Component.translatable(
							"cmi.tooltip.initial_item_kit",
							ITEM_LIST.size()
					).withStyle(ChatFormatting.AQUA).getString(),
					FontHelper.Palette.STANDARD_CREATE
			));

			// 标题
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					Component.translatable("cmi.tooltip.initial_item_kit.list")
							.withStyle(ChatFormatting.GRAY).getString(),
					FontHelper.Palette.STANDARD_CREATE
			));

			// 列表
			ITEM_LIST.forEach((id) -> {
				ResourceLocation resources = ResourceLocation.parse(id);
				Item item = ForgeRegistries.ITEMS.getValue(resources);

				if (item != null) {
					Component itemName = item.getDescription();

					Component line = Component.translatable(
							"cmi.tooltip.initial_item_kit.entry",
							id,
							0.5,
							itemName
					).withStyle(ChatFormatting.DARK_GRAY);

					tooltip.add(line);
				}
			});
		} else {
			tooltip.addAll(TooltipHelper.cutStringTextComponent(
					Component.translatable("cmi.tooltip.initial_item_kit.hold_shift")
							.withStyle(ChatFormatting.DARK_GRAY).getString(),
					FontHelper.Palette.STANDARD_CREATE
			));
		}
	}

	@Override
	public boolean isFoil(@NotNull ItemStack stack) {
		return true;
	}
}