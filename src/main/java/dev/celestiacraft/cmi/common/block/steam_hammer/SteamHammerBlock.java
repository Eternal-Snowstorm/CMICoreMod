package dev.celestiacraft.cmi.common.block.steam_hammer;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import org.jetbrains.annotations.NotNull;

public class SteamHammerBlock extends MechanicalPressBlock {
	public SteamHammerBlock(Properties properties) {
		super(properties.mapColor(MapColor.PODZOL)
				.strength(5.0F, 6.0F)
				.sound(SoundType.LANTERN)
				.noOcclusion());
	}

	@Override
	public BlockEntityType<? extends SteamHammerBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.STEAM_HAMMER.get();
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Block.box(0, 0, 0, 16, 16, 16);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						BlockModelProvider models = provider.models();
						Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
						return ConfiguredModel.builder()
								.modelFile(models.getExistingFile(provider.modLoc("block/steam_hammer/block")))
								.rotationY((int) facing.toYRot())
								.build();
					});
		};
	}
}