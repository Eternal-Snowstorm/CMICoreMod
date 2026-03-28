package dev.celestiacraft.cmi.common.entity.space_elevator;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import dev.celestiacraft.cmi.compat.adastra.AdAstraSpaceElevatorTravelCompat;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.c2s.StartSpaceElevatorTransportPacket;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.adastra.common.registry.ModSoundEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT)
public class SpaceElevatorEntity extends Entity implements GeoEntity {
	private static final EntityDataAccessor<BlockPos> ANCHOR_POS = SynchedEntityData.defineId(SpaceElevatorEntity.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Boolean> HAS_ANCHOR = SynchedEntityData.defineId(SpaceElevatorEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TRANSPORT_STATE = SynchedEntityData.defineId(SpaceElevatorEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TRANSPORT_TICKS = SynchedEntityData.defineId(SpaceElevatorEntity.class, EntityDataSerializers.INT);

	private static final int STATE_IDLE = 0;
	private static final int STATE_COUNTDOWN_UP = 1;
	private static final int STATE_DEPART_UP = 2;
	private static final int STATE_ARRIVE_ORBIT = 3;
	private static final int STATE_COUNTDOWN_DOWN = 4;
	private static final int STATE_DEPART_DOWN = 5;
	private static final int STATE_ARRIVE_GROUND = 6;

	private static final double ANCHOR_Y_OFFSET = 1.01D;
	private static final double ORBIT_DOCK_Y_OFFSET = -1.35D;
	private static final int COUNTDOWN_TICKS = Rocket.COUNTDOWN_LENGTH;
	private static final double GROUND_ASCENT_DISTANCE = 192.0D;
	private static final int GROUND_ASCENT_TICKS = 180;
	private static final double ORBIT_APPROACH_DISTANCE = 96.0D;
	private static final int ORBIT_APPROACH_TICKS = 120;
	private static final double ORBIT_DESCENT_DISTANCE = 168.0D;
	private static final int ORBIT_DESCENT_TICKS = 160;
	private static final double GROUND_APPROACH_DISTANCE = 104.0D;
	private static final int GROUND_APPROACH_TICKS = 124;
	private static final double SEARCH_RADIUS = 2.5D;
	private static final double SEARCH_HEIGHT = 12.0D;
	private static final double CONFLICT_SEARCH_RADIUS = 3.5D;
	private static final double CONFLICT_SEARCH_HEIGHT = 256.0D;
	private static final double CABLE_TOP_Y = 320.0D;
	private static final double CABLE_BOTTOM_Y = -64.0D;
	private static final Vec3[] CABLE_OFFSETS = new Vec3[] {
			new Vec3(-20.0D / 16.0D, 20.0D / 16.0D, -20.0D / 16.0D),
			new Vec3(-20.0D / 16.0D, 20.0D / 16.0D, 20.0D / 16.0D),
			new Vec3(20.0D / 16.0D, 20.0D / 16.0D, 20.0D / 16.0D),
			new Vec3(20.0D / 16.0D, 20.0D / 16.0D, -20.0D / 16.0D)
	};

	private static CameraType previousCameraType;
	private static boolean jumpWasDown;

	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private boolean startedTravelSound;
	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYRot;
	private double lerpXRot;

	@Nullable
	private ResourceKey<Level> pendingDestinationDimension;
	@Nullable
	private BlockPos pendingDestinationAnchor;

	public SpaceElevatorEntity(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.noCulling = true;
	}

	@SubscribeEvent
	public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if (event.getEntity().getVehicle() instanceof SpaceElevatorEntity) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			jumpWasDown = false;
			restoreCamera(mc);
			return;
		}

		Entity vehicle = mc.player.getVehicle();
		if (vehicle instanceof SpaceElevatorEntity elevator) {
			if (previousCameraType == null) {
				previousCameraType = mc.options.getCameraType();
				mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}

			boolean jumpDown = mc.options.keyJump.isDown();
			if (jumpDown && !jumpWasDown) {
				CmiNetwork.CHANNEL.sendToServer(new StartSpaceElevatorTransportPacket(elevator.getId()));
			}
			jumpWasDown = jumpDown;
			return;
		}

		jumpWasDown = false;
		restoreCamera(mc);
	}

	private static void restoreCamera(Minecraft mc) {
		if (previousCameraType != null) {
			mc.options.setCameraType(previousCameraType);
			previousCameraType = null;
		}
	}

	public void setAnchor(BlockPos anchorPos) {
		setAnchor(anchorPos, true);
	}

	private void setAnchor(BlockPos anchorPos, boolean snapNow) {
		this.entityData.set(ANCHOR_POS, anchorPos.immutable());
		this.entityData.set(HAS_ANCHOR, true);
		if (snapNow) {
			snapToAnchor();
		}
	}

	public boolean hasAnchor() {
		return this.entityData.get(HAS_ANCHOR);
	}

	public BlockPos getAnchor() {
		return this.entityData.get(ANCHOR_POS);
	}

	public boolean isAnchoredTo(BlockPos pos) {
		return hasAnchor() && getAnchor().equals(pos);
	}

	public boolean requestLaunch(ServerPlayer player) {
		if (!hasAnchor() || isTransporting() || getFirstPassenger() != player || !(level() instanceof ServerLevel serverLevel)) {
			return false;
		}

		AdAstraSpaceElevatorTravelCompat.TravelTarget target = isOrbitSide()
				? AdAstraSpaceElevatorTravelCompat.resolveGroundTarget(serverLevel, getAnchor())
				: AdAstraSpaceElevatorTravelCompat.resolveEarthOrbitTarget(serverLevel, getAnchor());
		if (target == null) {
			player.displayClientMessage(Component.translatable("text.cmi.space_elevator.no_link"), false);
			return false;
		}

		SpaceElevatorEntity counterpart = findOrCreateElevator(target.level(), target.targetAnchor());
		if (counterpart == null) {
			player.displayClientMessage(Component.translatable("text.cmi.space_elevator.spawn_failed"), false);
			return false;
		}
		if (counterpart.isTransporting() || counterpart.getFirstPassenger() != null) {
			player.displayClientMessage(Component.translatable("text.cmi.space_elevator.spawn_failed"), false);
			return false;
		}

		this.pendingDestinationDimension = target.level().dimension();
		this.pendingDestinationAnchor = target.targetAnchor().immutable();
		beginState(isOrbitSide() ? STATE_COUNTDOWN_DOWN : STATE_COUNTDOWN_UP);
		return true;
	}

	@Override
	public void tick() {
		super.tick();

		if (!level().isClientSide() && hasAnchor() && !isTransporting() && level().getBlockState(getAnchor()).isAir()) {
			discard();
			return;
		}

		if (!level().isClientSide() && hasAnchor() && tickCount % 10 == 0) {
			resolveAnchorConflict();
			if (!isAlive()) {
				return;
			}
		}

		if (isTransporting()) {
			tickTransport(level().isClientSide());
		} else if (hasAnchor()) {
			setDeltaMovement(Vec3.ZERO);
			snapToAnchor();
		}

		if (level().isClientSide()) {
			tickClientLerp();
			spawnCableParticlesClient();
			tickTravelSoundClient();
		}
	}

	private void tickClientLerp() {
		if (isControlledByLocalInstance()) {
			lerpSteps = 0;
			syncPacketPositionCodec(getX(), getY(), getZ());
		}
		if (lerpSteps <= 0) {
			return;
		}

		double x = getX() + (lerpX - getX()) / (double) lerpSteps;
		double y = getY() + (lerpY - getY()) / (double) lerpSteps;
		double z = getZ() + (lerpZ - getZ()) / (double) lerpSteps;
		float yRot = getYRot() + (float) Mth.wrapDegrees(lerpYRot - getYRot()) / (float) lerpSteps;
		float xRot = getXRot() + (float) (lerpXRot - getXRot()) / (float) lerpSteps;
		lerpSteps--;
		setPos(x, y, z);
		setRot(yRot, xRot);
	}

	@Override
	public boolean isControlledByLocalInstance() {
		return getControllingPassenger() instanceof Player player && player.isLocalPlayer();
	}

	private void tickTransport(boolean clientSide) {
		switch (getTransportState()) {
			case STATE_COUNTDOWN_UP -> tickCountdown(clientSide, STATE_DEPART_UP);
			case STATE_COUNTDOWN_DOWN -> tickCountdown(clientSide, STATE_DEPART_DOWN);
			case STATE_DEPART_UP -> tickDeparture(clientSide, getDockY(), getDockY() + GROUND_ASCENT_DISTANCE, GROUND_ASCENT_TICKS);
			case STATE_ARRIVE_ORBIT -> tickArrival(clientSide, getDockY() - ORBIT_APPROACH_DISTANCE, getDockY(), ORBIT_APPROACH_TICKS);
			case STATE_DEPART_DOWN -> tickDeparture(clientSide, getDockY(), getDockY() - ORBIT_DESCENT_DISTANCE, ORBIT_DESCENT_TICKS);
			case STATE_ARRIVE_GROUND -> tickArrival(clientSide, getDockY() + GROUND_APPROACH_DISTANCE, getDockY(), GROUND_APPROACH_TICKS);
			default -> {
			}
		}
	}

	private void tickCountdown(boolean clientSide, int nextState) {
		if (!clientSide && getFirstPassenger() == null) {
			stopLaunchAudio((ServerLevel) level());
			resetToAnchor();
			return;
		}

		snapToAnchor();
		if (!clientSide && getTransportTicks() == 0) {
			level().playSound(null, blockPosition(), ModSoundEvents.ROCKET_LAUNCH.get(), SoundSource.AMBIENT, 10.0F, 1.0F);
		}

		if (!clientSide) {
			setTransportTicks(getTransportTicks() + 1);
		}
		if (!clientSide && getTransportTicks() >= COUNTDOWN_TICKS) {
			beginState(nextState);
		}
	}

	private void tickDeparture(boolean clientSide, double startY, double targetY, int duration) {
		if (!clientSide && getFirstPassenger() == null) {
			resetToAnchor();
			return;
		}

		moveAlongPath(startY, targetY, duration);
		if (clientSide) {
			return;
		}
		setTransportTicks(getTransportTicks() + 1);
		if (getTransportTicks() < duration) {
			return;
		}

		if (getTransportState() == STATE_DEPART_UP) {
			transferPassengerToOrbit();
		} else {
			transferPassengerToGround();
		}
	}

	private void tickArrival(boolean clientSide, double startY, double targetY, int duration) {
		if (!clientSide && getFirstPassenger() == null) {
			resetToAnchor();
			return;
		}

		moveAlongPath(startY, targetY, duration);
		if (clientSide) {
			return;
		}
		setTransportTicks(getTransportTicks() + 1);
		if (getTransportTicks() >= duration) {
			finishArrival();
		}
	}

	private void moveAlongPath(double startY, double targetY, int duration) {
		double deltaY = (targetY - startY) / (double) duration;
		setDeltaMovement(0.0D, deltaY, 0.0D);
		move(MoverType.SELF, getDeltaMovement());
		if (getTransportTicks() + 1 >= duration) {
			setDeltaMovement(Vec3.ZERO);
			setPos(getDockX(), targetY, getDockZ());
		}
	}

	private void resolveAnchorConflict() {
		if (!(level() instanceof ServerLevel serverLevel) || !hasAnchor()) {
			return;
		}

		Vec3 center = Vec3.atCenterOf(getAnchor());
		AABB bounds = AABB.ofSize(center, CONFLICT_SEARCH_RADIUS * 2.0D, CONFLICT_SEARCH_HEIGHT, CONFLICT_SEARCH_RADIUS * 2.0D);
		List<SpaceElevatorEntity> elevators = serverLevel.getEntitiesOfClass(SpaceElevatorEntity.class, bounds, entity -> entity.isAlive() && entity.isAnchoredTo(getAnchor()));
		if (elevators.size() <= 1) {
			return;
		}

		SpaceElevatorEntity keep = elevators.get(0);
		for (SpaceElevatorEntity elevator : elevators) {
			if (shouldReplaceKeeper(keep, elevator)) {
				keep = elevator;
			}
		}

		for (SpaceElevatorEntity elevator : elevators) {
			if (elevator == keep) {
				continue;
			}
			ServerPlayer passenger = elevator.getPassengerPlayer();
			if (passenger != null) {
				passenger.stopRiding();
			}
			elevator.discard();
		}

		if (keep != this) {
			discard();
		}
	}

	private static boolean shouldReplaceKeeper(SpaceElevatorEntity current, SpaceElevatorEntity candidate) {
		if (candidate.isTransporting() != current.isTransporting()) {
			return candidate.isTransporting();
		}
		if ((candidate.getFirstPassenger() != null) != (current.getFirstPassenger() != null)) {
			return candidate.getFirstPassenger() != null;
		}
		return candidate.getId() < current.getId();
	}

	private void transferPassengerToOrbit() {
		ServerPlayer player = getPassengerPlayer();
		if (player == null || pendingDestinationDimension == null || pendingDestinationAnchor == null) {
			resetToAnchor();
			return;
		}

		ServerLevel targetLevel = player.server.getLevel(pendingDestinationDimension);
		if (targetLevel == null) {
			player.displayClientMessage(Component.translatable("text.cmi.space_elevator.no_link"), false);
			resetToAnchor();
			return;
		}

		SpaceElevatorEntity counterpart = findOrCreateElevator(targetLevel, pendingDestinationAnchor);
		if (counterpart == null) {
			player.displayClientMessage(Component.translatable("text.cmi.space_elevator.spawn_failed"), false);
			resetToAnchor();
			return;
		}

		counterpart.beginState(STATE_ARRIVE_ORBIT);
		movePassengerToCounterpart(player, targetLevel, counterpart);
		discard();
	}

	private void transferPassengerToGround() {
		ServerPlayer player = getPassengerPlayer();
		if (player == null || pendingDestinationDimension == null || pendingDestinationAnchor == null) {
			resetToAnchor();
			return;
		}

		ServerLevel targetLevel = player.server.getLevel(pendingDestinationDimension);
		if (targetLevel == null) {
			player.displayClientMessage(Component.translatable("text.cmi.space_elevator.no_link"), false);
			resetToAnchor();
			return;
		}

		SpaceElevatorEntity counterpart = findOrCreateElevator(targetLevel, pendingDestinationAnchor);
		if (counterpart == null) {
			player.displayClientMessage(Component.translatable("text.cmi.space_elevator.spawn_failed"), false);
			resetToAnchor();
			return;
		}

		counterpart.beginState(STATE_ARRIVE_GROUND);
		movePassengerToCounterpart(player, targetLevel, counterpart);
		discard();
	}

	private void movePassengerToCounterpart(ServerPlayer player, ServerLevel targetLevel, SpaceElevatorEntity counterpart) {
		player.stopRiding();
		player.teleportTo(targetLevel, counterpart.getX(), counterpart.getY() - 0.5D, counterpart.getZ(), player.getYRot(), player.getXRot());
		player.startRiding(counterpart, true);
	}

	private void finishArrival() {
		snapToAnchor();
		ServerPlayer player = getPassengerPlayer();
		if (player != null) {
			Vec3 exitPos = findExitPosition();
			player.stopRiding();
			player.teleportTo((ServerLevel) level(), exitPos.x, exitPos.y, exitPos.z, player.getYRot(), player.getXRot());
		}
		clearTransport();
	}

	private Vec3 findExitPosition() {
		if (!hasAnchor()) {
			return new Vec3(getX(), getY() + 0.15D, getZ());
		}
		if (isOrbitSide()) {
			return new Vec3(getAnchor().getX() + 0.5D, getAnchor().getY() + 1.02D, getAnchor().getZ() + 0.5D);
		}

		List<BlockPos> candidates = new ArrayList<>();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			candidates.add(getAnchor().relative(direction));
		}
		candidates.add(getAnchor());

		for (BlockPos floorPos : candidates) {
			if (hasStandingRoom(floorPos)) {
				return new Vec3(floorPos.getX() + 0.5D, floorPos.getY() + 1.02D, floorPos.getZ() + 0.5D);
			}
		}
		return new Vec3(getX(), getY() + 0.15D, getZ());
	}

