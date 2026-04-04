package dev.celestiacraft.cmi.common.block.water_pump;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockEntity;
import dev.celestiacraft.cmi.common.block.water_pump.capability.WaterPumpFluidCapability;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaterPumpBlockEntity extends ControllerBlockEntity implements IHaveGoggleInformation {
	public WaterPumpBlockEntity(BlockEntityType<? extends WaterPumpBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.WATER_PUMP);
	}

	@Override
	protected String getModId() {
		return Cmi.MODID;
	}

	@Override
	protected String getMultiblockName() {
		return "water_pump";
	}

	private WaterPumpFluidCapability fluidHandler;
	private LazyOptional<IFluidHandler> fluidCapability = LazyOptional.empty();

	@Override
	public void onLoad() {
		super.onLoad();
		this.fluidHandler = new WaterPumpFluidCapability(this);
		this.fluidCapability = LazyOptional.of(() -> fluidHandler);
	}

	public boolean isOcean() {
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
		this.fluidHandler = new WaterPumpFluidCapability(this);
		this.fluidCapability = LazyOptional.of(() -> fluidHandler);
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