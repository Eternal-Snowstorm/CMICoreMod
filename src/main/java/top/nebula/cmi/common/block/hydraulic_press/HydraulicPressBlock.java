package top.nebula.cmi.common.block.hydraulic_press;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import top.nebula.cmi.common.register.ModBlockEntityTypes;

public class HydraulicPressBlock extends MechanicalPressBlock {
	public HydraulicPressBlock(Properties properties) {
		super(properties.mapColor(MapColor.PODZOL)
				.sound(SoundType.COPPER));
	}

	@Override
	public BlockEntityType<? extends HydraulicPressBlockEntity> getBlockEntityType() {
		return ModBlockEntityTypes.HYDRAULIC_PRESS.get();
	}
}