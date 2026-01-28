package top.nebula.cmi.common.block.hydraulic_press;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
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

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Block.box(0, 0, 0, 16, 16, 16);
	}
}