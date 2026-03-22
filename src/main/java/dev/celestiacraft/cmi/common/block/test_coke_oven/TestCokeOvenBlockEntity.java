package dev.celestiacraft.cmi.common.block.test_coke_oven;

import blusunrize.immersiveengineering.common.register.IEFluids;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockEntity;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenFluidCapability;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenItemCapability;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenItemHandler;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TestCokeOvenBlockEntity extends ControllerBlockEntity {
	public TestCokeOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_COKE_OVEN);
	}

	private final CokeOvenItemCapability itemHandler;

	{
		assert level != null;
		itemHandler = new CokeOvenItemCapability((TestCokeOvenIOBlockEntity) level.getBlockEntity(this.getBlockPos().below()));
	}

	private LazyOptional<IItemHandler> itemCap = LazyOptional.empty();

	private final CokeOvenFluidCapability fluidHandler = new CokeOvenFluidCapability((TestCokeOvenIOBlockEntity) level.getBlockEntity(this.getBlockPos().below()));
	private LazyOptional<IFluidHandler> fluidCap = LazyOptional.empty();

	@Getter
	private FluidStack fluid = FluidStack.EMPTY;
	private int workTimer = 0;

	public static void tick(Level level, BlockPos pos, BlockState state, TestCokeOvenBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		entity.runRecipe();
	}

	private void runRecipe() {
		if (level == null || level.isClientSide()) {
			return;
		}

		ItemStack input = itemHandler.getStackInSlot(0);
		ItemStack output = itemHandler.getStackInSlot(1);
		int timeToWork = 20;

		boolean canWork = isStructureValid() && input.is(ItemTags.LOGS) && output.getCount() < 64;

		if (!canWork) {
			workTimer = 0;
			return;
		}

		workTimer++;

		setChanged();

		if (workTimer >= timeToWork) {
			workTimer = 0;

			input.shrink(1);
			itemHandler.insertItem(1, Items.CHARCOAL.getDefaultInstance(), false);
			fillFluid(new FluidStack(IEFluids.CREOSOTE.getStill(), 125), IFluidHandler.FluidAction.EXECUTE);
		}
	}

	public int fillFluid(FluidStack stack, IFluidHandler.FluidAction action) {
		if (!isStructureValid() || stack.isEmpty()) return 0;

		int fillable = Math.min(stack.getAmount(), 4000 - fluid.getAmount());

		if (action.execute() && fillable > 0) {
			if (fluid.isEmpty()) {
				fluid = new FluidStack(stack, fillable);
			} else {
				fluid.grow(fillable);
			}
			setChanged();
		}

		return fillable;
	}

	public FluidStack drainFluid(int amount, IFluidHandler.FluidAction action) {
		if (!isStructureValid() || fluid.isEmpty()) return FluidStack.EMPTY;

		int drained = Math.min(amount, fluid.getAmount());
		FluidStack result = new FluidStack(fluid, drained);

		if (action.execute()) {
			fluid.shrink(drained);
			if (fluid.isEmpty()) fluid = FluidStack.EMPTY;
			setChanged();
		}

		return result;
	}

	@Override
	protected String getMultiblockKey() {
		return String.format("multiblock.building.%s.test_coke_oven", Cmi.MODID);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);

		tag.put("Inventory", itemHandler.serializeNBT());
		tag.put("Fluid", fluid.writeToNBT(new CompoundTag()));
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);

		itemHandler.deserializeNBT(tag.getCompound("Inventory"));
		fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(Objects.requireNonNull(level.getBlockEntity(this.getBlockPos().below())));
	}

	@Override
	public void onLoad() {
		super.onLoad();

		this.itemCap = LazyOptional.of(() -> new CokeOvenItemHandler(itemHandler, (TestCokeOvenIOBlockEntity) level.getBlockEntity(this.getBlockPos().below())));
		this.fluidCap = LazyOptional.of(() -> fluidHandler);
	}
}