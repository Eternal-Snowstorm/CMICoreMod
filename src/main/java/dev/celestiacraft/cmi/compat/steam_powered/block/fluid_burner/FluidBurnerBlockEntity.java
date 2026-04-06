package dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.teammoeg.steampowered.content.burner.BurnerBlock;
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
	public FluidStack fluid = FluidStack.EMPTY;
	private int capacity;
	private double efficiency;

	public FluidBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public abstract int getFluidTankCapacity();

	protected abstract double getEfficiency();

	protected int HURemain;

	private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> {
		return new FluidBurnerFluidHandler(this);
	});

	// 缓存
	private FluidBurnRecipe cachedRecipe;

	@Override
	public void tick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		BlockState state = level.getBlockState(worldPosition);

		if (!tryConsumeAndEmit()) {
			if (state.getValue(BurnerBlock.LIT)) {
				level.setBlockAndUpdate(worldPosition, state.setValue(BurnerBlock.LIT, false));
			}
			return;
		}

		if (!state.getValue(BurnerBlock.LIT)) {
			level.setBlockAndUpdate(worldPosition, state.setValue(BurnerBlock.LIT, true));
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

		int required = recipe.getFluid().getRequiredAmount();

		if (fluid.getAmount() < required) {
			return false;
		}

		// 每tick消耗
		fluid.shrink(required);
		if (fluid.getAmount() <= 0) {
			fluid = FluidStack.EMPTY;
		}

		float hu = (float) (recipe.getHu() * getEfficiency());
		emitHeat(hu);

		setChanged();
		return true;
	}

	public FluidBurnRecipe findRecipe(FluidStack stack) {
		if (level == null) {
			return null;
		}

		if (cachedRecipe != null && cachedRecipe.matches(stack)) {
			return cachedRecipe;
		}

		cachedRecipe = level.getRecipeManager()
				.getAllRecipesFor(CmiRecipeType.FLUID_BURN.get())
				.stream()
				.filter((recipe) -> {
					return recipe.matches(stack);
				})
				.findFirst()
				.orElse(null);

		return cachedRecipe;
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
		HURemain = nbt.getInt("hu");
		super.read(nbt, clientPacket);
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		CompoundTag tankTag = new CompoundTag();

		fluid.writeToNBT(tankTag);
		nbt.put("tank", tankTag);
		nbt.putInt("hu", HURemain);
		super.write(nbt, clientPacket);
	}

	protected void emitHeat(float value) {
		BlockEntity be = level.getBlockEntity(this.getBlockPos().above());
		if (be instanceof IHeatReceiver receiver) {
			receiver.commitHeat(value);
		}
	}
}