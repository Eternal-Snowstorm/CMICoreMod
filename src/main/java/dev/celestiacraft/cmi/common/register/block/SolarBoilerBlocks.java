package dev.celestiacraft.cmi.common.register.block;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.assets.ItemModelGen;
import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerBlock;
import dev.celestiacraft.cmi.common.block.solar_boiler.SolarBoilerItem;
import dev.celestiacraft.cmi.common.block.solar_boiler.bronze.BronzeSolarBoilerBlock;
import dev.celestiacraft.cmi.common.block.solar_boiler.cast_iron.CastIronSolarBoilerBlock;
import dev.celestiacraft.cmi.common.block.solar_boiler.steel.SteelSolarBoilerBlock;
import dev.celestiacraft.cmi.tags.CmiItemTags;
import net.minecraft.tags.BlockTags;

public class SolarBoilerBlocks {
	public static final BlockEntry<BronzeSolarBoilerBlock> BRONZE_SOLAR_BOILER;
	public static final BlockEntry<CastIronSolarBoilerBlock> CAST_IRON_SOLAR_BOILER;
	public static final BlockEntry<SteelSolarBoilerBlock> STEEL_SOLAR_BOILER;

	static {
		BRONZE_SOLAR_BOILER = Cmi.REGISTRATE.block("bronze_solar_boiler", BronzeSolarBoilerBlock::new)
				.initialProperties(SharedProperties::softMetal)
				.transform(TagGen.pickaxeOnly())
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.item(SolarBoilerItem::new)
				.tag(CmiItemTags.BURNER)
				.model(ItemModelGen.withModel("block/solar_boiler/bronze"))
				.build()
				.blockstate(SolarBoilerBlock.genBlockState("bronze"))
				.register();

		CAST_IRON_SOLAR_BOILER = Cmi.REGISTRATE.block("cast_iron_solar_boiler", CastIronSolarBoilerBlock::new)
				.initialProperties(SharedProperties::softMetal)
				.transform(TagGen.pickaxeOnly())
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.item(SolarBoilerItem::new)
				.tag(CmiItemTags.BURNER)
				.model(ItemModelGen.withModel("block/solar_boiler/cast_iron"))
				.build()
				.blockstate(SolarBoilerBlock.genBlockState("cast_iron"))
				.register();

		STEEL_SOLAR_BOILER = Cmi.REGISTRATE.block("steel_solar_boiler", SteelSolarBoilerBlock::new)
				.initialProperties(SharedProperties::softMetal)
				.transform(TagGen.pickaxeOnly())
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_STONE_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.item(SolarBoilerItem::new)
				.tag(CmiItemTags.BURNER)
				.model(ItemModelGen.withModel("block/solar_boiler/steel"))
				.build()
				.blockstate(SolarBoilerBlock.genBlockState("steel"))
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Solar Boiler Blocks Registered!", Cmi.MODID);
	}
}