	private boolean hasStandingRoom(BlockPos floorPos) {
		BlockState floorState = level().getBlockState(floorPos);
		if (floorState.getCollisionShape(level(), floorPos).isEmpty()) {
			return false;
		}

		BlockPos bodyPos = floorPos.above();
		BlockPos headPos = floorPos.above(2);
		return level().getBlockState(bodyPos).getCollisionShape(level(), bodyPos).isEmpty()
				&& level().getBlockState(headPos).getCollisionShape(level(), headPos).isEmpty();
	}

	private void spawnCableParticlesClient() {
		int state = getTransportState();
		if (state == STATE_IDLE || tickCount % 2 != 0) {
			return;
		}

		Level level = level();
		for (Vec3 offset : CABLE_OFFSETS) {
			level.addParticle(
					ParticleTypes.ELECTRIC_SPARK,
					getX() + offset.x,
					getY() + offset.y,
					getZ() + offset.z,
					Mth.nextDouble(level.random, -0.02D, 0.02D),
					Mth.nextDouble(level.random, -0.01D, 0.03D),
					Mth.nextDouble(level.random, -0.02D, 0.02D)
			);
		}
	}

	private void tickTravelSoundClient() {
		if (!isFlightSoundActive()) {
			startedTravelSound = false;
			return;
		}
		if (startedTravelSound) {
			return;
		}
		Minecraft.getInstance().getSoundManager().play(new SpaceElevatorTravelSoundInstance(this));
		startedTravelSound = true;
	}

