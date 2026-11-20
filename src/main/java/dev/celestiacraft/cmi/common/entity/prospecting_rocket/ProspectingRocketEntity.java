package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.client.entity.ProspectingRocketClientHandler;
import dev.celestiacraft.cmi.client.gui.ProspectingRocketCargoUI;
import dev.celestiacraft.cmi.client.gui.ProspectingRocketUIFactory;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlock;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.GeneralAstronauticPlatformBlockEntity;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.PlatformBindingHandler;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.registry.ModDamageSources;
import earth.terrarium.adastra.common.registry.ModSoundEvents;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.UUID;

public class ProspectingRocketEntity extends Entity implements GeoEntity, IUIHolder {
	private static final String TAG_CARGO = "CargoItems";
	private static final String TAG_FUEL = "FuelTank";
	private static final String TAG_DESTINATION = "Destination";
	private static final String TAG_PHASE = "FlightPhase";
	private static final String TAG_SPEED = "FlightSpeed";
	private static final String TAG_COUNTDOWN = "LaunchCountdown";
	private static final EntityDataAccessor<Integer> DATA_FLIGHT_PHASE = SynchedEntityData.defineId(ProspectingRocketEntity.class, EntityDataSerializers.INT);
	private static final double ASCENT_ACCELERATION = 0.005D;
	private static final double MAX_FLIGHT_SPEED = 1.0D;
	private static final double DESCENT_BRAKE_DISTANCE = 100.0D;
	private static final double MIN_DESCENT_SPEED = 0.05D;
	private static final double NOTIFY_RADIUS = 32.0D;
	private static final int BURN_SECONDS = 10;
	private static final float BURN_DAMAGE = 10.0F;
	private static final int NORMAL_LAUNCH_FUEL = 3000;
	private static final int EFFICIENT_LAUNCH_FUEL = 1000;
	private static final int LAUNCH_COUNTDOWN_TICKS = 200;

	@Getter
	private final ProspectingRocketTier tier;
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	@Getter
	private final SimpleContainer cargoItems;
	@Getter
	private final FluidTank fuelTank;
	private LazyOptional<IItemHandler> cargoItemsCap = LazyOptional.empty();
	private LazyOptional<IFluidHandler> fuelTankCap = LazyOptional.empty();
	private RocketFlightPhase flightPhase = RocketFlightPhase.IDLE;
	private double flightSpeed;
	private boolean flightSoundStarted;
	private boolean landingVerified;
	private int countdownTicks;
	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private float lerpYRot;
	private float lerpXRot;
	@Nullable
	private GlobalPos destination;

	public ProspectingRocketEntity(EntityType<?> type, Level level, ProspectingRocketTier tier) {
		super(type, level);
		this.tier = tier;
		cargoItems = new SimpleContainer(tier.cargoSlots());
		fuelTank = new FluidTank(tier.fuelCapacity(), stack -> RocketFuel.isRocketFuel(stack, tier.level()));
		noCulling = true;
		rebuildCaps();
	}

	public boolean isFueled() {
		return fuelTank.getFluidAmount() >= fuelTank.getCapacity();
	}

	// 红石触发: 校验目标平台, 等级与燃料后进入点火倒计时
	public RocketLaunchResult launch(@NotNull GlobalPos target) {
		if (!(level() instanceof ServerLevel serverLevel)) {
			return RocketLaunchResult.NOT_SERVER;
		}
		if (isFlying()) {
			return RocketLaunchResult.BUSY;
		}
		if (!canReach(target)) {
			return RocketLaunchResult.TOO_LOW_TIER;
		}
		if (!hasTierFuel()) {
			return RocketLaunchResult.WRONG_FUEL;
		}
		RocketLaunchResult targetState = prepareTarget(target);
		if (targetState != RocketLaunchResult.SUCCESS) {
			return targetState;
		}
		if (!consumeLaunchFuel()) {
			releaseDestinationChunk(target);
			return RocketLaunchResult.NO_FUEL;
		}
		destination = target;
		countdownTicks = LAUNCH_COUNTDOWN_TICKS;
		flightSpeed = 0.0D;
		landingVerified = false;
		setPhase(RocketFlightPhase.COUNTDOWN);
		serverLevel.playSound(null, blockPosition(), ModSoundEvents.ROCKET_LAUNCH.get(), SoundSource.AMBIENT, 10.0F, 1.0F);
		return RocketLaunchResult.SUCCESS;
	}

