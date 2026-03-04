package dev.celestiacraft.cmi.common.recipe.fan_processig.freezing;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Color;
import dev.celestiacraft.cmi.common.register.CmiCreateRecipe;
import dev.celestiacraft.cmi.tag.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FreezingType implements FanProcessingType {
	public static final FreezingWrapper FREEZING_WRAPPER = new FreezingWrapper();

	@Override
	public boolean isValidAt(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		return state.is(ModBlockTags.FREEZING_CATALYST);
	}

	/**
	 * 优先级不允许和别的Mod或Create自己的一样!
	 *
	 * @return
	 */
	@Override
	public int getPriority() {
		return 500;
	}

	@Override
	public boolean canProcess(ItemStack stack, Level level) {
		FREEZING_WRAPPER.setItem(0, stack);
		Optional<FreezingRecipe> recipe = CmiCreateRecipe.FREEZING.find(FREEZING_WRAPPER, level);

		return recipe.isPresent();
	}

	@Override
	public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
		FREEZING_WRAPPER.setItem(0, stack);
		Optional<FreezingRecipe> recipe = CmiCreateRecipe.FREEZING.find(FREEZING_WRAPPER, level);

		return recipe.map((freezing) -> {
			return RecipeApplier.applyRecipeOn(level, stack, freezing);
		}).orElse(null);
	}

	@Override
	public void spawnProcessingParticles(Level level, Vec3 pos) {
		if (level.random.nextInt(8) != 0) {
			return;
		}
		level.addParticle(
				ParticleTypes.POOF,
				pos.x, pos.y + .25f,
				pos.z,
				0,
				1 / 16f,
				0
		);
	}

	@Override
	public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
		particleAccess.setColor(Color.mixColors(0xFFFFFF, 0xFAFEFF, random.nextFloat()));
		particleAccess.setAlpha(1f);

		if (random.nextFloat() < 1 / 32f) {
			particleAccess.spawnExtraParticle(ParticleTypes.SNOWFLAKE, .125f);
		}
		if (random.nextFloat() < 1 / 32f) {
			particleAccess.spawnExtraParticle(ParticleTypes.SNOWFLAKE, .125f);
		}
	}

	@Override
	public void affectEntity(Entity entity, Level level) {
		if (level.isClientSide()) {
			return;
		}

		if (entity.canFreeze()) {
			entity.isInPowderSnow = true;
			entity.setTicksFrozen(entity.getTicksRequiredToFreeze());
			entity.hurt(entity.damageSources().freeze(), 1);
		}

		if (entity.getType() == EntityType.BLAZE) {
			entity.hurt(entity.damageSources().freeze(), 2);
		}
		if (entity.isOnFire()) {
			entity.clearFire();
			level.playSound(
					null,
					entity.blockPosition(),
					SoundEvents.GENERIC_EXTINGUISH_FIRE,
					SoundSource.NEUTRAL,
					0.7F,
					1.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
			);
		}
	}
}
