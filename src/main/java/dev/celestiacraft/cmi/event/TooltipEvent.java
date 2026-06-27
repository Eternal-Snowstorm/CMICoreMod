package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.libs.compat.ICheckModLoaded;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.MekanismLang;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.item.block.ItemBlockCardboardBox;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.util.text.BooleanStateDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipEvent {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		List<Component> tooltip = event.getToolTip();

		if (ICheckModLoaded.hasMekanism()) {
			Item item = MekanismBlocks.CARDBOARD_BOX.asItem();
			ItemStack stack = event.getItemStack();

			if (stack.getItem().equals(item)) {
				Player player = event.getEntity();
				if (player != null) {
					if (item instanceof ItemBlockCardboardBox cardboardBox) {
						Level level = player.level();
						BlockCardboardBox.BlockData data = cardboardBox.getBlockData(level, stack);
						tooltip.remove(MekanismLang.BLOCK_DATA.translateColored(EnumColor.INDIGO, BooleanStateDisplay.YesNo.of(data != null)));
						if (stack.hasTag()) {
							if (data != null) {
								tooltip.add(MekanismLang.BLOCK_DATA.translateColored(EnumColor.INDIGO, BooleanStateDisplay.YesNo.of(true, true)));

								Block block = data.blockState.getBlock();
								tooltip.remove(MekanismLang.BLOCK.translate(block));
								tooltip.add(TextComponentUtil.build(
										EnumColor.INDIGO,
										MekanismLang.BLOCK.translate(Component.translatable(block.getDescriptionId())
												.withStyle(ChatFormatting.GRAY))));

								if (block instanceof SpawnerBlock) {
									CompoundTag tileTag = data.tileTag;
									if (tileTag != null) {
										tooltip.remove(MekanismLang.BLOCK_ENTITY.translate(tileTag.getString("id")));
										tooltip.add(MekanismLang.BLOCK_ENTITY.translateColored(
												EnumColor.INDIGO,
												Component.translatable(tileTag.getString("id")).withStyle(ChatFormatting.GRAY))
										);

										Tag tag = data.tileTag.getCompound("SpawnData").getCompound("entity").get("id");
										if (tag != null) {
											EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.tryParse(tag.toString().replace("\"", "")));
											if (type != null) {
												ResourceLocation entityLocation = ForgeRegistries.ENTITY_TYPES.getKey(type);
												if (entityLocation != null) {
													tooltip.add(
															TextComponentUtil.build(EnumColor.INDIGO, Component.translatable(
																			"cardboard_box.mekanism.block_entity.spawn_type",
																			Component.translatable(
																					capitaliseAllWords(entityLocation.toShortLanguageKey().replace("_", " "))
																			).withStyle(ChatFormatting.GRAY)
																	)
															)
													);
												}
											}
										}
									}
								}
							}
						} else {
							tooltip.add(MekanismLang.BLOCK_DATA.translateColored(EnumColor.INDIGO, TextComponentUtil.build(EnumColor.RED, MekanismLang.NO)));
						}
					}
				}
			}
		}
	}

	private static String capitaliseAllWords(String string) {
		if (string == null) {
			return null;
		}
		int sz = string.length();
		StringBuilder buffer = new StringBuilder(sz);
		boolean space = true;
		for (int i = 0; i < sz; i++) {
			char ch = string.charAt(i);
			if (Character.isWhitespace(ch)) {
				buffer.append(ch);
				space = true;
			} else if (space) {
				buffer.append(Character.toTitleCase(ch));
				space = false;
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
}