	boolean isFlightSoundActive() {
		int state = getTransportState();
		return state == STATE_DEPART_UP || state == STATE_ARRIVE_ORBIT || state == STATE_DEPART_DOWN || state == STATE_ARRIVE_GROUND;
	}

	private void stopLaunchAudio(ServerLevel level) {
		ClientboundStopSoundPacket launchStop = new ClientboundStopSoundPacket(BuiltInRegistries.SOUND_EVENT.getKey(ModSoundEvents.ROCKET_LAUNCH.get()), SoundSource.AMBIENT);
		ClientboundStopSoundPacket travelStop = new ClientboundStopSoundPacket(BuiltInRegistries.SOUND_EVENT.getKey(ModSoundEvents.ROCKET.get()), SoundSource.AMBIENT);
		for (ServerPlayer serverPlayer : level.players()) {
			if (serverPlayer.distanceToSqr(this) > 256.0D * 256.0D) {
				continue;
			}
			serverPlayer.connection.send(launchStop);
			serverPlayer.connection.send(travelStop);
		}
	}

	private void beginState(int state) {
		this.entityData.set(TRANSPORT_STATE, state);
		setTransportTicks(0);
		applyStateStartPosition(state);
	}

	private void applyStateStartPosition(int state) {
		if (!hasAnchor()) {
			return;
		}
		switch (state) {
			case STATE_COUNTDOWN_UP, STATE_COUNTDOWN_DOWN, STATE_IDLE -> snapToAnchor();
			case STATE_DEPART_UP, STATE_DEPART_DOWN -> setPos(getDockX(), getDockY(), getDockZ());
			case STATE_ARRIVE_ORBIT -> setPos(getDockX(), getDockY() - ORBIT_APPROACH_DISTANCE, getDockZ());
			case STATE_ARRIVE_GROUND -> setPos(getDockX(), getDockY() + GROUND_APPROACH_DISTANCE, getDockZ());
			default -> {
			}
		}
	}

