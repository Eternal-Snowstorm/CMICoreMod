package dev.celestiacraft.cmi.common.block.metal_cogwheel;

import com.tterrag.registrate.util.entry.BlockEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetalCogWheelInfo {
	private final String material;
	private final BlockEntry<MetalCogWheelBlock> small;
	private final BlockEntry<MetalCogWheelBlock> large;
}