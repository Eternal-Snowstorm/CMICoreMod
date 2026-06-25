package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortShape;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortType;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlockEntity;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.structure.SpaceElevatorBaseStructure;
import dev.celestiacraft.cmi.common.register.CmiBlockEntity;
import dev.celestiacraft.cmi.common.register.block.SpaceElevatorBlocks;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SpaceElevatorBaseConsoleBlock extends BasicBlock implements IBE<SpaceElevatorBaseConsoleBlockEntity> {
	public SpaceElevatorBaseConsoleBlock(Properties properties) {
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
	public Class<SpaceElevatorBaseConsoleBlockEntity> getBlockEntityClass() {
		return SpaceElevatorBaseConsoleBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends SpaceElevatorBaseConsoleBlockEntity> getBlockEntityType() {
		return CmiBlockEntity.SPACE_ELEVATOR_BASE_CONSOLE.get();
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return null;
		}
		return createTickerHelper(type, CmiBlockEntity.SPACE_ELEVATOR_BASE_CONSOLE.get(), SpaceElevatorBaseConsoleBlockEntity::serverTick);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		for (Vec3i offset : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.keySet()) {
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos check = clickedPos.offset(offset);
			if (!level.getWorldBorder().isWithinBounds(check)) {
				return null;
			}
			BlockState existing = level.getBlockState(check);
			if (existing.getBlock() instanceof SpaceElevatorIoPortBlock) {
				continue;
			}
			if (!existing.canBeReplaced(context)) {
				return null;
			}
		}
		return this.defaultBlockState();
	}

	@Override
	public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide()) {
			deployStructure(level, pos);
		}
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean moved) {
		if (!level.isClientSide() && !state.is(newState.getBlock())) {
			dismantlePorts(level, pos);
		}
		super.onRemove(state, level, pos, newState, moved);
	}

	public static void ensureStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		boolean needsDeploy = false;
		for (Map.Entry<Vec3i, IoPortShape> entry : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.entrySet()) {
			Vec3i offset = entry.getKey();
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			BlockState existing = level.getBlockState(portPos);
			if (!(existing.getBlock() instanceof SpaceElevatorIoPortBlock)) {
				needsDeploy = true;
				break;
			}
			if (existing.getValue(SpaceElevatorIoPortBlock.SHAPE) != entry.getValue()) {
				needsDeploy = true;
				break;
			}
		}
		if (needsDeploy) {
			deployStructure(level, controllerPos);
		}
	}

	public static void deployStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		Map<Vec3i, IoPortType> ioByOffset = new HashMap<>();
		for (Map.Entry<IoPortType, Vec3i> entry : SpaceElevatorBaseStructure.IO_PORT_OFFSETS.entrySet()) {
			ioByOffset.put(entry.getValue(), entry.getKey());
		}

		for (Map.Entry<Vec3i, IoPortShape> entry : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.entrySet()) {
			Vec3i offset = entry.getKey();
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			BlockState existing = level.getBlockState(portPos);
			if (!(existing.getBlock() instanceof SpaceElevatorIoPortBlock) && !existing.canBeReplaced()) {
				continue;
			}
			IoPortType ioType = ioByOffset.getOrDefault(offset, IoPortType.NONE);
			BlockState portState = SpaceElevatorBlocks.SPACE_ELEVATOR_IO_PORT.getDefaultState()
					.setValue(SpaceElevatorIoPortBlock.IO_TYPE, ioType)
					.setValue(SpaceElevatorIoPortBlock.SHAPE, entry.getValue());
			level.setBlock(portPos, portState, 3);
			if (level.getBlockEntity(portPos) instanceof SpaceElevatorIoPortBlockEntity portBe) {
				portBe.setControllerPos(controllerPos);
			}
		}
	}

	public static void dismantleStructure(@NotNull Level level, @NotNull BlockPos controllerPos) {
		dismantlePorts(level, controllerPos);
		if (level.getBlockState(controllerPos).getBlock() instanceof SpaceElevatorBaseConsoleBlock) {
			level.removeBlock(controllerPos, false);
		}
	}

	private static void dismantlePorts(@NotNull Level level, @NotNull BlockPos controllerPos) {
		for (Vec3i offset : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.keySet()) {
			if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
				continue;
			}
			BlockPos portPos = controllerPos.offset(offset);
			if (level.getBlockState(portPos).getBlock() instanceof SpaceElevatorIoPortBlock) {
				level.removeBlock(portPos, false);
			}
		}
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStatesExcept((state) -> {
						return ConfiguredModel.builder()
								.modelFile(provider.models().getExistingFile(provider.modLoc("block/space_elevator_base_console")))
								.build();
					});
		};
	}
}
