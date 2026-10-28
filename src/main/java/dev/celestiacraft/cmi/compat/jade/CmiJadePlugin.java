package dev.celestiacraft.cmi.compat.jade;

import com.lowdragmc.mbd2.common.block.MBDMachineBlock;
import com.lowdragmc.mbd2.common.blockentity.MachineBlockEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.compat.jade.provider.CardboardBoxProvider;
import dev.celestiacraft.cmi.compat.jade.provider.mbd.CokeOvenProvider;
import dev.celestiacraft.cmi.compat.jade.provider.SpaceElevatorEntityProvider;
import dev.celestiacraft.cmi.compat.jade.provider.UpgradeProvider;
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
		registration.registerBlockDataProvider(CokeOvenProvider.INSTANCE, MachineBlockEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(SpaceElevatorEntityProvider.INSTANCE, SpaceElevatorEntity.class);
		registration.registerBlockComponent(UpgradeProvider.INSTANCE, BlockTile.class);
		registration.registerBlockComponent(CardboardBoxProvider.INSTANCE, BlockCardboardBox.class);
		registration.registerBlockComponent(CokeOvenProvider.INSTANCE, MBDMachineBlock.class);
	}
}