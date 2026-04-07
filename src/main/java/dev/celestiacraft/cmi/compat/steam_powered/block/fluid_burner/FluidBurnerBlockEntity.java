package dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.teammoeg.steampowered.content.burner.IHeatReceiver;
import dev.celestiacraft.cmi.common.recipe.fluid_burn.FluidBurnRecipe;
import dev.celestiacraft.cmi.common.register.CmiRecipeType;
import dev.celestiacraft.cmi.compat.steam_powered.block.FluidBurnerFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class FluidBurnerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
	private FluidStack fluid = FluidStack.EMPTY;
	private FluidBurnRecipe cachedRecipe;
	private LazyOptional<IFluidHandler> fluidCap = LazyOptional.empty();

	public FluidBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public abstract int getFluidTankCapacity();

	protected abstract double getEfficiency();

	@Override
	public void tick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		BlockState state = level.getBlockState(worldPosition);

		if (!tryConsumeAndEmit()) {
			if (state.getValue(FluidBurnerBlock.LIT)) {
				level.setBlockAndUpdate(worldPosition, state.setValue(FluidBurnerBlock.LIT, false));
			}
			return;
		}

		if (!state.getValue(FluidBurnerBlock.LIT)) {
			level.setBlockAndUpdate(worldPosition, state.setValue(FluidBurnerBlock.LIT, true));
		}
	}

	private boolean tryConsumeAndEmit() {
		if (fluid.isEmpty()) {
			return false;
		}

		FluidBurnRecipe recipe = findRecipe(fluid);
		if (recipe == null) {
			return false;
		}

		int required = recipe.getAmount();
		if (fluid.getAmount() < required) {
			return false;
		}

		fluid.shrink(required);
		if (fluid.isEmpty()) {
			fluid = FluidStack.EMPTY;
			cachedRecipe = null;
		}

		emitHeat((float) (recipe.getHu() * getEfficiency()));
		onFluidChanged();
		return true;
	}

	public FluidBurnRecipe findRecipe(FluidStack stack) {
		if (level == null || stack.isEmpty()) {
			return null;
		}

		if (cachedRecipe != null && cachedRecipe.matches(stack)) {
			return cachedRecipe;
		}

		cachedRecipe = level.getRecipeManager()
				.getAllRecipesFor(CmiRecipeType.FLUID_BURN.get())
				.stream()
				.filter(recipe -> recipe.matches(stack))
				.findFirst()
				.orElse(null);

		return cachedRecipe;
	}

	public FluidStack getFluid() {
		return fluid.copy();
	}

	public boolean canFillFluid(FluidStack stack) {
		return !stack.isEmpty()
				&& findRecipe(stack) != null
				&& (fluid.isEmpty() || fluid.isFluidEqual(stack));
	}

	public int fillFluid(FluidStack resource, IFluidHandler.FluidAction action) {
		if (!canFillFluid(resource)) {
			return 0;
		}

		int fillAmount = Math.min(getFluidTankCapacity() - fluid.getAmount(), resource.getAmount());
		if (fillAmount <= 0) {
			return 0;
		}

		if (action.execute()) {
			if (fluid.isEmpty()) {
				fluid = new FluidStack(resource, fillAmount);
			} else {
				fluid.grow(fillAmount);
			}
			onFluidChanged();
		}

		return fillAmount;
	}

	public FluidStack drainFluid(int maxDrain, IFluidHandler.FluidAction action) {
		if (fluid.isEmpty() || maxDrain <= 0) {
			return FluidStack.EMPTY;
		}

		int drained = Math.min(maxDrain, fluid.getAmount());
		FluidStack result = new FluidStack(fluid, drained);

		if (action.execute()) {
			fluid.shrink(drained);
			if (fluid.isEmpty()) {
				fluid = FluidStack.EMPTY;
				cachedRecipe = null;
			}
			onFluidChanged();
		}

		return result;
	}

	public FluidStack drainFluid(FluidStack resource, IFluidHandler.FluidAction action) {
		if (resource.isEmpty() || fluid.isEmpty() || !fluid.isFluidEqual(resource)) {
			return FluidStack.EMPTY;
		}
		return drainFluid(resource.getAmount(), action);
	}

	private void onFluidChanged() {
		setChanged();
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction direction) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCap.cast();
		}
		return super.getCapability(capability, direction);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("tank"));
		if (fluid.isEmpty()) {
			fluid = FluidStack.EMPTY;
			cachedRecipe = null;
		}
		super.read(nbt, clientPacket);
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		CompoundTag tankTag = new CompoundTag();
		fluid.writeToNBT(tankTag);
		nbt.put("tank", tankTag);
		super.write(nbt, clientPacket);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		fluidCap = LazyOptional.of(() -> new FluidBurnerFluidHandler(this));
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fluidCap = LazyOptional.of(() -> new FluidBurnerFluidHandler(this));
	}

	protected void emitHeat(float value) {
		BlockEntity be = level.getBlockEntity(getBlockPos().above());
		if (be instanceof IHeatReceiver receiver) {
			receiver.commitHeat(value);
		}
	}
}
