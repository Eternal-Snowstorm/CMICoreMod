package dev.celestiacraft.cmi.api.register.block;

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelBlock;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelBlockItem;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelInfo;
import dev.celestiacraft.cmi.compat.create.CmiStress;
import dev.celestiacraft.cmi.tags.ModBlockTags;
import dev.celestiacraft.cmi.tags.ModItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetalCogWheelRegister {
	public static final List<BlockEntry<MetalCogWheelBlock>> COMMON_LIST = new ArrayList<>();
	public static final List<String> MATERIAL_LIST = new ArrayList<>();
	public static final Map<Block, MetalCogWheelInfo> BLOCK_TO_SET = new HashMap<>();

	private static <T extends MetalCogWheelBlock> BlockBuilder<T, CreateRegistrate> registerSmall(String material, NonNullFunction<BlockBehaviour.Properties, T> factory) {
		String name = String.format("%s_cogwheel", material);
		BlockBuilder<T, CreateRegistrate> builder = Cmi.REGISTRATE.block(name, factory);

		builder.item(MetalCogWheelBlockItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc(String.format("block/cogwheel/%s/%s", material, "small"))
					);
				})
				.tag(ModItemTags.COGWHEEL)
				.build();
		builder.initialProperties(SharedProperties::stone);
		builder.blockstate(MetalCogWheelBlock.blockstate(material, false));
		builder.tag(ModBlockTags.COGWHEEL);
		builder.transform(CmiStress.setNoImpact());
		builder.transform(TagGen.pickaxeOnly());
		builder.onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new));

		return builder;
	}

	private static <T extends MetalCogWheelBlock> BlockBuilder<T, CreateRegistrate> registerLarge(String material, NonNullFunction<BlockBehaviour.Properties, T> factory) {
		String name = String.format("%s_large_cogwheel", material);
		BlockBuilder<T, CreateRegistrate> builder = Cmi.REGISTRATE.block(name, factory);

		builder.item(MetalCogWheelBlockItem::new)
				.model((context, provider) -> {
					provider.withExistingParent(
							context.getName(),
							provider.modLoc(String.format("block/cogwheel/%s/%s", material, "large"))
					);
				})
				.tag(ModItemTags.LARGE_COGWHEEL)
				.build();
		builder.initialProperties(SharedProperties::stone);
		builder.blockstate(MetalCogWheelBlock.blockstate(material, true));
		builder.tag(ModBlockTags.LARGE_COGWHEEL);
		builder.transform(CmiStress.setNoImpact());
		builder.transform(TagGen.pickaxeOnly());
		builder.onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new));

		return builder;
	}

	public static MetalCogWheelInfo register(String material) {
		BlockBuilder<MetalCogWheelBlock, CreateRegistrate> smallBuilder = registerSmall(material, MetalCogWheelBlock::small);
		BlockBuilder<MetalCogWheelBlock, CreateRegistrate> largeBuilder = registerLarge(material, MetalCogWheelBlock::large);
		final MetalCogWheelInfo[] holder = new MetalCogWheelInfo[1];

		smallBuilder.onRegister((block) -> {
			if (holder[0] != null) {
				BLOCK_TO_SET.put(block, holder[0]);
			}
		});

		largeBuilder.onRegister((block) -> {
			if (holder[0] != null) {
				BLOCK_TO_SET.put(block, holder[0]);
			}
		});

		BlockEntry<MetalCogWheelBlock> small = smallBuilder.register();
		BlockEntry<MetalCogWheelBlock> large = largeBuilder.register();

		MetalCogWheelInfo set = new MetalCogWheelInfo(material, small, large);
		holder[0] = set;

		COMMON_LIST.add(small);
		COMMON_LIST.add(large);
		MATERIAL_LIST.add(material);

		return set;
	}
}