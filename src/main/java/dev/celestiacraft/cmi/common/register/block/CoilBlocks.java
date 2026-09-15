package dev.celestiacraft.cmi.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.ItemModelGen;
import dev.celestiacraft.cmi.common.block.coil.CoilBlock;
import dev.celestiacraft.cmi.tags.CmiBlockTags;
import dev.celestiacraft.cmi.tags.CmiItemTags;

public class CoilBlocks {
	public static final BlockEntry<CoilBlock>
			COPPER,
			ELECTRUM,
			STEEL;

	static {
		COPPER = addCoil("copper");
		ELECTRUM = addCoil("electrum");
		STEEL = addCoil("steel");
	}

	private static BlockEntry<CoilBlock> addCoil(String material) {
		String id = String.format("%s_coil", material);
		return Cmi.REGISTRATE.block(id, CoilBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/coil/%s/off".formatted(material)))
				.tag(CmiItemTags.COILS)
				.build()
				.blockstate(CoilBlock.genBlockState(material))
				.tag(CmiBlockTags.COILS)
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Coil Blocks Registered!", Cmi.NAME);
	}
}