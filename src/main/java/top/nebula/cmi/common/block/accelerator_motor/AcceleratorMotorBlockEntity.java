package top.nebula.cmi.common.block.accelerator_motor;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate.SpeedLevel;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import top.nebula.cmi.common.register.ModBlocks;
import top.nebula.cmi.config.CommonConfig;

import java.util.List;

public class AcceleratorMotorBlockEntity extends GeneratingKineticBlockEntity {
	protected ScrollValueBehaviour generatedSpeed;

	public AcceleratorMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		int max = CommonConfig.ACCELERATOR_MOTOR_MAX_SPEED.get();

		generatedSpeed = new AcceleratorMotorScrollValueBehaviour(
				Lang.translateDirect("kinetics.creative_motor.rotation_speed"),
				this,
				new MotorValueBox()
		);
		generatedSpeed.between(-max, max);
		generatedSpeed.value = CommonConfig.ACCELERATOR_MOTOR_DEFAULT_SPEED.get();
		generatedSpeed.withCallback((integer) -> {
			this.updateGeneratedRotation();
		});
		behaviours.add(generatedSpeed);
	}

	@Override
	public void initialize() {
		super.initialize();

		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed()) {
			updateGeneratedRotation();
		}
	}

	@Override
	public float getGeneratedSpeed() {
		if (!ModBlocks.ACCELERATOR_MOTOR.has(getBlockState())) {
			return 0;
		}
		return convertToDirection(generatedSpeed.getValue(), getBlockState().getValue(AcceleratorMotorBlock.FACING));
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		Lang.translate("gui.speedometer.title")
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);

		SpeedLevel.getFormattedSpeedText(speed, isOverStressed())
				.forGoggles(tooltip);

		return true;
	}

	static class MotorValueBox extends ValueBoxTransform.Sided {
		@Override
		protected Vec3 getSouthLocation() {
			return VecHelper.voxelSpace(8, 8, 12.5);
		}

		@Override
		public Vec3 getLocalOffset(BlockState state) {
			Direction facing = state.getValue(AcceleratorMotorBlock.FACING);
			return super.getLocalOffset(state)
					.add(Vec3.atLowerCornerOf(facing.getNormal()).scale(-1 / 16f));
		}

		@Override
		public void rotate(BlockState state, PoseStack ms) {
			super.rotate(state, ms);
			Direction facing = state.getValue(AcceleratorMotorBlock.FACING);
			if (facing.getAxis() == Direction.Axis.Y) {
				return;
			}
			if (getSide() != Direction.UP) {
				return;
			}
			TransformStack.cast(ms).rotateZ(-AngleHelper.horizontalAngle(facing) + 180);
		}

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			Direction facing = state.getValue(AcceleratorMotorBlock.FACING);
			if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN) {
				return false;
			}
			return direction.getAxis() != facing.getAxis();
		}
	}
}