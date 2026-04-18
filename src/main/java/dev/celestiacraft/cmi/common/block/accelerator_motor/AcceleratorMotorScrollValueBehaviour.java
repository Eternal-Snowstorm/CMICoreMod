package dev.celestiacraft.cmi.common.block.accelerator_motor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import dev.celestiacraft.cmi.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class AcceleratorMotorScrollValueBehaviour extends KineticScrollValueBehaviour {
	public AcceleratorMotorScrollValueBehaviour(Component component, SmartBlockEntity be, ValueBoxTransform slot) {
		super(component, be, slot);
		withFormatter((integer) -> {
			return String.valueOf(Math.abs(integer));
		});
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		int maxSpeed = CommonConfig.ACCELERATOR_MOTOR_MAX_SPEED.get();
		ImmutableList<Component> rows = ImmutableList.of(
				Component.literal("⟳").withStyle(ChatFormatting.BOLD),
				Component.literal("⟲").withStyle(ChatFormatting.BOLD)
		);
		ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
		return new ValueSettingsBoard(label, maxSpeed, 16, rows, formatter);
	}
}