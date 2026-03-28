package dev.celestiacraft.cmi.compat.adastra;

import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class SpaceElevatorConstructionHandler {
	private static final ResourceLocation WRENCH_ID = ResourceLocation.fromNamespaceAndPath("ad_astra", "wrench");
	private static final ResourceLocation GLOWING_IRON_PILLAR_ID = ResourceLocation.fromNamespaceAndPath("ad_astra", "glowing_iron_pillar");
	private static final double MAX_USE_DISTANCE_SQR = 64.0D;
	private static final double EXISTING_ELEVATOR_RADIUS = 2.5D;

	@Nullable
	private static Item cachedWrench;
	@Nullable
	private static Block cachedAnchorBlock;

	private SpaceElevatorConstructionHandler() {
	}

	public static boolean isWrench(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		if (cachedWrench == null) {
			cachedWrench = ForgeRegistries.ITEMS.getValue(WRENCH_ID);
		}
		return cachedWrench != null ? stack.is(cachedWrench) : WRENCH_ID.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()));
	}

	public static boolean isAnchorBlock(Level level, BlockPos pos) {
		if (cachedAnchorBlock == null) {
			cachedAnchorBlock = ForgeRegistries.BLOCKS.getValue(GLOWING_IRON_PILLAR_ID);
		}
		return cachedAnchorBlock != null ? level.getBlockState(pos).is(cachedAnchorBlock) : GLOWING_IRON_PILLAR_ID.equals(ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock()));
	}

	public static boolean isWithinUseRange(Player player, BlockPos pos) {
		return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= MAX_USE_DISTANCE_SQR;
	}

	@Nullable
	public static SpaceElevatorConstructionRecipe getRecipe(Level level) {
		return SpaceElevatorConstructionRecipe.getRecipe(level, level.dimension());
	}

	public static int[] getStoredCounts(ServerLevel level, BlockPos anchorPos, int ingredientCount) {
		return SpaceElevatorMaterialStorage.getStoredCounts(level, anchorPos, ingredientCount);
	}

	public static boolean hasNearbyElevator(Level level, BlockPos anchorPos) {
		return getNearbyElevator(level, anchorPos) != null;
	}

	@Nullable
	public static SpaceElevatorEntity getNearbyElevator(Level level, BlockPos anchorPos) {
		Vec3 center = Vec3.atCenterOf(anchorPos);
		AABB bounds = AABB.ofSize(center, EXISTING_ELEVATOR_RADIUS * 2.0D, 8.0D, EXISTING_ELEVATOR_RADIUS * 2.0D);
		return level.getEntitiesOfClass(SpaceElevatorEntity.class, bounds, entity -> entity.isAlive() && entity.isAnchoredTo(anchorPos))
				.stream()
				.findFirst()
				.orElse(null);
	}

	public static ConstructResult tryConstruct(ServerPlayer player, BlockPos anchorPos) {
		ServerLevel level = player.serverLevel();
		if (!isWithinUseRange(player, anchorPos)) {
			return ConstructResult.TOO_FAR;
		}
		if (!isAnchorBlock(level, anchorPos)) {
			return ConstructResult.INVALID_ANCHOR;
		}

		SpaceElevatorConstructionRecipe recipe = getRecipe(level);
		if (recipe == null) {
			return ConstructResult.NO_RECIPE;
		}
		if (hasNearbyElevator(level, anchorPos)) {
			return ConstructResult.ALREADY_PRESENT;
		}
		if (!(player.isCreative() || player.isSpectator()) && !SpaceElevatorMaterialStorage.hasAllMaterials(level, anchorPos, recipe)) {
			return ConstructResult.NOT_ENOUGH_MATERIALS;
		}

		if (!spawnElevator(level, anchorPos)) {
			return ConstructResult.SPAWN_FAILED;
		}
		AdAstraSpaceElevatorTravelCompat.bindConstructedGroundAnchor(level, anchorPos);
		SpaceElevatorMaterialStorage.clear(level, anchorPos);
		playFeedback(level, anchorPos);
		return ConstructResult.SUCCESS;
	}

	private static boolean spawnElevator(ServerLevel level, BlockPos anchorPos) {
		SpaceElevatorEntity elevator = CmiEntity.SPACE_ELEVATOR.get().create(level);
		if (elevator == null) {
			return false;
		}

		elevator.setAnchor(anchorPos);
		elevator.moveTo(anchorPos.getX() + 0.5D, anchorPos.getY() + 1.01D, anchorPos.getZ() + 0.5D, 0.0F, 0.0F);
		level.addFreshEntity(elevator);
		return true;
	}

	private static void playFeedback(ServerLevel level, BlockPos anchorPos) {
		level.playSound(null, anchorPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.9F, 1.3F);
		level.sendParticles(ParticleTypes.END_ROD, anchorPos.getX() + 0.5D, anchorPos.getY() + 1.05D, anchorPos.getZ() + 0.5D, 24, 0.55D, 0.8D, 0.55D, 0.02D);
		level.sendParticles(ParticleTypes.ELECTRIC_SPARK, anchorPos.getX() + 0.5D, anchorPos.getY() + 1.15D, anchorPos.getZ() + 0.5D, 12, 0.35D, 0.35D, 0.35D, 0.01D);
	}

	public enum ConstructResult {
		SUCCESS("text.cmi.space_elevator.success"),
		ALREADY_PRESENT("text.cmi.space_elevator.already_present"),
		NOT_ENOUGH_MATERIALS("text.cmi.space_elevator.not_enough_materials"),
		INVALID_ANCHOR("text.cmi.space_elevator.invalid_anchor"),
		NO_RECIPE("text.cmi.space_elevator.no_recipe"),
		SPAWN_FAILED("text.cmi.space_elevator.spawn_failed"),
		TOO_FAR("text.cmi.space_elevator.too_far");

		private final String translationKey;

		ConstructResult(String translationKey) {
			this.translationKey = translationKey;
		}

		public String translationKey() {
			return translationKey;
		}
	}
}
