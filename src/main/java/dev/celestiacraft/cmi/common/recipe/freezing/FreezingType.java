package dev.celestiacraft.cmi.common.recipe.freezing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.VecHelper;
import dev.celestiacraft.cmi.api.CatalystUtils;
import dev.celestiacraft.cmi.common.register.CmiCreateRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class FreezingType implements FanProcessingType {
	private static final RecipeWrapper RECIPE_WRAPPER = new FreezingRecipe.Wrapper();

	@Override
	public boolean isValidAt(Level level, BlockPos pos) {
		BlockState block = level.getBlockState(pos);
		if (block.is(Blocks.POWDER_SNOW)) {
			Optional<Direction> facing = CatalystUtils.GetDirection(block);
			return facing.isPresent() && level.getBlockState(
					pos.relative(facing.get().getOpposite())).is(AllBlocks.ENCASED_FAN.get()
			);
		}
		return false;
	}

	@Override
	public int getPriority() {
		return 500;
	}

	@Override
	public boolean canProcess(ItemStack stack, Level level) {
		RECIPE_WRAPPER.setItem(0, stack);
		return CmiCreateRecipe.FREEZING.find(RECIPE_WRAPPER, level).isPresent();
	}

	@Override
	public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
		RECIPE_WRAPPER.setItem(0, stack);
		Optional<Recipe<RecipeWrapper>> recipe = CmiCreateRecipe.FREEZING.find(RECIPE_WRAPPER, level);
		return recipe.map((wrapperRecipe) -> {
			return RecipeApplier.applyRecipeOn(level, stack, wrapperRecipe);
		}).orElse(null);
	}

	@Override
	public void spawnProcessingParticles(Level level, Vec3 pos) {
		if (level.random.nextInt(4) != 0) {
			return;
		}
		pos = pos.add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1).multiply(1, 0.05f, 1).normalize().scale(0.15f));
		DustParticleOptions particleOptions = new DustParticleOptions(
				new Color(0xB180E5).asVectorF(),
				1
		);
		level.addParticle(
				particleOptions,
				pos.x + (level.random.nextFloat() - .5f) * .5f,
				pos.y + .5f, pos.z + (level.random.nextFloat() - .5f) * .5f,
				0,
				1 / 8f,
				0
		);
	}

	@Override
	public void morphAirFlow(AirFlowParticleAccess access, RandomSource source) {
		access.setColor(Color.mixColors(0xB180E5, 0x7353BA, source.nextFloat()));
		access.setAlpha(.5f);

		if (source.nextFloat() < 1 / 16f) {
			access.spawnExtraParticle(ParticleTypes.WITCH, .5f);
		}
	}

	@Override
	public void affectEntity(Entity entity, Level level) {
		if (level.isClientSide()) {
			return;
		}

		entity.hurt(level.damageSources().dragonBreath(), 4.0f);
	}
}