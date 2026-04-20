package dev.celestiacraft.cmi.common.block.test_coke_oven;

import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenFluidCapability;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenItemCapability;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenItemHandler;
import dev.celestiacraft.libs.api.register.multiblock.machine.IOBlockEntity;
import dev.celestiacraft.libs.api.register.multiblock.machine.MachineControllerBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestCokeOvenIOBlockEntity extends IOBlockEntity {
	public TestCokeOvenIOBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected @NotNull List<Class<? extends MachineControllerBlockEntity>> supportedControllers() {
		return List.of(TestCokeOvenBlockEntity.class);
	}

	@Getter
	private FluidStack fluid = FluidStack.EMPTY;

	private final CokeOvenItemCapability itemHandler = new CokeOvenItemCapability(this);
	private LazyOptional<IItemHandler> itemCap = LazyOptional.empty();

	private final CokeOvenFluidCapability fluidHandler = new CokeOvenFluidCapability(this);
	private LazyOptional<IFluidHandler> fluidCap = LazyOptional.empty();

	public ItemStackHandler getInternalItemHandler() {
		return itemHandler;
	}

	public ItemStack getInternalStackInSlot(int slot) {
		return itemHandler.getStackInSlot(slot);
	}

	public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate) {
		return itemHandler.insertItem(slot, stack, simulate);
	}

	public ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
		return itemHandler.extractItem(slot, amount, simulate);
	}

	public int fillFluid(FluidStack stack, IFluidHandler.FluidAction action) {
		if (stack.isEmpty()) return 0;

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
		if (fluid.isEmpty()) return FluidStack.EMPTY;

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
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction direction) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return itemCap.cast();
		}
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCap.cast();
		}
		return super.getCapability(capability, direction);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		itemCap.invalidate();
		fluidCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();

		this.itemCap = LazyOptional.of(() -> new CokeOvenItemHandler(itemHandler, this));
		this.fluidCap = LazyOptional.of(() -> fluidHandler);
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
	public void onLoad() {
		super.onLoad();

		this.itemCap = LazyOptional.of(() -> new CokeOvenItemHandler(itemHandler, this));
		this.fluidCap = LazyOptional.of(() -> fluidHandler);
	}
}
