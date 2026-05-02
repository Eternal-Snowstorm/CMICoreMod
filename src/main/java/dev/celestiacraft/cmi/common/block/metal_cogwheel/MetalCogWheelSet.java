package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import com.tterrag.registrate.util.entry.BlockEntry;
import lombok.Getter;

@Getter
public class MetalCogWheelSet {
	private final BlockEntry<MetalCogWheelBlock> small;
	private final BlockEntry<MetalCogWheelBlock> large;

	public MetalCogWheelSet(BlockEntry<MetalCogWheelBlock> small, BlockEntry<MetalCogWheelBlock> large) {
		this.small = small;
		this.large = large;
	}
}