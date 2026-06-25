package dev.celestiacraft.cmi.common.register.block;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.teammoeg.steampowered.registrate.SPBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.assets.ItemModelGen;
import dev.celestiacraft.cmi.common.block.fluid_burner.FluidBurnerBlock;
import dev.celestiacraft.cmi.common.block.fluid_burner.FluidBurnerBlockItem;
import dev.celestiacraft.cmi.common.block.fluid_burner.bronze.BronzeFluidBurnerBlock;
import dev.celestiacraft.cmi.common.block.fluid_burner.cast_iron.CastIronFluidBurnerBlock;
import dev.celestiacraft.cmi.common.block.fluid_burner.steel.SteelFluidBurnerBlock;
import dev.celestiacraft.cmi.tags.CmiItemTags;
import net.minecraft.tags.BlockTags;

public class FluidBurnerBlocks {
	public static final BlockEntry<BronzeFluidBurnerBlock> BRONZE_FLUID_BURNER;
	public static final BlockEntry<CastIronFluidBurnerBlock> CAST_IRON_FLUID_BURNER;
	public static final BlockEntry<SteelFluidBurnerBlock> STEEL_FLUID_BURNER;

	static {
		BRONZE_FLUID_BURNER = Cmi.REGISTRATE.block("bronze_fluid_burner", BronzeFluidBurnerBlock::new)
				.initialProperties(SharedProperties::softMetal)
				.transform(TagGen.pickaxeOnly())
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.item(FluidBurnerBlockItem::new)
				.tag(CmiItemTags.BURNER)
				.model(ItemModelGen.withModel("block/fluid_burner/bronze/off"))
				.build()
				.blockstate(FluidBurnerBlock.genBlockState("bronze"))
				.register();

		CAST_IRON_FLUID_BURNER = Cmi.REGISTRATE.block("cast_iron_fluid_burner", CastIronFluidBurnerBlock::new)
				.initialProperties(SPBlocks::hardMetal)
				.transform(TagGen.pickaxeOnly())
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.item(FluidBurnerBlockItem::new)
				.tag(CmiItemTags.BURNER)
				.model(ItemModelGen.withModel("block/fluid_burner/cast_iron/off"))
				.build()
				.blockstate(FluidBurnerBlock.genBlockState("cast_iron"))
				.register();

		STEEL_FLUID_BURNER = Cmi.REGISTRATE.block("steel_fluid_burner", SteelFluidBurnerBlock::new)
				.initialProperties(SPBlocks::hardMetal)
				.transform(TagGen.pickaxeOnly())
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.item(FluidBurnerBlockItem::new)
				.tag(CmiItemTags.BURNER)
				.model(ItemModelGen.withModel("block/fluid_burner/steel/off"))
				.build()
				.blockstate(FluidBurnerBlock.genBlockState("steel"))
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Fluid Burners Blocks Registered!", Cmi.MODID);
	}
}