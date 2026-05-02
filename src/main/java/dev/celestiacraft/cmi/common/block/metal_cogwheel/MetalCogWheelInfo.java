package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import com.tterrag.registrate.util.entry.BlockEntry;
import lombok.Getter;

@Getter
public class MetalCogWheelInfo {
	private final String material;
	private final BlockEntry<MetalCogWheelBlock> small;
	private final BlockEntry<MetalCogWheelBlock> large;

	public MetalCogWheelInfo(String material, BlockEntry<MetalCogWheelBlock> small, BlockEntry<MetalCogWheelBlock> large) {
		this.material = material;
		this.small = small;
		this.large = large;
	}
}