	private void clearTransport() {
		this.entityData.set(TRANSPORT_STATE, STATE_IDLE);
		setTransportTicks(0);
		this.pendingDestinationDimension = null;
		this.pendingDestinationAnchor = null;
	}

	private void resetToAnchor() {
		clearTransport();
		if (hasAnchor()) {
			snapToAnchor();
		}
	}

	private int getTransportState() {
		return this.entityData.get(TRANSPORT_STATE);
	}

	private int getTransportTicks() {
		return this.entityData.get(TRANSPORT_TICKS);
	}

	private void setTransportTicks(int ticks) {
		this.entityData.set(TRANSPORT_TICKS, ticks);
	}

	private boolean isTransporting() {
		return getTransportState() != STATE_IDLE;
	}

	private boolean isOrbitSide() {
		return Planet.EARTH_ORBIT.equals(level().dimension());
	}

	private double getDockX() {
		return getAnchor().getX() + 0.5D;
	}

	private double getDockY() {
		return getAnchor().getY() + (isOrbitSide() ? ORBIT_DOCK_Y_OFFSET : ANCHOR_Y_OFFSET);
	}

	private double getDockZ() {
		return getAnchor().getZ() + 0.5D;
	}

	private void snapToAnchor() {
		if (!hasAnchor()) {
			return;
		}
		setPos(getDockX(), getDockY(), getDockZ());
	}

