package dev.celestiacraft.cmi.common.block.void_dust_collector;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.BasicBlockFacing;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class VoidDustCollectorBlock extends BasicBlock implements IBE<VoidDustCollectorBlockEntity> {
	public VoidDustCollectorBlock(Properties properties) {
		super(Properties.copy(Blocks.IRON_BLOCK)
				.sound(SoundType.NETHERITE_BLOCK));
	}

	@Override
	protected BasicBlockFacing useFacingType() {
		return BasicBlockFacing.HORIZONTAL;
	}

	@Override
	protected boolean useLitState() {
		return true;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return null;
		}

		return createTickerHelper(
				type,
				CmiBlockEntity.VOID_DUST_COLLECTOR.get(),
				VoidDustCollectorBlockEntity::tick
		);
	}

	@Override
	public Class<VoidDustCollectorBlockEntity> getBlockEntityClass() {
		return VoidDustCollectorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends VoidDustCollectorBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.VOID_DUST_COLLECTOR.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						BlockModelProvider models = provider.models();
						Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
						boolean working = state.getValue(BasicBlock.LIT);
						return ConfiguredModel.builder()
								.modelFile(models.getExistingFile(provider.modLoc(working ? "block/void_dust_collector/on" : "block/void_dust_collector/off")))
								.rotationY((int) facing.toYRot())
								.build();
					});
		};
	}
}