package dev.celestiacraft.cmi.common.entity.space_elevator;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class SpaceElevatorEntity extends Entity implements GeoEntity {
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private static CameraType previous = null;

	public SpaceElevatorEntity(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	/**
	 * 不渲染玩家
	 *
	 * @param event 事件
	 */
	@SubscribeEvent
	public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();

		if (player.getVehicle() instanceof SpaceElevatorEntity) {
			event.setCanceled(true);
		}
	}

	/**
	 * 切换相机为第三人称
	 *
	 * @param event 事件
	 */
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.player == null) {
			return;
		}

		Entity vehicle = mc.player.getVehicle();

		// 坐上电梯
		if (vehicle instanceof SpaceElevatorEntity) {
			if (previous == null) {
				previous = mc.options.getCameraType();
				mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}
		} else {
			// 下来恢复
			if (previous != null) {
				mc.options.setCameraType(previous);
				previous = null;
			}
		}
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
		registrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
		AnimationController<T> controller = state.getController();
		RawAnimation begin = RawAnimation.begin();

		controller.setAnimation(begin.then("idle", Animation.LoopType.LOOP));

		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
	}

	@Override
	protected boolean canAddPassenger(@NotNull Entity entity) {
		// return this.getPassengers().size() < 4;
		return this.getPassengers().isEmpty();
	}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}

		if (!level().isClientSide()) {
			if (player.isPassenger()) {
				return InteractionResult.FAIL;
			}

			if (player.startRiding(this)) {
				return InteractionResult.CONSUME;
			}

			return InteractionResult.FAIL;
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean isAttackable() {
		return super.isAttackable();
	}

	@Override
	public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction function) {
		if (this.hasPassenger(passenger)) {
			double x = this.getX();
			double y = this.getY() - 0.5;
			double z = this.getZ();

			function.accept(passenger, x, y, z);
		}
	}
}