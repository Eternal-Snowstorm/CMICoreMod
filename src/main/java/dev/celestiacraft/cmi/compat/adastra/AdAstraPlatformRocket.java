package dev.celestiacraft.cmi.compat.adastra;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AdAstraPlatformRocket {
	private static final double DETECTION_HALF_WIDTH = 2.5D;
	private static final double DETECTION_HEIGHT = 8.0D;

	private AdAstraPlatformRocket() {
	}

	// 判定手持物品是否为 Ad Astra 火箭物品
	public static boolean isRocketItem(@NotNull ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return stack.getItem() instanceof RocketItem;
	}

	// 取平台顶面停放着的 Ad Astra 火箭，没有则返回空
	@Nullable
	public static Rocket findRocket(@NotNull Level level, @NotNull BlockPos platformPos) {
		AABB bounds = areaOf(platformPos);
		List<Rocket> rockets = level.getEntitiesOfClass(Rocket.class, bounds, Rocket::isAlive);
		if (rockets.isEmpty()) {
			return null;
		}
		return rockets.get(0);
	}

	// 按平台顶面中心生成 Ad Astra 火箭实体，失败时返回空
	@Nullable
	public static Rocket createRocket(@NotNull Level level, @NotNull ItemStack stack, @NotNull BlockPos platformPos, @NotNull Direction facing) {
		if (!(stack.getItem() instanceof RocketItem rocketItem)) {
			return null;
		}
		Entity entity = rocketItem.type().create(level);
		if (!(entity instanceof Rocket rocket)) {
			return null;
		}
		double surfaceY = SpaceElevatorConstructionHandler.platformSurfaceY(platformPos);
		rocket.setPos(platformPos.getX() + 0.5D, surfaceY, platformPos.getZ() + 0.5D);
		rocket.setYRot(facing.getOpposite().toYRot());
		return rocket;
	}

	private static AABB areaOf(@NotNull BlockPos platformPos) {
		double surfaceY = SpaceElevatorConstructionHandler.platformSurfaceY(platformPos);
		return new AABB(
				platformPos.getX() + 0.5D - DETECTION_HALF_WIDTH,
				surfaceY,
				platformPos.getZ() + 0.5D - DETECTION_HALF_WIDTH,
				platformPos.getX() + 0.5D + DETECTION_HALF_WIDTH,
				surfaceY + DETECTION_HEIGHT,
				platformPos.getZ() + 0.5D + DETECTION_HALF_WIDTH
		);
	}
}