package dev.celestiacraft.cmi.common.block.general_astronautic_platform.render;

import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockEntity;
import net.minecraft.core.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GeneralAstronauticPlatformRenderer extends GeoBlockRenderer<GeneralAstronauticPlatformBlockEntity> {
	public GeneralAstronauticPlatformRenderer() {
		super(new GeneralAstronauticPlatformModel());
	}

	@Override
	protected Direction getFacing(GeneralAstronauticPlatformBlockEntity block) {
		return Direction.SOUTH;
	}
}