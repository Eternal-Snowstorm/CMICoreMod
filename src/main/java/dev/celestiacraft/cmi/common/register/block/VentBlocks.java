package dev.celestiacraft.cmi.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.assets.ItemModelGen;
import dev.celestiacraft.cmi.common.block.mars_geothermal_vent.MarsGeothermalVentBlock;
import dev.celestiacraft.cmi.common.block.mercury_geothermal_vent.MercuryGeothermalVentBlock;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;

public class VentBlocks {
	public static final BlockEntry<MarsGeothermalVentBlock> MARS_GEO;
	public static final BlockEntry<MercuryGeothermalVentBlock> MERCURY_GEO;

	static {
		MARS_GEO = Cmi.REGISTRATE.block("mars_geothermal_vent", MarsGeothermalVentBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/mars_geothermal_vent"))
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.blockstate(MarsGeothermalVentBlock.genBlockState())
				.register();

		MERCURY_GEO = Cmi.REGISTRATE.block("mercury_geothermal_vent", MercuryGeothermalVentBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/mercury_geothermal_vent"))
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(Tags.Blocks.NEEDS_WOOD_TOOL)
				.blockstate(MercuryGeothermalVentBlock.genBlockState())
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Vent Blocks Registered!", Cmi.MODID);
	}
}