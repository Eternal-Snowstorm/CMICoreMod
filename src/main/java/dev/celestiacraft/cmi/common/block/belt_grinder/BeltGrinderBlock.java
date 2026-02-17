package dev.celestiacraft.cmi.common.block.belt_grinder;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;

public class BeltGrinderBlock extends HorizontalKineticBlock implements IBE<BeltGrinderBlockEntity> {
	public BeltGrinderBlock(Properties properties) {
		super(properties.mapColor(MapColor.STONE)
				.noOcclusion());
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return AllShapes.CASING_14PX.get(Direction.UP);
	}

	@Override
	public Class<BeltGrinderBlockEntity> getBlockEntityClass() {
		return BeltGrinderBlockEntity.class;
	}

	public BlockEntityType<? extends BeltGrinderBlockEntity> getBlockEntityType() {
		return CmiBlockEntityTypes.BELT_GRINDER.get();
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
		if (player.isSpectator() || !player.getItemInHand(hand).isEmpty()) {
			return InteractionResult.PASS;
		}
		if (result.getDirection() != Direction.UP) {
			return InteractionResult.PASS;
		}

		return onBlockEntityUse(worldIn, pos, (be) -> {
			for (int i = 0; i < be.inv.getSlots(); i++) {
				ItemStack heldItemStack = be.inv.getStackInSlot(i);
				if (!worldIn.isClientSide && !heldItemStack.isEmpty()) {
					player.getInventory().placeItemBackInInventory(heldItemStack);
				}
			}
			be.inv.clear();
			be.notifyUpdate();
			return InteractionResult.SUCCESS;
		});
	}

	@Override
	public void updateEntityAfterFallOn(@NotNull BlockGetter worldIn, @NotNull Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);
		if (!(entityIn instanceof ItemEntity)) {
			return;
		}
		if (entityIn.level().isClientSide()) {
			return;
		}

		BlockPos pos = entityIn.blockPosition();
		withBlockEntityDo(entityIn.level(), pos, (be) -> {
			if (be.getSpeed() == 0) {
				return;
			}
			be.insertItem((ItemEntity) entityIn);
		});
	}
}