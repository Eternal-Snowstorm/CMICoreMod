package dev.celestiacraft.cmi.common.block.space_elevator_top;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

public class SpaceElevatorTopBlock extends BasicBlock implements IBE<SpaceElevatorTopBlockEntity> {
	public SpaceElevatorTopBlock(Properties properties) {
		super(Properties.copy(Blocks.IRON_BLOCK)
				.sound(SoundType.NETHERITE_BLOCK)
				.noOcclusion()
				.dynamicShape()
				.strength(5.0F, 1200.0F));
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public Class<SpaceElevatorTopBlockEntity> getBlockEntityClass() {
		return SpaceElevatorTopBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SpaceElevatorTopBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.SPACE_ELEVATOR_TOP.get();
	}

	@Override
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}
		if (SpaceElevatorConstructionHandler.isWrench(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.PASS;
		}

		SpaceElevatorEntity existing = SpaceElevatorConstructionHandler.getNearbyElevator(serverLevel, pos);
		if (existing != null && existing.getFirstPassenger() == null) {
			serverPlayer.startRiding(existing);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						return ConfiguredModel.builder()
								.modelFile(provider.models().getExistingFile(provider.modLoc("block/space_elevator_top")))
								.build();
					});
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> genItemModel() {
		return (context, provider) -> {
			provider.getBuilder(context.getName())
					.parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"))
					.transforms()
					.transform(ItemDisplayContext.GUI)
					.rotation(30.0F, 45.0F, 0.0F)
					.translation(0.0F, 0.0F, 0.0F)
					.scale(0.10F)
					.end()
					.transform(ItemDisplayContext.GROUND)
					.rotation(0.0F, 0.0F, 0.0F)
					.translation(0.0F, 2.0F, 0.0F)
					.scale(0.08F)
					.end()
					.transform(ItemDisplayContext.FIXED)
					.rotation(0.0F, 0.0F, 0.0F)
					.translation(0.0F, 0.0F, 0.0F)
					.scale(0.12F)
					.end()
					.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
					.rotation(75.0F, 45.0F, 0.0F)
					.translation(0.0F, 2.5F, 0.0F)
					.scale(0.10F)
					.end()
					.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
					.rotation(75.0F, 45.0F, 0.0F)
					.translation(0.0F, 2.5F, 0.0F)
					.scale(0.10F)
					.end()
					.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
					.rotation(0.0F, 45.0F, 0.0F)
					.translation(0.0F, 4.0F, 2.0F)
					.scale(0.12F)
					.end()
					.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
					.rotation(0.0F, 225.0F, 0.0F)
					.translation(0.0F, 4.0F, 2.0F)
					.scale(0.12F)
					.end()
					.end();
		};
	}
}
