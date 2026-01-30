package top.nebula.cmi.common.block.accelerator_motor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import top.nebula.cmi.config.CommonConfig;

public class AcceleratorMotorScrollValueBehaviour extends KineticScrollValueBehaviour {
	public AcceleratorMotorScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
		super(label, be, slot);
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		int maxSpeed = CommonConfig.ACCELERATOR_MOTOR_MAX_SPEED.get();
		ImmutableList<Component> rows = ImmutableList.of(
				Components.literal("⟳").withStyle(ChatFormatting.BOLD),
				Components.literal("⟲").withStyle(ChatFormatting.BOLD)
		);
		ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
		return new ValueSettingsBoard(label, maxSpeed, 16, rows, formatter);
	}
}