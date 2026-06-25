package dev.celestiacraft.cmi.compat.adastra;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockEntity;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortShape;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.structure.SpaceElevatorBaseStructure;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.FluidIngredientEntry;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import dev.celestiacraft.cmi.common.register.block.SpaceElevatorBlocks;
import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class SpaceElevatorConstructionHandler {
	private static final ResourceLocation WRENCH_ID = ResourceLocation.fromNamespaceAndPath("ad_astra", "wrench");
	private static final double MAX_USE_DISTANCE_SQR = 64.0D;
	private static final double EXISTING_ELEVATOR_RADIUS = 4.0D;
	private static final double GROUND_BASE_LINK_RADIUS = 16.0D;
	private static final double LAUNCH_PAD_SURFACE_OFFSET = 2.0D;
	private static final double ROCKET_DETECTION_HALF_WIDTH = 2.5D;
	private static final double ROCKET_DETECTION_HEIGHT = 8.0D;

	@Nullable
	private static Item cachedWrench;

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
		return level.getBlockState(pos).is(SpaceElevatorBlocks.SPACE_ELEVATOR_BASE_CONSOLE.get());
	}

	@Nullable
	public static BlockPos resolveAnchorPos(Level level, BlockPos clickedPos) {
		BlockState state = level.getBlockState(clickedPos);
		if (!(state.getBlock() instanceof SpaceElevatorIoPortBlock)
				|| state.getValue(SpaceElevatorIoPortBlock.SHAPE) != IoPortShape.SCREEN_CENTER) {
			return null;
		}
		BlockPos controller = clickedPos.subtract(SpaceElevatorBaseStructure.CENTER_SCREEN_OFFSET);
		return isAnchorBlock(level, controller) ? controller : null;
	}

	@Nullable
	public static BlockPos resolveLaunchPadAnchor(Level level, BlockPos clickedPos) {
		BlockState state = level.getBlockState(clickedPos);
		if (!(state.getBlock() instanceof SpaceElevatorIoPortBlock)
				|| state.getValue(SpaceElevatorIoPortBlock.SHAPE) != IoPortShape.TOP_CENTER) {
			return null;
		}
		BlockPos controller = SpaceElevatorIoPortBlock.getControllerPos(level, clickedPos);
		return controller != null && isAnchorBlock(level, controller) ? controller : null;
	}

	public static boolean hasRocketOnBase(Level level, BlockPos anchorPos) {
		double surfaceY = anchorPos.getY() + LAUNCH_PAD_SURFACE_OFFSET;
		AABB bounds = new AABB(
				anchorPos.getX() + 0.5D - ROCKET_DETECTION_HALF_WIDTH,
				surfaceY,
				anchorPos.getZ() + 0.5D - ROCKET_DETECTION_HALF_WIDTH,
				anchorPos.getX() + 0.5D + ROCKET_DETECTION_HALF_WIDTH,
				surfaceY + ROCKET_DETECTION_HEIGHT,
				anchorPos.getZ() + 0.5D + ROCKET_DETECTION_HALF_WIDTH
		);
		return !level.getEntitiesOfClass(ProspectingRocketEntity.class, bounds, ProspectingRocketEntity::isAlive).isEmpty();
	}

	public static boolean isWithinUseRange(Player player, BlockPos pos) {
		return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= MAX_USE_DISTANCE_SQR;
	}

	@Nullable
	public static SpaceElevatorConstructionRecipe getRecipe(Level level) {
		return SpaceElevatorConstructionRecipe.getRecipe(level, level.dimension());
	}

	public static int[] getStoredCounts(ServerLevel level, BlockPos anchorPos, int ingredientCount) {
		SpaceElevatorBaseConsoleBlockEntity console = getConsole(level, anchorPos);
		SpaceElevatorConstructionRecipe recipe = getRecipe(level);
		if (console == null || recipe == null) {
			return new int[ingredientCount];
		}
		return countMaterials(console, recipe, ingredientCount);
	}

	public static int[] getStoredFluidAmounts(ServerLevel level, BlockPos anchorPos, int fluidIngredientCount) {
		SpaceElevatorBaseConsoleBlockEntity console = getConsole(level, anchorPos);
		SpaceElevatorConstructionRecipe recipe = getRecipe(level);
		if (console == null || recipe == null) {
			return new int[fluidIngredientCount];
		}
		return countFluids(console, recipe, fluidIngredientCount);
	}

	@Nullable
	private static SpaceElevatorBaseConsoleBlockEntity getConsole(Level level, BlockPos anchorPos) {
		BlockEntity be = level.getBlockEntity(anchorPos);
		return be instanceof SpaceElevatorBaseConsoleBlockEntity console ? console : null;
	}

	private static int[] countMaterials(SpaceElevatorBaseConsoleBlockEntity console, SpaceElevatorConstructionRecipe recipe, int ingredientCount) {
		int[] result = new int[ingredientCount];
		ItemStackHandler items = console.getInputItems();
		int slots = items.getSlots();
		int recipeSize = Math.min(ingredientCount, recipe.ingredients().size());
		for (int i = 0; i < recipeSize; i++) {
			SpaceElevatorConstructionRecipe.IngredientEntry entry = recipe.ingredients().get(i);
			int count = 0;
			for (int slot = 0; slot < slots; slot++) {
				ItemStack stack = items.getStackInSlot(slot);
				if (stack.isEmpty() || !entry.ingredient().test(stack)) {
					continue;
				}
				count += stack.getCount();
			}
			result[i] = count;
		}
		return result;
	}

	private static int[] countFluids(SpaceElevatorBaseConsoleBlockEntity console, SpaceElevatorConstructionRecipe recipe, int fluidIngredientCount) {
		int[] result = new int[fluidIngredientCount];
		FluidTank[] tanks = console.getInputFluids();
		int recipeSize = Math.min(fluidIngredientCount, recipe.fluidIngredients().size());
		for (int i = 0; i < recipeSize; i++) {
			FluidIngredientEntry entry = recipe.fluidIngredients().get(i);
			int amount = 0;
			for (FluidTank tank : tanks) {
				FluidStack stack = tank.getFluid();
				if (stack.isEmpty() || !entry.test(stack)) {
					continue;
				}
				amount += stack.getAmount();
			}
			result[i] = amount;
		}
		return result;
	}

	public static boolean hasAllMaterials(ServerLevel level, BlockPos anchorPos, SpaceElevatorConstructionRecipe recipe) {
		SpaceElevatorBaseConsoleBlockEntity console = getConsole(level, anchorPos);
		if (console == null) {
			return false;
		}
		int[] itemCounts = countMaterials(console, recipe, recipe.ingredients().size());
		for (int i = 0; i < recipe.ingredients().size(); i++) {
			if (itemCounts[i] < recipe.ingredients().get(i).count()) {
				return false;
			}
		}
		int[] fluidCounts = countFluids(console, recipe, recipe.fluidIngredients().size());
		for (int i = 0; i < recipe.fluidIngredients().size(); i++) {
			if (fluidCounts[i] < recipe.fluidIngredients().get(i).amount()) {
				return false;
			}
		}
		return true;
	}

	private static void consumeMaterials(SpaceElevatorBaseConsoleBlockEntity console, SpaceElevatorConstructionRecipe recipe) {
		ItemStackHandler items = console.getInputItems();
		for (SpaceElevatorConstructionRecipe.IngredientEntry entry : recipe.ingredients()) {
			int remaining = entry.count();
			for (int slot = 0; slot < items.getSlots() && remaining > 0; slot++) {
				ItemStack stack = items.getStackInSlot(slot);
				if (stack.isEmpty() || !entry.ingredient().test(stack)) {
					continue;
				}
				int take = Math.min(remaining, stack.getCount());
				items.extractItem(slot, take, false);
				remaining -= take;
			}
		}
		FluidTank[] tanks = console.getInputFluids();
		for (FluidIngredientEntry entry : recipe.fluidIngredients()) {
			int remaining = entry.amount();
			for (FluidTank tank : tanks) {
				if (remaining <= 0) {
					break;
				}
				FluidStack stack = tank.getFluid();
				if (stack.isEmpty() || !entry.test(stack)) {
					continue;
				}
				int take = Math.min(remaining, stack.getAmount());
				FluidStack drainTarget = new FluidStack(stack, take);
				FluidStack drained = tank.drain(drainTarget, IFluidHandler.FluidAction.EXECUTE);
				remaining -= drained.getAmount();
			}
		}
	}

	public static boolean hasNearbyElevator(Level level, BlockPos anchorPos) {
		return getNearbyElevator(level, anchorPos) != null;
	}

	public static boolean hasOrbitalCounterpart(ServerLevel groundLevel, BlockPos groundAnchor) {
		ServerLevel orbitLevel = groundLevel.getServer().getLevel(Planet.EARTH_ORBIT);
		if (orbitLevel == null) {
			return false;
		}
		SpaceElevatorLinkHandler.LinkTarget link = SpaceElevatorLinkHandler.findByGroundAnchor(
				orbitLevel, groundLevel.dimension(), groundAnchor);
		if (link == null || link.orbitAnchor() == null) {
			link = SpaceElevatorLinkHandler.findNearestByGroundBase(orbitLevel, groundLevel.dimension(), groundAnchor, GROUND_BASE_LINK_RADIUS);
			if (link == null || link.orbitAnchor() == null) {
				return false;
			}
			SpaceElevatorLinkHandler.setGroundAnchor(orbitLevel, link.stationPos(), groundAnchor);
		}
		if (SpaceElevatorLinkHandler.isElevatorPresent(orbitLevel, link.stationPos())) {
			return true;
		}
		BlockPos orbitAnchor = link.orbitAnchor();
		ChunkPos chunkPos = new ChunkPos(orbitAnchor);
		orbitLevel.getChunkSource().addRegionTicket(TicketType.PORTAL, chunkPos, 1, orbitAnchor);
		orbitLevel.getChunk(chunkPos.x, chunkPos.z);
		AABB bounds = AABB.ofSize(Vec3.atCenterOf(orbitAnchor), 8.0D, 16.0D, 8.0D);
		boolean found = !orbitLevel.getEntitiesOfClass(SpaceElevatorEntity.class, bounds,
				entity -> entity.isAlive() && entity.isAnchoredTo(orbitAnchor)).isEmpty();
		if (found) {
			SpaceElevatorLinkHandler.setElevatorPresent(orbitLevel, link.stationPos(), true);
		}
		return found;
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
		if (hasRocketOnBase(level, anchorPos)) {
			return ConstructResult.ROCKET_PRESENT;
		}
		if (hasOrbitalCounterpart(level, anchorPos)) {
			return ConstructResult.ALREADY_IN_ORBIT;
		}
		boolean creativeBypass = player.isCreative() || player.isSpectator();
		if (!creativeBypass && !hasAllMaterials(level, anchorPos, recipe)) {
			return ConstructResult.NOT_ENOUGH_MATERIALS;
		}

		if (!spawnElevator(level, anchorPos)) {
			return ConstructResult.SPAWN_FAILED;
		}
		AdAstraSpaceElevatorTravelCompat.bindConstructedGroundAnchor(level, anchorPos);
		SpaceElevatorLinkHandler.markElevatorPresent(level, anchorPos, true);
		if (!creativeBypass) {
			SpaceElevatorBaseConsoleBlockEntity console = getConsole(level, anchorPos);
			if (console != null) {
				consumeMaterials(console, recipe);
			}
		}
		playFeedback(level, anchorPos);
		return ConstructResult.SUCCESS;
	}

	private static boolean spawnElevator(ServerLevel level, BlockPos anchorPos) {
		SpaceElevatorEntity elevator = CmiEntity.SPACE_ELEVATOR.get().create(level);
		if (elevator == null) {
			return false;
		}

		elevator.setAnchor(anchorPos);
		elevator.setYRot(180.0F);
		elevator.setYBodyRot(180.0F);
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
		ROCKET_PRESENT("text.cmi.space_elevator.rocket_present"),
		ALREADY_IN_ORBIT("text.cmi.space_elevator.already_in_orbit"),
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
