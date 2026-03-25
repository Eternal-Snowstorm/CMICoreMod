package dev.celestiacraft.cmi.common.entity.qi_month;

import dev.celestiacraft.cmi.common.register.CmiEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class QiMonthEntity extends Animal implements GeoEntity {
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public QiMonthEntity(EntityType<? extends Animal> type, Level level) {
		super(type, level);
	}

	@Override
	public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob mob) {
		return CmiEntity.QI_MONTH.create(level);
	}

	public static AttributeSupplier setAttributes() {
		return Animal.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 40)
				.build();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
		registrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
		if (state.isMoving()) {
			state.getController().setAnimation(RawAnimation.begin()
					.then("move", Animation.LoopType.LOOP));
			return PlayState.CONTINUE;
		}

		state.getController().setAnimation(RawAnimation.begin()
				.then("idle", Animation.LoopType.LOOP));

		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}