package dev.celestiacraft.cmi.common.block.general_astronautic_platform.render;

import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlock;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GeneralAstronauticPlatformRenderer extends GeoBlockRenderer<GeneralAstronauticPlatformBlockEntity> {
	public GeneralAstronauticPlatformRenderer() {
		super(new GeneralAstronauticPlatformModel());
		addRenderLayer(new GeneralAstronauticPlatformScreenLayer(this));
	}

	// 模型随平台朝向旋转, 屏幕层处于模型空间无需额外处理
	@Override
	protected Direction getFacing(GeneralAstronauticPlatformBlockEntity block) {
		BlockState state = block.getBlockState();
		if (!state.hasProperty(GeneralAstronauticPlatformBlock.HORIZONTAL_FACING)) {
			return Direction.SOUTH;
		}
		return state.getValue(GeneralAstronauticPlatformBlock.HORIZONTAL_FACING);
	}
}
