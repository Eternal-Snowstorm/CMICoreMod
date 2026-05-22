package dev.celestiacraft.cmi.compat.jade;

import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SpaceElevatorEntityProvider implements IEntityComponentProvider {
	INSTANCE;

	private static final ResourceLocation UID = CmiType.addType("space_elevator_cargo_fluid");

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!(accessor.getEntity() instanceof SpaceElevatorEntity elevator)) {
			return;
		}
		FluidStack stored = elevator.getCargoFluid().getFluid();
		int capacity = elevator.getCargoFluid().getCapacity();
		if (stored.isEmpty()) {
			tooltip.add(Component.translatable("jade.tip.cmi.space_elevator.cargo_fluid.empty", capacity).withStyle(ChatFormatting.GRAY));
			return;
		}
		tooltip.add(Component.translatable(
				"jade.tip.cmi.space_elevator.cargo_fluid",
				stored.getDisplayName(),
				stored.getAmount(),
				capacity
		).withStyle(ChatFormatting.AQUA));
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}
}
