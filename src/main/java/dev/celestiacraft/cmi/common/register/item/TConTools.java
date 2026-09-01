package dev.celestiacraft.cmi.common.register.item;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

public class TConTools {
	public static final ItemEntry<ModifiableItem> PAXEL;

	static {
		PAXEL = addTConTool("paxel")
				.tag(ItemTags.PICKAXES)
				.tag(ItemTags.AXES)
				.tag(ItemTags.SHOVELS)
				.tag(TinkerTags.Items.MODIFIABLE)
				.tag(TinkerTags.Items.MULTIPART_TOOL)
				.tag(TinkerTags.Items.DURABILITY)
				.tag(TinkerTags.Items.SMALL_TOOLS)
				.tag(TinkerTags.Items.MELEE)
				.tag(TinkerTags.Items.HELD)
				.tag(TinkerTags.Items.HARVEST)
				.tag(TinkerTags.Items.HARVEST_PRIMARY)
				.tag(TinkerTags.Items.STONE_HARVEST)
				.tag(TinkerTags.Items.INTERACTABLE)
				.tag(TinkerTags.Items.INTERACTABLE_RIGHT)
				.tag(TinkerTags.Items.NUGGETS_NETHERITE)
				.tag(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP)
				.tag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
				.tag(TinkerTags.Items.BONUS_SLOTS)
				.register();
	}

	private static ToolDefinition addTConToolType(String name) {
		return ToolDefinition.create(Cmi.loadResource(name));
	}

	private static ItemBuilder<ModifiableItem, CreateRegistrate> addTConTool(String name) {
		ItemBuilder<ModifiableItem, CreateRegistrate> builder = Cmi.REGISTRATE.item(name, (properties) -> {
			return new ModifiableItem(properties, addTConToolType(name));
		});

		builder.model(NonNullBiConsumer.noop());
		builder.tag(Tags.Items.TOOLS);
		builder.tag(ItemTags.TOOLS);

		return builder;
	}

	public static void register() {
		Cmi.LOGGER.info("{} TCon Tools Registered!", Cmi.NAME);
	}
}