package dev.celestiacraft.cmi.client.ponder;

import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.teammoeg.immersiveindustry.IIContent;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.utils.ModResources;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CmiPonderItem {
	public static class Cmi {
		public static final Item[] SEA_WATER = new Item[]{
				CmiBlock.WATER_PUMP.asItem(),
				ModResources.SEA_WATER.getBucket()
		};

		public static final Item[] CRUCIBLE = new Item[]{
				IIContent.IIMultiblocks.CRUCIBLE.blockItem().get(),
				IIContent.IIBlocks.burning_chamber.get().asItem(),
				IEBlocks.MetalDevices.BLAST_FURNACE_PREHEATER.asItem(),
				IEBlocks.StoneDecoration.BLASTBRICK.asItem(),
				ModResources.CRUCIBLE_BASE.getItem(),
				ModResources.CRUCIBLE_TUYERE.getItem()
		};

		public static final Item[] ACCELERATOR = new Item[]{
				CmiBlock.ACCELERATOR.asItem(),
		};
	}

	public static class Mekanism {
		public static final Item[] SPS = new Item[]{
				MekanismBlocks.SUPERCHARGED_COIL.asItem(),
				MekanismBlocks.SPS_CASING.asItem(),
				MekanismBlocks.SPS_PORT.asItem()
		};
	}

	public static class TConstruct {
		public static final Item[] MELTER = new Item[]{
				TinkerSmeltery.searedMelter.get().asItem()
		};

		public static final Item[] ALLOYER = new Item[]{
				TinkerSmeltery.scorchedAlloyer.get().asItem(),
		};

		public static final Item[] HEATER = new Item[]{
				TinkerSmeltery.searedHeater.get().asItem(),
		};

		public static final Item[] CASTING = new Item[]{
				TinkerSmeltery.searedTable.get().asItem(),
				TinkerSmeltery.searedBasin.get().asItem(),
				TinkerSmeltery.scorchedTable.get().asItem(),
				TinkerSmeltery.scorchedBasin.get().asItem(),
				TinkerSmeltery.searedFaucet.get().asItem(),
				TinkerSmeltery.scorchedFaucet.get().asItem(),
				TinkerSmeltery.searedChannel.get().asItem(),
				TinkerSmeltery.scorchedChannel.get().asItem(),
		};

		public static final Item[] SAND = new Item[]{
				TinkerSmeltery.blankSandCast.get().asItem(),
				TinkerSmeltery.blankRedSandCast.get().asItem()
		};

		public static final Item[] SMELTERY = new Item[]{
				TinkerSmeltery.smelteryController.get().asItem(),
				TinkerSmeltery.searedDrain.get().asItem(),
				TinkerSmeltery.searedDuct.get().asItem(),
				TinkerSmeltery.searedChute.get().asItem()
		};

		public static final Item[] FOUNDRY = new Item[]{
				TinkerSmeltery.foundryController.get().asItem(),
				TinkerSmeltery.scorchedDrain.get().asItem(),
				TinkerSmeltery.scorchedDuct.get().asItem(),
				TinkerSmeltery.scorchedChute.get().asItem()
		};

		public static final Item[] TANK = new Item[]{
				TinkerSmeltery.searedCastingTank.get().asItem(),
				TinkerSmeltery.scorchedProxyTank.get().asItem()
		};

		public static final Item[] CANNONS = new Item[]{
				TinkerSmeltery.searedFluidCannon.get().asItem(),
				TinkerSmeltery.scorchedFluidCannon.get().asItem()
		};
	}
}