	// 目标平台需存在且无停放火箭, 未加载时强制加载其所在区块
	private RocketLaunchResult prepareTarget(GlobalPos target) {
		if (level().getServer() == null) {
			return RocketLaunchResult.NOT_SERVER;
		}
		ServerLevel targetLevel = level().getServer().getLevel(target.dimension());
		if (targetLevel == null) {
			return RocketLaunchResult.NO_DIMENSION;
		}
		BlockPos targetPos = target.pos();
		loadTargetChunk(targetLevel, targetPos);
		if (!(targetLevel.getBlockState(targetPos).getBlock() instanceof GeneralAstronauticPlatformBlock)) {
			releaseTargetChunk(targetLevel, targetPos);
			return RocketLaunchResult.NO_PLATFORM;
		}
		if (SpaceElevatorConstructionHandler.hasRocketOnPlatform(targetLevel, targetPos)) {
			releaseTargetChunk(targetLevel, targetPos);
			return RocketLaunchResult.OCCUPIED;
		}
		return RocketLaunchResult.SUCCESS;
	}

	public boolean isFlying() {
		return flightPhase != RocketFlightPhase.IDLE;
	}

	public RocketFlightPhase getFlightPhase() {
		return flightPhase;
	}

	// 上升与下降段播放飞行循环音, 倒计时不播放
	public boolean isRocketSoundActive() {
		return flightPhase == RocketFlightPhase.ASCENDING || flightPhase == RocketFlightPhase.DESCENDING;
	}

	public boolean isFlightSoundStarted() {
		return flightSoundStarted;
	}

	public void setFlightSoundStarted(boolean started) {
		flightSoundStarted = started;
	}

	// 同步飞行阶段并关闭碰撞, 避免被平台与地形挡住
	private void setPhase(RocketFlightPhase phase) {
		flightPhase = phase;
		entityData.set(DATA_FLIGHT_PHASE, phase.ordinal());
		noPhysics = phase != RocketFlightPhase.IDLE;
	}

