package dev.celestiacraft.cmi.api.register.block;

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelSet;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelBlock;
import dev.celestiacraft.cmi.common.block.metal_cogwheel.MetalCogWheelBlockItem;
import dev.celestiacraft.cmi.compat.create.CmiStress;
import dev.celestiacraft.cmi.tags.ModBlockTags;
import dev.celestiacraft.cmi.tags.ModItemTags;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;

public class CogWheelRegister {
	public static final List<BlockEntry<MetalCogWheelBlock>> COMMON_LIST = new ArrayList<>();

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

	public static MetalCogWheelSet register(String material) {
		BlockEntry<MetalCogWheelBlock> small = registerSmall(material, MetalCogWheelBlock::small).register();
		BlockEntry<MetalCogWheelBlock> large = registerLarge(material, MetalCogWheelBlock::large).register();

		COMMON_LIST.add(small);
		COMMON_LIST.add(large);

		return new MetalCogWheelSet(small, large);
	}
}