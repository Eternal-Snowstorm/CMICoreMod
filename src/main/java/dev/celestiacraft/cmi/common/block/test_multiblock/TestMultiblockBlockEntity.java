package dev.celestiacraft.cmi.common.block.test_multiblock;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockEntity;
import dev.celestiacraft.cmi.common.block.test_multiblock.capability.TestEnergyCapability;
import dev.celestiacraft.cmi.common.block.test_multiblock.capability.TestFluidCapability;
import dev.celestiacraft.cmi.common.block.test_multiblock.capability.TestItemCapability;
import dev.celestiacraft.cmi.common.block.test_multiblock.capability.TestItemHandler;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class TestMultiblockBlockEntity extends ControllerBlockEntity {
	public TestMultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_MULTIBLOCK);
	}

	@Getter
	private int energyStored = 0;
	@Getter
	private FluidStack fluid = FluidStack.EMPTY;

	private final TestItemCapability itemHandler = new TestItemCapability(this);
	private LazyOptional<IItemHandler> itemCap = LazyOptional.empty();

	private final TestFluidCapability fluidHandler = new TestFluidCapability(this);
	private LazyOptional<IFluidHandler> fluidCap = LazyOptional.empty();

	private final TestEnergyCapability energyHandler = new TestEnergyCapability(this);
	private LazyOptional<IEnergyStorage> energyCap = LazyOptional.empty();

	@Override
	public void onLoad() {
		super.onLoad();

		this.itemCap = LazyOptional.of(() -> new TestItemHandler(itemHandler, this));
		this.fluidCap = LazyOptional.of(() -> fluidHandler);
		this.energyCap = LazyOptional.of(() -> energyHandler);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction direction) {
		if (!isStructureValid()) {
			return LazyOptional.empty();
		}

		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return itemCap.cast();
		}

		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCap.cast();
		}

		if (capability == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}

		return super.getCapability(capability, direction);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		itemCap.invalidate();
		fluidCap.invalidate();
		energyCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();

		this.itemCap = LazyOptional.of(() -> new TestItemHandler(itemHandler, this));
		this.fluidCap = LazyOptional.of(() -> fluidHandler);
		this.energyCap = LazyOptional.of(() -> energyHandler);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("Energy", energyStored);
		tag.put("Fluid", fluid.writeToNBT(new CompoundTag()));
		tag.put("Inventory", itemHandler.serializeNBT());
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		energyStored = tag.getInt("Energy");
		fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
		itemHandler.deserializeNBT(tag.getCompound("Inventory"));
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public int fillFluid(FluidStack stack, IFluidHandler.FluidAction action) {
		if (!isStructureValid() || stack.isEmpty()) return 0;

		int fillable = Math.min(stack.getAmount(), 32000 - fluid.getAmount());

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

	public int receiveEnergy(int maxReceive, boolean simulate) {
		int received = Math.min(32000 - energyStored, maxReceive);

		if (!simulate && received > 0) {
			energyStored += received;
			setChanged();
		}

		return received;
	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		int extracted = Math.min(energyStored, maxExtract);

		if (!simulate && extracted > 0) {
			energyStored -= extracted;
			setChanged();
		}

		return extracted;
	}

	@Override
	protected String getModId() {
		return Cmi.MODID;
	}

	@Override
	protected String getMultiblockName() {
		return "test_multiblock";
	}
}