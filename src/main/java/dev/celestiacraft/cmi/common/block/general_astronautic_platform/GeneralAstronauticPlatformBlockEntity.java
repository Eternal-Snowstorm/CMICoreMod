package dev.celestiacraft.cmi.common.block.general_astronautic_platform;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.io.PlatformInputFluidHandler;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.io.PlatformInputItemHandler;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.io.PlatformOutputItemHandler;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortType;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.structure.SpaceElevatorBaseStructure;
import dev.celestiacraft.cmi.common.block.space_elevator_io.SpaceElevatorIoHost;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.RocketLaunchResult;
import dev.celestiacraft.cmi.compat.adastra.AdAstraPlatformRocket;
import dev.celestiacraft.cmi.compat.adastra.AdAstraRocketFluidHandler;
import dev.celestiacraft.cmi.compat.adastra.AdAstraRocketItemHandler;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GeneralAstronauticPlatformBlockEntity extends BlockEntity implements GeoBlockEntity, SpaceElevatorIoHost {
	private static final String TAG_BOUND_PLATFORM = "BoundPlatform";
	private static final String TAG_DISPLAY_STATE = "DisplayState";
	private static final double NOTIFY_RADIUS = 32.0D;
	private static final int FAILURE_DISPLAY_TICKS = 100;
	private static final int DISPLAY_REFRESH_INTERVAL = 10;
	private static final int LANDING_LOAD_TICKS = 100;
	private static final int LANDING_LOAD_CENTER_DISTANCE = 1;
	private static final int LANDING_LOAD_NEIGHBOR_DISTANCE = 0;
	private static final TicketType<ChunkPos> LANDING_LOAD_TICKET = TicketType.create("cmi_landing_load", Comparator.comparingLong(ChunkPos::toLong), 400);

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private LazyOptional<IItemHandler> inputItemCap = LazyOptional.empty();
	private LazyOptional<IItemHandler> outputItemCap = LazyOptional.empty();
	private LazyOptional<IFluidHandler> inputFluidCap = LazyOptional.empty();

	private boolean lastPowered;
	private int displayRefreshCounter;
	private int displayOverrideTicks;
	private int landingLoadTicks;
	private AstronauticPlatformDisplayState displayState = AstronauticPlatformDisplayState.NONE;

	@Nullable
	private AstronauticPlatformDisplayState displayOverride;

	@Nullable
	private GlobalPos boundPlatform;

	public GeneralAstronauticPlatformBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		rebuildCaps();
		PlatformBindingHandler.applyPending(this);
		PlatformBindingHandler.validate(this);
		refreshDisplay();
	}

	// 重建三个对外能力
	public void rebuildCaps() {
		inputItemCap = LazyOptional.of(() -> new PlatformInputItemHandler(this));
		outputItemCap = LazyOptional.of(() -> new PlatformOutputItemHandler(this));
		inputFluidCap = LazyOptional.of(() -> new PlatformInputFluidHandler(this));
	}

	// 火箭停靠后重装配对外能力, 使端口重新解析到平台上的火箭
	public void refreshStorageProxy() {
		rebuildCaps();
		setChanged();
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		inputItemCap.invalidate();
		outputItemCap.invalidate();
		inputFluidCap.invalidate();
		inputItemCap = LazyOptional.empty();
		outputItemCap = LazyOptional.empty();
		inputFluidCap = LazyOptional.empty();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		rebuildCaps();
	}

	// 能力被提前失效时补建一次
	public void ensureCaps() {
		if (inputItemCap.isPresent()) {
			return;
		}
		rebuildCaps();
	}

	// 取停放在平台顶面的探矿火箭实体
	@Nullable
	public ProspectingRocketEntity findRocket() {
		if (level == null) {
			return null;
		}
		return SpaceElevatorConstructionHandler.findRocketOnPlatform(level, worldPosition);
	}

	// 取停放在平台顶面的 Ad Astra 火箭实体
	@Nullable
	public Rocket findAdAstraRocket() {
		if (level == null) {
			return null;
		}
		return AdAstraPlatformRocket.findRocket(level, worldPosition);
	}

	// 平台上是否已经停放了任意火箭
	public boolean hasAnyRocket() {
		return findRocket() != null || findAdAstraRocket() != null;
	}

	// 取平台上火箭的货舱物品能力，探矿火箭优先
	@Nullable
	public IItemHandler getRocketItems() {
		ProspectingRocketEntity prospecting = findRocket();
		if (prospecting != null) {
			return prospecting.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
		}
		Rocket adAstra = findAdAstraRocket();
		if (adAstra == null) {
			return null;
		}
		return new AdAstraRocketItemHandler(adAstra);
	}

	// 取平台上火箭的燃料罐流体能力，探矿火箭优先
	@Nullable
	public IFluidHandler getRocketFluid() {
		ProspectingRocketEntity prospecting = findRocket();
		if (prospecting != null) {
			return prospecting.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
		}
		Rocket adAstra = findAdAstraRocket();
		if (adAstra == null) {
			return null;
		}
		return new AdAstraRocketFluidHandler(adAstra);
	}

	// 按端口类型返回对外暴露的能力，没有对应能力时返回空
	@Override
	public @NotNull LazyOptional<?> getIoCapability(@NotNull IoPortType type) {
		ensureCaps();
		switch (type) {
			case INPUT_ITEM -> {
				return inputItemCap;
			}
			case OUTPUT_ITEM -> {
				return outputItemCap;
			}
			case INPUT_FLUID -> {
				return inputFluidCap;
			}
			default -> {
				return LazyOptional.empty();
			}
		}
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		ensureCaps();
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return inputItemCap.cast();
		}
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return inputFluidCap.cast();
		}
		return super.getCapability(capability, side);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, GeneralAstronauticPlatformBlockEntity be) {
		if (level.isClientSide()) {
			return;
		}
		be.tickRedstone();
		be.tickDisplay();
		be.tickLandingLoad();
	}

	// 红石上升沿触发一次发射尝试
	private void tickRedstone() {
		boolean powered = hasRedstoneSignal();
		if (powered && !lastPowered) {
			launchRocket();
		}
		lastPowered = powered;
	}

	// 检查控制器与全部结构位置周围的红石信号
	private boolean hasRedstoneSignal() {
		if (level == null) {
			return false;
		}
		for (Vec3i offset : SpaceElevatorBaseStructure.STRUCTURE_OFFSETS.keySet()) {
			if (level.getBestNeighborSignal(worldPosition.offset(offset)) > 0) {
				return true;
			}
		}
		return false;
	}

	// 货用火箭直接发往绑定平台, 客运火箭需玩家与燃料
	private void launchRocket() {
		ProspectingRocketEntity prospecting = findRocket();
		if (prospecting != null) {
			GlobalPos target = boundPlatform;
			if (target == null) {
				notifyLaunchFailure(RocketLaunchResult.NO_BINDING);
				return;
			}
			RocketLaunchResult result = prospecting.launch(target);
			notifyLaunchFailure(result);
			showLaunchResult(result);
			return;
		}
		Rocket rocket = findAdAstraRocket();
		if (rocket == null) {
			return;
		}
		if (rocket.isLaunching() || rocket.hasLaunched()) {
			return;
		}
		if (!(rocket.getFirstPassenger() instanceof Player)) {
			return;
		}
		if (!rocket.hasEnoughFuel()) {
			return;
		}
		rocket.initiateLaunchSequence();
	}

	// 把发射结果映射为屏幕图标, 目标被占用单独区分
	private void showLaunchResult(RocketLaunchResult result) {
		if (result == RocketLaunchResult.SUCCESS || result == RocketLaunchResult.NO_BINDING
				|| result == RocketLaunchResult.BUSY || result == RocketLaunchResult.NOT_SERVER) {
			return;
		}
		if (result == RocketLaunchResult.OCCUPIED) {
			showDisplayEvent(AstronauticPlatformDisplayState.TARGET_OCCUPIED, FAILURE_DISPLAY_TICKS);
			return;
		}
		showDisplayEvent(AstronauticPlatformDisplayState.LAUNCH_FAILED, FAILURE_DISPLAY_TICKS);
	}

	// 发射失败时向平台附近玩家提示原因
	private void notifyLaunchFailure(RocketLaunchResult result) {
		if (level == null || result == RocketLaunchResult.SUCCESS || !result.shouldNotify()) {
			return;
		}
		Component message = CmiLang.textDirect(result.messageKey());
		AABB area = new AABB(worldPosition).inflate(NOTIFY_RADIUS);
		for (Player player : level.getEntitiesOfClass(Player.class, area)) {
			player.displayClientMessage(message, true);
		}
	}

	// 客户端读取的屏幕显示状态
	@NotNull
	public AstronauticPlatformDisplayState getDisplayState() {
		return displayState;
	}

	// 按绑定与火箭状态刷新常驻图标
	public void refreshDisplay() {
		if (level != null && level.isClientSide()) {
			return;
		}
		setDisplayState(resolveBaseDisplayState());
	}

	// 显示一次限时事件图标, 覆盖常驻图标
	public void showDisplayEvent(AstronauticPlatformDisplayState state, int ticks) {
		displayOverride = state;
		displayOverrideTicks = ticks;
		setDisplayState(state);
	}

	private void tickDisplay() {
		if (displayOverrideTicks > 0) {
			displayOverrideTicks--;
			if (displayOverrideTicks <= 0) {
				displayOverride = null;
				setDisplayState(resolveBaseDisplayState());
			}
			return;
		}
		if (displayRefreshCounter > 0) {
			displayRefreshCounter--;
			return;
		}
		displayRefreshCounter = DISPLAY_REFRESH_INTERVAL;
		setDisplayState(resolveBaseDisplayState());
	}

	private AstronauticPlatformDisplayState resolveBaseDisplayState() {
		if (boundPlatform == null) {
			return AstronauticPlatformDisplayState.NONE;
		}
		if (hasAnyRocket()) {
			return AstronauticPlatformDisplayState.BOUND_ROCKET;
		}
		return AstronauticPlatformDisplayState.BOUND_EMPTY;
	}

	private void setDisplayState(AstronauticPlatformDisplayState state) {
		if (displayState == state) {
			return;
		}
		displayState = state;
		syncDisplay();
	}

	private void syncDisplay() {
		if (level == null) {
			return;
		}
		BlockState state = getBlockState();
		level.sendBlockUpdated(worldPosition, state, state, 3);
	}

	// 火箭落地后保持平台区块加载五分钟并弱加载周边八区块
	public void beginLandingLoad() {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		landingLoadTicks = LANDING_LOAD_TICKS;
		ChunkPos center = new ChunkPos(worldPosition);
		for (int offsetX = -1; offsetX <= 1; offsetX++) {
			for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
				ChunkPos chunkPos = new ChunkPos(center.x + offsetX, center.z + offsetZ);
				int distance = chunkPos.equals(center) ? LANDING_LOAD_CENTER_DISTANCE : LANDING_LOAD_NEIGHBOR_DISTANCE;
				serverLevel.getChunkSource().addRegionTicket(LANDING_LOAD_TICKET, chunkPos, distance, chunkPos);
			}
		}
	}

	private void tickLandingLoad() {
		if (landingLoadTicks <= 0) {
			return;
		}
		landingLoadTicks--;
		if (landingLoadTicks <= 0) {
			releaseLandingLoad();
		}
	}

	// 释放落地后的加载票据
	public void releaseLandingLoad() {
		landingLoadTicks = 0;
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		ChunkPos center = new ChunkPos(worldPosition);
		for (int offsetX = -1; offsetX <= 1; offsetX++) {
			for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
				ChunkPos chunkPos = new ChunkPos(center.x + offsetX, center.z + offsetZ);
				int distance = chunkPos.equals(center) ? LANDING_LOAD_CENTER_DISTANCE : LANDING_LOAD_NEIGHBOR_DISTANCE;
				serverLevel.getChunkSource().removeRegionTicket(LANDING_LOAD_TICKET, chunkPos, distance, chunkPos);
			}
		}
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		if (boundPlatform != null) {
			PlatformBindingHandler.write(tag, TAG_BOUND_PLATFORM, boundPlatform);
		}
		tag.putString(TAG_DISPLAY_STATE, displayState.name());
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		boundPlatform = PlatformBindingHandler.read(tag, TAG_BOUND_PLATFORM);
		displayState = AstronauticPlatformDisplayState.fromName(tag.contains(TAG_DISPLAY_STATE) ? tag.getString(TAG_DISPLAY_STATE) : null);
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	// 取已绑定的对端平台坐标
	@Nullable
	public GlobalPos getBoundPlatform() {
		return boundPlatform;
	}

	// 记录对端平台坐标, 不写入对端
	public void setBoundPlatform(@Nullable GlobalPos pos) {
		boundPlatform = pos;
		setChanged();
		refreshDisplay();
	}

	// 清除绑定记录
	public void clearBoundPlatform() {
		if (boundPlatform == null) {
			return;
		}
		boundPlatform = null;
		setChanged();
		refreshDisplay();
	}
}