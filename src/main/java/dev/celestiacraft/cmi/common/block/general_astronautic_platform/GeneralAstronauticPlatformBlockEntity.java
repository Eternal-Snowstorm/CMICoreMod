package dev.celestiacraft.cmi.common.block.general_astronautic_platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.celestiacraft.cmi.common.block.general_astronautic_platform.io.PlatformInputFluidHandler;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.io.PlatformInputItemHandler;
import dev.celestiacraft.cmi.common.block.general_astronautic_platform.io.PlatformOutputItemHandler;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortType;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.structure.SpaceElevatorBaseStructure;
import dev.celestiacraft.cmi.common.block.space_elevator_io.SpaceElevatorIoHost;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import dev.celestiacraft.cmi.compat.adastra.AdAstraPlatformRocket;
import dev.celestiacraft.cmi.compat.adastra.AdAstraRocketFluidHandler;
import dev.celestiacraft.cmi.compat.adastra.AdAstraRocketItemHandler;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
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
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private LazyOptional<IItemHandler> inputItemCap = LazyOptional.empty();
	private LazyOptional<IItemHandler> outputItemCap = LazyOptional.empty();
	private LazyOptional<IFluidHandler> inputFluidCap = LazyOptional.empty();

	private boolean lastPowered;

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
	}

	// 重建三个对外能力
	public void rebuildCaps() {
		inputItemCap = LazyOptional.of(() -> new PlatformInputItemHandler(this));
		outputItemCap = LazyOptional.of(() -> new PlatformOutputItemHandler(this));
		inputFluidCap = LazyOptional.of(() -> new PlatformInputFluidHandler(this));
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

	// 仅当火箭上有玩家乘客且燃料充足时进入倒计时
	private void launchRocket() {
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
}