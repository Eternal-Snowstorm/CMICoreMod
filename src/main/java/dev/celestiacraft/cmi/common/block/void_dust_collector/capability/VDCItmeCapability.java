package dev.celestiacraft.cmi.common.block.void_dust_collector.capability;

import dev.celestiacraft.cmi.common.block.void_dust_collector.VoidDustCollectorBlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class VDCItmeCapability extends ItemStackHandler {
	private final VoidDustCollectorBlockEntity enitiy;

	public VDCItmeCapability(VoidDustCollectorBlockEntity enitiy) {
		super(1);
		this.enitiy = enitiy;
	}

	@Override
	protected void onContentsChanged(int slot) {
		enitiy.setChanged();
	}
}