package dev.celestiacraft.cmi.common.block.general_astronautic_platform;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortShape;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortType;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlockEntity;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.structure.SpaceElevatorBaseStructure;
import dev.celestiacraft.cmi.common.item.tool.DigitalLocator;
import dev.celestiacraft.cmi.common.register.block.SpaceElevatorBlocks;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.compat.adastra.AdAstraPlatformRocket;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.IEntityBlock;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class GeneralAstronauticPlatformBlock extends BasicBlock implements IBE<GeneralAstronauticPlatformBlockEntity> {
	private static final Map<Vec3i, IoPortType> IO_TYPES = Map.of(
			new Vec3i(-2, 0, 0), IoPortType.INPUT_ITEM,
			new Vec3i(2, 0, 0), IoPortType.OUTPUT_ITEM,
			new Vec3i(-2, 1, 0), IoPortType.INPUT_FLUID
	);

	public GeneralAstronauticPlatformBlock(Properties properties) {
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
	public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide()) {
			deployStructure(level, pos);
		}
	}

	// 右键平台顶面放置 Ad Astra 火箭，平台上已有任意火箭时拒绝
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		if (held.getItem() instanceof DigitalLocator) {
			return DigitalLocator.useOnPlatform(level, pos, player, held);
		}
		if (!AdAstraPlatformRocket.isRocketItem(held)) {
			return super.use(state, level, pos, player, hand, hit);
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(level.getBlockEntity(pos) instanceof GeneralAstronauticPlatformBlockEntity platform)) {
			return InteractionResult.PASS;
		}
		if (platform.hasAnyRocket()) {
			player.displayClientMessage(Component.translatable("text.cmi.general_astronautic_platform.rocket_exists"), true);
			return InteractionResult.PASS;
		}
		Rocket rocket = AdAstraPlatformRocket.createRocket(level, held, pos, hit.getDirection());
		if (rocket == null) {
			return InteractionResult.PASS;
		}
		level.addFreshEntity(rocket);
		level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
		if (!player.getAbilities().instabuild) {
			held.shrink(1);
		}
		return InteractionResult.CONSUME;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return null;
		}
		return IEntityBlock.createTickerHelper(type, CmiBlockEntity.GENERAL_ASTRONAUTIC_PLATFORM.get(), GeneralAstronauticPlatformBlockEntity::tick);
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean moved) {
		if (!level.isClientSide() && !state.is(newState.getBlock())) {
			releaseBinding(level, pos);
			dismantlePorts(level, pos);
		}
		super.onRemove(state, level, pos, newState, moved);
	}

	// 平台被破坏时解除双方绑定
	private static void releaseBinding(@NotNull Level level, @NotNull BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof GeneralAstronauticPlatformBlockEntity platform) {
			platform.releaseLandingLoad();
			PlatformBindingHandler.release(platform);
		}
	}

	@Nullable
	public static BlockPos findPlatform(@NotNull BlockGetter level, @NotNull BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof GeneralAstronauticPlatformBlock) {
			return pos;
		}
		if (!(state.getBlock() instanceof SpaceElevatorIoPortBlock)) {
			return null;
		}
		BlockPos controllerPos = SpaceElevatorIoPortBlock.getControllerPos(level, pos);
		if (controllerPos == null) {
			return null;
		}
		if (!(level.getBlockState(controllerPos).getBlock() instanceof GeneralAstronauticPlatformBlock)) {
			return null;
		}
		return controllerPos;
	}

	public static void deployStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		for (Map.Entry<Vec3i, IoPortShape> entry : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.entrySet()) {
			Vec3i offset = entry.getKey();
			if (isCenter(offset)) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			BlockState existing = level.getBlockState(portPos);
			if (!(existing.getBlock() instanceof SpaceElevatorIoPortBlock) && !existing.canBeReplaced()) {
				continue;
			}
			BlockState portState = SpaceElevatorBlocks.ASTRONAUTIC_PLATFORM_IO_PORT.getDefaultState()
					.setValue(SpaceElevatorIoPortBlock.IO_TYPE, IO_TYPES.getOrDefault(offset, IoPortType.NONE))
					.setValue(SpaceElevatorIoPortBlock.SHAPE, entry.getValue());
			level.setBlock(portPos, portState, 3);
			if (level.getBlockEntity(portPos) instanceof SpaceElevatorIoPortBlockEntity portBe) {
				portBe.setControllerPos(controllerPos);
			}
		}
	}

	public static void dismantleStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		dismantlePorts(level, controllerPos);
		if (level.getBlockState(controllerPos).getBlock() instanceof GeneralAstronauticPlatformBlock) {
			level.removeBlock(controllerPos, false);
		}
	}

	private static void dismantlePorts(@NotNull Level level, @NotNull BlockPos controllerPos) {
		for (Vec3i offset : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.keySet()) {
			if (isCenter(offset)) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			BlockState portState = level.getBlockState(portPos);
			if (!(portState.getBlock() instanceof SpaceElevatorIoPortBlock)) {
				continue;
			}
			if (!controllerPos.equals(SpaceElevatorIoPortBlock.getControllerPos(level, portPos))) {
				continue;
			}
			level.removeBlock(portPos, false);
		}
	}

	private static boolean isCenter(@NotNull Vec3i offset) {
		return offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0;
	}

	@Override
	public Class<GeneralAstronauticPlatformBlockEntity> getBlockEntityClass() {
		return GeneralAstronauticPlatformBlockEntity.class;
	}

	@Override
	public BlockEntityType<GeneralAstronauticPlatformBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.GENERAL_ASTRONAUTIC_PLATFORM.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						return ConfiguredModel.builder()
								.modelFile(provider.models().getExistingFile(provider.modLoc("block/general_astronautic_platform")))
								.build();
					});
		};
	}
}
