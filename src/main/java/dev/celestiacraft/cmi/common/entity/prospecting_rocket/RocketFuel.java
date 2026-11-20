package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

import earth.terrarium.adastra.common.tags.ModFluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class RocketFuel {
	// Ad Astra 四个等级的火箭燃料标签, 整合包扩充标签即可注册自定义燃料
	private static final List<TagKey<Fluid>> ROCKET_FUELS = List.of(
			ModFluidTags.TIER_1_ROCKET_FUEL,
			ModFluidTags.TIER_2_ROCKET_FUEL,
			ModFluidTags.TIER_3_ROCKET_FUEL,
			ModFluidTags.TIER_4_ROCKET_FUEL
	);

	private RocketFuel() {
	}

	// 火箭只使用自身等级的燃料, 等级越界时按第一级处理
	public static boolean isRocketFuel(@NotNull FluidStack stack, int rocketTier) {
		if (stack.isEmpty()) {
			return false;
		}
		return isRocketFuel(stack.getFluid(), rocketTier);
	}

	public static boolean isRocketFuel(@NotNull Fluid fluid, int rocketTier) {
		return fluid.is(tierTag(rocketTier));
	}

	// 高效燃料按一千 mB 折算
	public static boolean isEfficientFuel(@NotNull Fluid fluid) {
		return fluid.is(ModFluidTags.EFFICIENT_FUEL);
	}

	private static TagKey<Fluid> tierTag(int rocketTier) {
		if (rocketTier < 1 || rocketTier > ROCKET_FUELS.size()) {
			return ROCKET_FUELS.get(0);
		}
		return ROCKET_FUELS.get(rocketTier - 1);
	}
}