	boolean shouldRenderCables() {
		return hasAnchor();
	}

	int cableCount() {
		return CABLE_OFFSETS.length;
	}

	Vec3 getCableStart(int index, float partialTick) {
		Vec3 offset = cableOffset(index);
		if (isOrbitSide()) {
			return new Vec3(getDockX() + offset.x, CABLE_BOTTOM_Y, getDockZ() + offset.z);
		}
		return new Vec3(getDockX() + offset.x, getDockY() + offset.y, getDockZ() + offset.z);
	}

	Vec3 getCableEnd(int index) {
		Vec3 offset = cableOffset(index);
		if (isOrbitSide()) {
			return new Vec3(getDockX() + offset.x, getDockY() + offset.y, getDockZ() + offset.z);
		}
		return new Vec3(getDockX() + offset.x, CABLE_TOP_Y, getDockZ() + offset.z);
	}

	private Vec3 cableOffset(int index) {
		return CABLE_OFFSETS[Mth.clamp(index, 0, CABLE_OFFSETS.length - 1)];
	}

	@Nullable
	private ServerPlayer getPassengerPlayer() {
		return getFirstPassenger() instanceof ServerPlayer player ? player : null;
	}

	@Nullable
	private static SpaceElevatorEntity findOrCreateElevator(ServerLevel level, BlockPos anchorPos) {
		loadAnchorChunk(level, anchorPos);
		SpaceElevatorEntity existing = findElevator(level, anchorPos);
		if (existing != null) {
			return existing;
		}

		SpaceElevatorEntity elevator = CmiEntity.SPACE_ELEVATOR.get().create(level);
		if (elevator == null) {
			return null;
		}
		elevator.setAnchor(anchorPos);
		level.addFreshEntity(elevator);
		return elevator;
	}

