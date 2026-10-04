package dev.celestiacraft.cmi.common.block.solar_boiler;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.block.solar_boiler.capability.SolarBoilerFluidCapability;
import dev.celestiacraft.cmi.common.block.solar_boiler.capability.SolarBoilerFluidTank;
import dev.celestiacraft.cmi.common.register.block.SolarBoilerBlocks;
import dev.celestiacraft.cmi.config.common.SolarBoilerConfig;
import dev.celestiacraft.cmi.utils.ModResources;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SolarBoilerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
	protected final SolarBoilerFluidTank waterTank;
	protected final SolarBoilerFluidTank steamTank;
	private boolean working;

	private SolarBoilerFluidCapability fluidCapability;

	public SolarBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		int capacity = getFluidCapacity();
		waterTank = new SolarBoilerFluidTank(capacity, (stack) -> {
			return stack.getFluid().is(FluidTags.WATER);
		}, this::setChanged);

		steamTank = new SolarBoilerFluidTank(capacity, (stack) -> {
			return true;
		}, this::setChanged);

		fluidCapability = new SolarBoilerFluidCapability(waterTank, steamTank);
	}

	/**
	 * 每 Tick 消耗的水
	 *
	 * @return
	 */
	public abstract int getWaterConsumptionPerTick();

	/**
	 * 容量
	 *
	 * @return
	 */
	protected abstract int getFluidCapacity();

	@Override
	public void tick() {
		super.tick();

		if (level == null || level.isClientSide()) {
			return;
		}

		LightMode mode = getLightMode();
		if (mode == LightMode.NONE) {
			return;
		}

		process(mode);
	}

	/**
	 * 判定"人造光照"模式所需的最低方块光照等级 (0-15)
	 * <p>
	 * 当锅炉无法获得自然光照时, 若所在位置的人造光照达到该等级,
	 * 则仍可以按配置的人造光照效率倍率 (默认 50%) 运行。
	 */
	public static final int ARTIFICIAL_LIGHT_THRESHOLD = 8;

	/**
	 * 判断当前是否满足太阳能锅炉的运行条件
	 * <p>
	 * 太阳能锅炉依靠光照运行, 不同光照下效率不同:
	 * <ul>
	 *     <li><b>自然光照 (顶部无遮挡, 且处于白天)</b>: 效率随天气变化, 晴天 100%,
	 *     雨天与雷暴天的效率分别由 {@link SolarBoilerConfig#RAIN_EFFICIENCY_MULTIPLIER} (默认 50%)
	 *     与 {@link SolarBoilerConfig#THUNDER_EFFICIENCY_MULTIPLIER} (默认 25%) 决定;
	 *     在末地中不存在昼夜循环和天气系统, 因此只要顶部无遮挡即可</li>
	 *     <li><b>人造光照 (效率由 {@link SolarBoilerConfig#ARTIFICIAL_LIGHT_EFFICIENCY_MULTIPLIER} 决定, 默认 50%)</b>:
	 *     当无法获得自然光照时 (如顶部有遮挡, 夜晚或室内), 只要所在位置的人造光照
	 *     达到 {@link #ARTIFICIAL_LIGHT_THRESHOLD} 即可继续以较低效率运行</li>
	 * </ul>
	 * 当两种光照均无法提供效率时 (如夜晚且没有光照), 锅炉将停止产热和消耗水
	 *
	 * @return {@code true} 如果当前满足运行条件, 否则返回 {@code false}
	 */
	protected boolean canWork() {
		return getCurrentConsumption() > 0;
	}

	/**
	 * 当前的光源模式
	 * <p>
	 * 自然光照只在白天有效, 并随天气变化: 晴天为 {@link LightMode#SUNLIGHT}, 雨天为 {@link LightMode#RAIN},
	 * 雷暴为 {@link LightMode#THUNDER}; 无法获得自然光照时 (夜晚或顶部有遮挡) 则改用
	 * {@link LightMode#ARTIFICIAL} 人造光照
	 *
	 * @return 当前的光源模式, 没有任何可用光源时返回 {@link LightMode#NONE}
	 */
	public LightMode getLightMode() {
		if (hasNaturalLight()) {
			if (isRaining()) {
				return isThundering() ? LightMode.THUNDER : LightMode.RAIN;
			}

			return LightMode.SUNLIGHT;
		}

		return hasArtificialLight() ? LightMode.ARTIFICIAL : LightMode.NONE;
	}

	/**
	 * 当前是否处于"自然光照"模式 (顶部无遮挡, 且处于白天)
	 * <p>
	 * 天气不再决定能否使用自然光照, 只影响其效率, 详见 {@link #getLightMode()} 。
	 * 末地没有昼夜循环与天气系统, 因此只判断顶部是否无遮挡
	 */
	public boolean hasNaturalLight() {
		if (level == null) {
			return false;
		}

		if (level.dimension().equals(Level.END)) {
			return hasOpenSky();
		}

		long time = level.getDayTime() % 24000;

		return level.canSeeSky(worldPosition.above())
				&& time < 13000;
	}

	/**
	 * 当前位置是否正在下雨
	 * <p>
	 * 锅炉本体为不透明方块, 降水不会落在其内部, 因此检查其上方方块处的降水情况。
	 * 降水类型为雪时 (如积雪生物群系) 不视为下雨, 该位置仍按晴天计算效率
	 */
	public boolean isRaining() {
		if (level == null) {
			return false;
		}

		return level.isRainingAt(worldPosition.above());
	}

	/**
	 * 当前是否处于雷暴天气 (只在拥有天气的维度中生效)
	 */
	public boolean isThundering() {
		return level != null && level.isThundering();
	}

	/**
	 * 当前是否处于"人造光照"模式 (效率由配置决定, 默认 50%)
	 */
	public boolean hasArtificialLight() {
		if (level == null) {
			return false;
		}

		if (SolarBoilerConfig.ARTIFICIAL_LIGHT_EFFICIENCY_MULTIPLIER.get() <= 0) {
			return false;
		}

		/*
		 * 锅炉本体为不透明方块, 光照不会进入其内部, 因此自身位置存储的方块光照始终为 0;
		 * 需要检查其上方的空气方块 (或其它可透光方块) 处的人造光照
		 */
		int blockLight = Math.max(
				level.getBrightness(LightLayer.BLOCK, worldPosition),
				level.getBrightness(LightLayer.BLOCK, worldPosition.above())
		);

		return blockLight >= ARTIFICIAL_LIGHT_THRESHOLD;
	}

	/**
	 * 指定光源模式下的效率倍率
	 */
	public static double getEfficiencyMultiplier(LightMode mode) {
		return switch (mode) {
			case SUNLIGHT -> 1.0;
			case RAIN -> SolarBoilerConfig.RAIN_EFFICIENCY_MULTIPLIER.get();
			case THUNDER -> SolarBoilerConfig.THUNDER_EFFICIENCY_MULTIPLIER.get();
			case ARTIFICIAL -> SolarBoilerConfig.ARTIFICIAL_LIGHT_EFFICIENCY_MULTIPLIER.get();
			default -> 0.0;
		};
	}

	/**
	 * 指定光源模式下的效率百分比 (如 0.5 倍率对应 50)
	 */
	public static int getEfficiencyPercent(LightMode mode) {
		return (int) Math.round(getEfficiencyMultiplier(mode) * 100);
	}

	/**
	 * 计算指定光源模式下的实际效率 (mB / Tick)
	 * <p>
	 * 效率倍率被配置为 0 时返回 0, 表示该光源下锅炉无法工作
	 *
	 * @param baseEfficiency 晴天自然光照 (100%) 下的效率
	 */
	public static int getEfficiency(int baseEfficiency, LightMode mode) {
		double multiplier = getEfficiencyMultiplier(mode);

		if (baseEfficiency <= 0 || multiplier <= 0.0) {
			return 0;
		}

		return Math.max(1, (int) Math.round(baseEfficiency * multiplier));
	}

	/**
	 * 人造光照模式下的效率百分比 (如 0.5 倍率对应 50)
	 */
	public static int getArtificialLightEfficiencyPercent() {
		return getEfficiencyPercent(LightMode.ARTIFICIAL);
	}

	/**
	 * 计算人造光照模式下的实际效率 (mB / Tick)
	 *
	 * @param baseEfficiency 晴天自然光照 (100%) 下的效率
	 */
	public static int getArtificialLightEfficiency(int baseEfficiency) {
		return getEfficiency(baseEfficiency, LightMode.ARTIFICIAL);
	}

	/**
	 * 当前光源模式下实际的每 Tick 消耗/产量
	 *
	 * @return 实际的 mB / Tick, 无可用光源时返回 0
	 */
	public int getCurrentConsumption() {
		return getEfficiency(getWaterConsumptionPerTick(), getLightMode());
	}

	private boolean hasOpenSky() {
		return level.getHeight(
				Heightmap.Types.MOTION_BLOCKING,
				worldPosition.getX(),
				worldPosition.getZ()
		) <= worldPosition.getY() + 1;
	}

	protected void process(LightMode mode) {
		int consume = getEfficiency(getWaterConsumptionPerTick(), mode);
		if (consume <= 0) {
			return;
		}

		// 水检查
		FluidStack water = waterTank.getFluid();
		if (water.isEmpty() || !water.getFluid().is(FluidTags.WATER)) {
			return;
		}
		if (waterTank.getFluidAmount() < consume) {
			return;
		}

		// 空间检查
		if (steamTank.getSpace() < consume) {
			return;
		}

		FluidStack steam = ModResources.STEAM.getFluidStack(consume);

		int filled = steamTank.fill(steam, IFluidHandler.FluidAction.SIMULATE);
		if (filled <= 0) {
			return;
		}

		// 执行
		waterTank.drain(consume, IFluidHandler.FluidAction.EXECUTE);
		steamTank.fill(steam, IFluidHandler.FluidAction.EXECUTE);

		setChanged();
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction direction) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCapability.get(direction).cast();
		}
		return super.getCapability(capability, direction);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		fluidCapability.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		fluidCapability = new SolarBoilerFluidCapability(waterTank, steamTank);
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put("WaterTank", waterTank.writeToNBT(new CompoundTag()));
		tag.put("SteamTank", steamTank.writeToNBT(new CompoundTag()));
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		waterTank.readFromNBT(tag.getCompound("WaterTank"));
		steamTank.readFromNBT(tag.getCompound("SteamTank"));
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public void handleUpdateTag(@NotNull CompoundTag tag) {
		load(tag);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		int efficiency = 0;
		int capacity = 0;

		if (getBlockState().is(SolarBoilerBlocks.BRONZE_SOLAR_BOILER.get())) {
			efficiency = SolarBoilerConfig.BRONZE_EFFICIENCY.get();
			capacity = SolarBoilerConfig.BRONZE_CAPACITY.get();
		} else if (getBlockState().is(SolarBoilerBlocks.CAST_IRON_SOLAR_BOILER.get())) {
			efficiency = SolarBoilerConfig.CAST_IRON_EFFICIENCY.get();
			capacity = SolarBoilerConfig.CAST_IRON_CAPACITY.get();
		} else if (getBlockState().is(SolarBoilerBlocks.STEEL_SOLAR_BOILER.get())) {
			efficiency = SolarBoilerConfig.STEEL_EFFICIENCY.get();
			capacity = SolarBoilerConfig.STEEL_CAPACITY.get();
		}

		if (canWork()) {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.satisfy")
					.style(ChatFormatting.GREEN)
					.style(ChatFormatting.BOLD)
					.forGoggles(tooltip);
		} else {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.not_satisfy")
					.style(ChatFormatting.RED)
					.style(ChatFormatting.BOLD)
					.forGoggles(tooltip);
		}

		// 当前光照模式与天气
		LightMode mode = getLightMode();

		if (mode == LightMode.SUNLIGHT) {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.natural_light")
					.style(ChatFormatting.AQUA)
					.forGoggles(tooltip);
		} else if (mode == LightMode.RAIN) {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.rain_light", getEfficiencyPercent(LightMode.RAIN))
					.style(ChatFormatting.YELLOW)
					.forGoggles(tooltip);
		} else if (mode == LightMode.THUNDER) {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.thunder_light", getEfficiencyPercent(LightMode.THUNDER))
					.style(ChatFormatting.YELLOW)
					.forGoggles(tooltip);
		} else if (mode == LightMode.ARTIFICIAL) {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.artificial_light", getArtificialLightEfficiencyPercent())
					.style(ChatFormatting.YELLOW)
					.forGoggles(tooltip);
		} else {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.no_light")
					.style(ChatFormatting.RED)
					.forGoggles(tooltip);
		}

		CmiLang.isCtrlDown(tooltip);
		if (Screen.hasControlDown()) {
			CmiLang.builder()
					.translate("tooltip.solar_boiler.info")
					.style(ChatFormatting.GOLD)
					.forGoggles(tooltip);

			CmiLang.builder()
					.translate("tooltip.solar_boiler.efficiency", efficiency)
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			CmiLang.builder()
					.translate("tooltip.solar_boiler.rain_efficiency", getEfficiency(efficiency, LightMode.RAIN))
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			CmiLang.builder()
					.translate("tooltip.solar_boiler.thunder_efficiency", getEfficiency(efficiency, LightMode.THUNDER))
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			CmiLang.builder()
					.translate("tooltip.solar_boiler.artificial_efficiency", getArtificialLightEfficiency(efficiency))
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			CmiLang.builder()
					.translate("tooltip.solar_boiler.capacity", capacity)
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			CmiLang.builder()
					.translate("tooltip.solar_boiler.total_capacity", capacity << 1)
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);
		}

		return true;
	}
}