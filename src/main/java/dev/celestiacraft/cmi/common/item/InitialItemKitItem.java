package dev.celestiacraft.cmi.common.item;

import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
			"create:item_hatch",
			"create:packager",
			"create:stock_link",
			"create:stock_ticker",
			"create:red_seat",
			"63x create:item_vault",

			"tiab:time_in_a_bottle"
	);

	private static final List<Parsed> PARSED_LIST = ITEM_LIST.stream()
			.map(InitialItemKitItem::parseStatic)
			.toList();

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

		PARSED_LIST.forEach((parsed) -> {
			ResourceLocation rl = ResourceLocation.parse(parsed.id);
			Item item = ForgeRegistries.ITEMS.getValue(rl);

			if (item != null) {
				player.addItem(new ItemStack(item, parsed.count));

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
		});
		context.getItemInHand().shrink(1);

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
			PARSED_LIST.forEach((parsed) -> {
				ResourceLocation location = ResourceLocation.parse(parsed.id);
				Item item = ForgeRegistries.ITEMS.getValue(location);

				if (item != null) {
					Component itemName = item.getDescription();

					MutableComponent line = Component.translatable(
							"cmi.tooltip.initial_item_kit.entry",
							parsed.id,
							0.5,
							itemName
					);

					if (parsed.count > 1) {
						line.append(Component.literal(" x" + parsed.count));
					}

					tooltip.add(line.withStyle(ChatFormatting.AQUA));
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

	private static Parsed parseStatic(String entry) {
		entry = entry.trim();

		if (entry.contains("x ")) {
			String[] split = entry.split("x ", 2);
			try {
				int count = Integer.parseInt(split[0]);
				return new Parsed(split[1], count);
			} catch (NumberFormatException ignored) {
			}
		}

		return new Parsed(entry, 1);
	}

	@Override
	public boolean isFoil(@NotNull ItemStack stack) {
		return true;
	}

	private static class Parsed {
		String id;
		int count;

		Parsed(String id, int count) {
			this.id = id;
			this.count = count;
		}
	}
}