	@Nullable
	private static SpaceElevatorEntity findElevator(Level level, BlockPos anchorPos) {
		Vec3 center = Vec3.atCenterOf(anchorPos);
		AABB bounds = AABB.ofSize(center, SEARCH_RADIUS * 2.0D, SEARCH_HEIGHT, SEARCH_RADIUS * 2.0D);
		return level.getEntitiesOfClass(SpaceElevatorEntity.class, bounds, entity -> entity.isAlive() && entity.isAnchoredTo(anchorPos))
				.stream()
				.findFirst()
				.orElse(null);
	}

	private static void loadAnchorChunk(ServerLevel level, BlockPos anchorPos) {
		ChunkPos chunkPos = new ChunkPos(anchorPos);
		level.getChunkSource().addRegionTicket(TicketType.PORTAL, chunkPos, 1, anchorPos);
		level.getChunk(chunkPos.x, chunkPos.z);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
		registrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
		state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(ANCHOR_POS, BlockPos.ZERO);
		this.entityData.define(HAS_ANCHOR, false);
		this.entityData.define(TRANSPORT_STATE, STATE_IDLE);
		this.entityData.define(TRANSPORT_TICKS, 0);
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
		if (tag.contains("AnchorPos")) {
			setAnchor(BlockPos.of(tag.getLong("AnchorPos")));
		}
		clearTransport();
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
		if (hasAnchor()) {
			tag.putLong("AnchorPos", getAnchor().asLong());
		}
	}

	@Override
	public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (TRANSPORT_STATE.equals(key)) {
			this.lerpSteps = 0;
		}
	}

	@Override
	public void lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYRot = yRot;
		this.lerpXRot = xRot;
		this.lerpSteps = lerpSteps;
	}

	@Override
	protected boolean canAddPassenger(@NotNull Entity entity) {
		return getPassengers().isEmpty();
	}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}
		if (level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (isTransporting() || player.isPassenger()) {
			return InteractionResult.FAIL;
		}
		return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.FAIL;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public Vec3 getDismountLocationForPassenger(@NotNull LivingEntity passenger) {
		return findExitPosition();
	}

	@Override
	public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction function) {
		if (hasPassenger(passenger)) {
			function.accept(passenger, getX(), getY() - 0.5D, getZ());
		}
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double sqrDistance) {
		if (shouldRenderCables()) return true;
		return super.shouldRenderAtSqrDistance(sqrDistance);
	}
}
