package dev.celestiacraft.cmi.compat.jade;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.advanced_spout.AdvancedSpoutBlock;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.compat.jade.provider.*;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.tile.TileEntityCardboardBox;
import mekanism.common.tile.base.TileEntityMekanism;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CmiJadePlugin implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(UpgradeProvider.INSTANCE, TileEntityMekanism.class);
		registration.registerBlockDataProvider(CardboardBoxProvider.INSTANCE, TileEntityCardboardBox.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(CmiComponentProvider.INSTANCE, AdvancedSpoutBlock.class);
		registration.registerEntityComponent(SpaceElevatorEntityProvider.INSTANCE, SpaceElevatorEntity.class);
		registration.registerBlockComponent(UpgradeProvider.INSTANCE, BlockTile.class);
		registration.registerBlockComponent(CardboardBoxProvider.INSTANCE, BlockCardboardBox.class);

		Cmi.LOGGER.info("Jade Plugin is registered!");
	}
}