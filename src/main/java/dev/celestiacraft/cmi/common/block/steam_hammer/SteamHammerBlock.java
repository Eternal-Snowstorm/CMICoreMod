package dev.celestiacraft.cmi.common.block.steam_hammer;

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
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;

public class SteamHammerBlock extends MechanicalPressBlock {
	public SteamHammerBlock(Properties properties) {
		super(properties.mapColor(MapColor.PODZOL)
				.strength(5.0F, 6.0F)
				.sound(SoundType.LANTERN)
				.noOcclusion());
	}

	@Override
	public BlockEntityType<? extends SteamHammerBlockEntity> getBlockEntityType() {
		return CmiBlockEntityTypes.STEAM_HAMMER.get();
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Block.box(0, 0, 0, 16, 16, 16);
	}
}