	@Override
	public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_FLIGHT_PHASE.equals(key)) {
			flightPhase = RocketFlightPhase.byOrdinal(entityData.get(DATA_FLIGHT_PHASE));
			noPhysics = flightPhase != RocketFlightPhase.IDLE;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			tickClientLerp();
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ProspectingRocketClientHandler.tickRocket(this));
			return;
		}
		tickFlight();
	}

	// 服务端位置包到达时缓动到目标位置, 使飞行画面平滑
	@Override
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
		if (teleport) {
			setPos(x, y, z);
			setRot(yRot, xRot);
			lerpSteps = 0;
			return;
		}
		lerpX = x;
		lerpY = y;
		lerpZ = z;
		lerpYRot = yRot;
		lerpXRot = xRot;
		lerpSteps = Math.max(1, steps);
	}

	private void tickClientLerp() {
		if (lerpSteps <= 0) {
			return;
		}
		double x = getX() + (lerpX - getX()) / lerpSteps;
		double y = getY() + (lerpY - getY()) / lerpSteps;
		double z = getZ() + (lerpZ - getZ()) / lerpSteps;
		float yRot = getYRot() + Mth.wrapDegrees(lerpYRot - getYRot()) / lerpSteps;
		float xRot = getXRot() + (lerpXRot - getXRot()) / lerpSteps;
		lerpSteps--;
		setPos(x, y, z);
		setRot(yRot, xRot);
	}

	// 服务端推进飞行阶段
	private void tickFlight() {
		switch (flightPhase) {
			case COUNTDOWN -> {
				tickCountdown();
			}
			case ASCENDING -> {
				tickAscending();
			}
			case DESCENDING -> {
				tickDescending();
			}
			default -> {
			}
		}
	}

	// 点火倒计时: 平台冒烟, 结束后进入上升段
	private void tickCountdown() {
		if (countdownTicks > 0) {
			countdownTicks--;
		}
		if (countdownTicks <= 0) {
			setPhase(RocketFlightPhase.ASCENDING);
		}
	}

	private void tickAscending() {
		burnEntitiesUnderRocket();
		if (flightSpeed < MAX_FLIGHT_SPEED) {
			flightSpeed = Math.min(MAX_FLIGHT_SPEED, flightSpeed + ASCENT_ACCELERATION);
		}
		setDeltaMovement(0.0D, flightSpeed, 0.0D);
		move(MoverType.SELF, getDeltaMovement());
		if (getY() >= AdAstraConfig.atmosphereLeave) {
			transferToDestination();
		}
	}

	// 越过传送高度后迁移到目标维度并转入下降段
	private void transferToDestination() {
		if (!(level() instanceof ServerLevel sourceLevel) || destination == null) {
			finishLanding();
			return;
		}
		ServerLevel targetLevel = sourceLevel.getServer().getLevel(destination.dimension());
		if (targetLevel == null) {
			abortFlight(RocketLaunchResult.NO_DIMENSION);
			return;
		}
		BlockPos targetPos = destination.pos();
		loadTargetChunk(targetLevel, targetPos);
		Entity created = getType().create(targetLevel);
		if (!(created instanceof ProspectingRocketEntity transferred)) {
			abortFlight(RocketLaunchResult.NO_DIMENSION);
			return;
		}
		CompoundTag tag = saveWithoutId(new CompoundTag());
		tag.putString(TAG_PHASE, RocketFlightPhase.DESCENDING.name());
		tag.putDouble(TAG_SPEED, MAX_FLIGHT_SPEED);
		transferred.load(tag);
		// 源实体仍在世界中, 沿用其 UUID 会因重复而被实体存储拒收, 必须换新
		transferred.setUUID(UUID.randomUUID());
		// 与 Ad Astra 返回舱生成高度一致, 自传送高度开始下降
		transferred.setPos(targetPos.getX() + 0.5D, AdAstraConfig.atmosphereLeave, targetPos.getZ() + 0.5D);
		transferred.setYRot(getYRot());
		transferred.setPhase(RocketFlightPhase.DESCENDING);
		if (!targetLevel.addFreshEntity(transferred)) {
			transferred.discard();
			setPos(targetPos.getX() + 0.5D, AdAstraConfig.atmosphereLeave, targetPos.getZ() + 0.5D);
			abortFlight(RocketLaunchResult.SPAWN_FAILED);
			return;
		}
		discard();
	}

	// 目标区块需加载到实体 tick 等级, 否则下降中的火箭不会移动
	private static void loadTargetChunk(ServerLevel level, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		level.getChunkSource().addRegionTicket(TicketType.PORTAL, chunkPos, 2, pos);
		level.getChunk(chunkPos.x, chunkPos.z);
	}

	// 释放强制加载票据, 落地或放弃飞行后调用
	private static void releaseTargetChunk(ServerLevel level, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		level.getChunkSource().removeRegionTicket(TicketType.PORTAL, chunkPos, 2, pos);
	}

	private void releaseDestinationChunk(@Nullable GlobalPos target) {
		if (target == null || level().getServer() == null) {
			return;
		}
		ServerLevel targetLevel = level().getServer().getLevel(target.dimension());
		if (targetLevel == null) {
			return;
		}
		releaseTargetChunk(targetLevel, target.pos());
	}

	private void tickDescending() {
		if (!landingVerified) {
			landingVerified = true;
			if (!verifyLandingTarget()) {
				return;
			}
		}
		burnEntitiesUnderRocket();
		double targetY = getLandingY();
		double remaining = getY() - targetY;
		if (remaining <= 0.0D) {
			finishLanding();
			return;
		}
		double speed = descentSpeed(remaining);
		if (remaining <= speed) {
			finishLanding();
			return;
		}
		setDeltaMovement(0.0D, -speed, 0.0D);
		move(MoverType.SELF, getDeltaMovement());
	}

	private static double descentSpeed(double remaining) {
		if (remaining >= DESCENT_BRAKE_DISTANCE) {
			return MAX_FLIGHT_SPEED;
		}
		double speed = MAX_FLIGHT_SPEED * (remaining / DESCENT_BRAKE_DISTANCE);
		return Math.max(MIN_DESCENT_SPEED, speed);
	}

	private double getLandingY() {
		if (destination == null) {
			return getY();
		}
		return SpaceElevatorConstructionHandler.platformSurfaceY(destination.pos());
	}

	// 对齐平台顶面上一格, 启动平台存储代理后回到待机
	private void finishLanding() {
		setDeltaMovement(Vec3.ZERO);
		setPhase(RocketFlightPhase.IDLE);
		flightSpeed = 0.0D;
		countdownTicks = 0;
		landingVerified = false;
		releaseDestinationChunk(destination);
		if (destination != null) {
			BlockPos targetPos = destination.pos();
			setPos(targetPos.getX() + 0.5D, getLandingY(), targetPos.getZ() + 0.5D);
		}
		startStorageProxy();
		destination = null;
	}

	// 降落前确认目标平台仍在, 被破坏时仅提示并照常着陆
	private boolean verifyLandingTarget() {
		if (destination == null || !(level() instanceof ServerLevel serverLevel)) {
			abortFlight(RocketLaunchResult.NO_DIMENSION);
			return false;
		}
		BlockPos targetPos = destination.pos();
		if (!(serverLevel.getBlockState(targetPos).getBlock() instanceof GeneralAstronauticPlatformBlock)) {
			notifyNearbyPlayers(RocketLaunchResult.NO_PLATFORM);
			return true;
		}
		ProspectingRocketEntity occupant = SpaceElevatorConstructionHandler.findRocketOnPlatform(serverLevel, targetPos);
		if (occupant != null && occupant != this) {
			abortFlight(RocketLaunchResult.OCCUPIED);
			return false;
		}
		return true;
	}

	// 放弃本次飞行, 货物散落到当前位置
	private void abortFlight(RocketLaunchResult reason) {
		notifyNearbyPlayers(reason);
		releaseDestinationChunk(destination);
		dropCargo();
		setPhase(RocketFlightPhase.IDLE);
		flightSpeed = 0.0D;
		countdownTicks = 0;
		landingVerified = false;
		destination = null;
		discard();
	}

	// 向火箭附近玩家发送失败原因
	private void notifyNearbyPlayers(RocketLaunchResult reason) {
		if (!reason.shouldNotify()) {
			return;
		}
		Component message = CmiLang.textDirect(reason.messageKey());
		for (Player player : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(NOTIFY_RADIUS))) {
			player.displayClientMessage(message, true);
		}
	}

	// 停靠后激活目标平台存储代理, 货物经由平台端口访问
	private void startStorageProxy() {
		GeneralAstronauticPlatformBlockEntity platform = resolvePlatform(level(), destination);
		if (platform == null) {
			return;
		}
		platform.refreshStorageProxy();
		platform.beginLandingLoad();
	}

	@Nullable
	private static GeneralAstronauticPlatformBlockEntity resolvePlatform(Level level, @Nullable GlobalPos pos) {
		if (pos == null || !(level instanceof ServerLevel serverLevel)) {
			return null;
		}
		if (!serverLevel.dimension().equals(pos.dimension())) {
			return null;
		}
		BlockPos platformPos = pos.pos();
		if (!serverLevel.isLoaded(platformPos)) {
			return null;
		}
		if (!(serverLevel.getBlockEntity(platformPos) instanceof GeneralAstronauticPlatformBlockEntity platform)) {
			return null;
		}
		return platform;
	}

	// 与 Ad Astra 客运火箭一致: 灼烧箭体下方实体
	private void burnEntitiesUnderRocket() {
		AABB area = getBoundingBox().inflate(2.0D, 30.0D, 2.0D).move(0.0D, -37.0D, 0.0D);
		for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area)) {
			if (this.equals(target) || this.equals(target.getVehicle())) {
				continue;
			}
			target.setSecondsOnFire(BURN_SECONDS);
			target.hurt(ModDamageSources.create(level(), ModDamageSources.ROCKET_FLAMES), BURN_DAMAGE);
		}
	}

	// 罐内燃料必须属于自身等级
	private boolean hasTierFuel() {
		return RocketFuel.isRocketFuel(fuelTank.getFluid(), tier.level());
	}

	// 燃料达到 3000 mB 普通或 1000 mB 高效才可发射
	private boolean consumeLaunchFuel() {
		FluidStack fuel = fuelTank.getFluid();
		if (fuel.isEmpty()) {
			return false;
		}
		int required = RocketFuel.isEfficientFuel(fuel.getFluid()) ? EFFICIENT_LAUNCH_FUEL : NORMAL_LAUNCH_FUEL;
		if (fuel.getAmount() < required) {
			return false;
		}
		fuelTank.drain(required, IFluidHandler.FluidAction.EXECUTE);
		return true;
	}

	// 跨维度时按 Ad Astra 等级规则判断能否到达
	private boolean canReach(GlobalPos target) {
		if (target.dimension().equals(level().dimension())) {
			return true;
		}
		Planet planet = PlanetApi.API.getPlanet(target.dimension());
		if (planet == null) {
			return true;
		}
		return tier.level() >= planet.tier();
	}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}
		if (level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			ProspectingRocketUIFactory.INSTANCE.openUI(this, serverPlayer);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
		registrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
		return PlayState.STOP;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(DATA_FLIGHT_PHASE, RocketFlightPhase.IDLE.ordinal());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
		cargoItems.fromTag(tag.getList(TAG_CARGO, Tag.TAG_COMPOUND));
		fuelTank.readFromNBT(tag.getCompound(TAG_FUEL));
		destination = tag.contains(TAG_DESTINATION, Tag.TAG_COMPOUND) ? PlatformBindingHandler.read(tag, TAG_DESTINATION) : null;
		flightPhase = parsePhase(tag.getString(TAG_PHASE));
		flightSpeed = tag.getDouble(TAG_SPEED);
		countdownTicks = tag.getInt(TAG_COUNTDOWN);
		setPhase(flightPhase);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
		tag.put(TAG_CARGO, cargoItems.createTag());
		tag.put(TAG_FUEL, fuelTank.writeToNBT(new CompoundTag()));
		if (destination != null) {
			PlatformBindingHandler.write(tag, TAG_DESTINATION, destination);
		}
		tag.putString(TAG_PHASE, flightPhase.name());
		tag.putDouble(TAG_SPEED, flightSpeed);
		tag.putInt(TAG_COUNTDOWN, countdownTicks);
	}

	private static RocketFlightPhase parsePhase(String name) {
		for (RocketFlightPhase value : RocketFlightPhase.values()) {
			if (value.name().equals(name)) {
				return value;
			}
		}
		return RocketFlightPhase.IDLE;
	}

	private void rebuildCaps() {
		cargoItemsCap = LazyOptional.of(() -> new InvWrapper(cargoItems));
		fuelTankCap = LazyOptional.of(() -> fuelTank);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return cargoItemsCap.cast();
		}
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return fuelTankCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		cargoItemsCap.invalidate();
		fuelTankCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		rebuildCaps();
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount) {
		if (level().isClientSide()) {
			return false;
		}
		if (source.getEntity() instanceof Player player && player.isCreative()) {
			releaseDestinationChunk(destination);
			discard();
			return true;
		}
		return false;
	}

	@Override
	public ModularUI createUI(Player entityPlayer) {
		return ProspectingRocketCargoUI.create(this, entityPlayer);
	}

	@Override
	public boolean isInvalid() {
		return isRemoved();
	}

	@Override
	public boolean isRemote() {
		return level().isClientSide();
	}

	@Override
	public void markAsDirty() {
	}

	public void dropCargo() {
		if (level().isClientSide()) {
			return;
		}
		for (int i = 0; i < cargoItems.getContainerSize(); i++) {
			ItemStack stack = cargoItems.getItem(i);
			if (!stack.isEmpty()) {
				spawnAtLocation(stack);
			}
		}
		cargoItems.clearContent();
	}

	public boolean hasFuel() {
		return !fuelTank.isEmpty();
	}

	public FluidStack getFuel() {
		return fuelTank.getFluid();
	}
}