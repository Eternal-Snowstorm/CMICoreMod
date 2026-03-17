package dev.celestiacraft.cmi.common.block.water_pump;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.cmi.api.multiblock.MultiblockControllerBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import dev.celestiacraft.cmi.utils.ModResource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaterPumpBlockEntity extends MultiblockControllerBlockEntity implements IHaveGoggleInformation {
	public WaterPumpBlockEntity(BlockEntityType<? extends WaterPumpBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.WATER_PUMP);
	}

	@Override
	protected String getMultiblockKey() {
		return String.format("multiblock.building.%s.water_pump", Cmi.MODID);
	}

	@Override
	protected int getRenderOffsetY() {
		return -1;
	}

	private final IFluidHandler fluidHandler = new IFluidHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int amount) {
			if (isStructureValid()) {
				if (isOcean()) {
					return new FluidStack(ForgeRegistries.FLUIDS.getValue(ModResource.SEA_WATER), Integer.MAX_VALUE);
				}
				return new FluidStack(Fluids.WATER, Integer.MAX_VALUE);
			}
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int amount) {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isFluidValid(int amount, @NotNull FluidStack fluidStack) {
			return false;
		}

		@Override
		public int fill(FluidStack fluidStack, FluidAction fluidAction) {
			return 0;
		}

		@Override
		public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
			if (isStructureValid()) {
				if (isOcean()) {
					if (fluidStack.getFluid() == ForgeRegistries.FLUIDS.getValue(ModResource.SEA_WATER)) {
						return fluidStack;
					}
				} else if (fluidStack.getFluid() == Fluids.WATER) {
					return fluidStack;
				}
				return FluidStack.EMPTY;
			}
			return FluidStack.EMPTY;
		}

		@Override
		public @NotNull FluidStack drain(int amount, FluidAction fluidAction) {
			if (isStructureValid()) {
				if (isOcean()) {
					return new FluidStack(ForgeRegistries.FLUIDS.getValue(ModResource.SEA_WATER), amount);
				}
				return new FluidStack(Fluids.WATER, amount);
			}
			return FluidStack.EMPTY;
		}
	};

	private LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> fluidHandler);

	private boolean isOcean() {
		if (this.level != null) {
			return this.level.getBiome(this.getBlockPos()).is(BiomeTags.IS_OCEAN) &&
					this.getBlockPos().getY() == 62;
		}
		return false;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCapability.cast();
		}
		return super.getCapability(capability, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidCapability.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fluidCapability = LazyOptional.of(() -> fluidHandler);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (isStructureValid()) {
			CmiLang.builder()
					.translate("tooltip.water_pump.functional")
					.forGoggles(tooltip);
		} else {
			CmiLang.builder()
					.translate("tooltip.water_pump.non_functional")
					.forGoggles(tooltip);
		}
		return true;
	}
}