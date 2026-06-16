package dev.celestiacraft.cmi.common.block.geothermal_generator;

import dev.celestiacraft.cmi.common.block.geothermal_generator.capability.GGEnergyStorage;
import dev.celestiacraft.cmi.tags.CmiFluidTags;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GGBlockEntity extends BasicBlockEntity {
	@Getter
	private int selfIncreasingEnergy = 0;
	private final GGEnergyStorage storage;
	@Getter
	private int storagedEnergy;
	@Getter
	private static final int BASE_PRODUCTION = 5000;

	public GGBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		storage = new GGEnergyStorage(this);
	}

	@Override
	public void tick() {
		super.tick();

		if (level == null || level.isClientSide()) {
			return;
		}

		int production = working();

		boolean working = production > 0;

		BlockState state = getBlockState();

		if (state.getValue(BasicBlock.LIT) != working) {
			level.setBlock(
					worldPosition,
					state.setValue(BasicBlock.LIT, working),
					3
			);
		}

		if (working) {
			addEnergy(production);
		}
	}

	/**
	 * 计算当前地热发电机的发电量
	 *
	 * <p>
	 * 发电机将检查六个相邻方向(上, 下, 北, 南, 西, 东)
	 * <p>
	 * 是否存在 {@link CmiFluidTags#GG_WORK_FLUID} 标签中的流体
	 *
	 * <p>每检测到一个有效接触面, 发电机的产能倍率都会翻倍:
	 *
	 * <pre>
	 * 1面 = 1×基础产能
	 * 2面 = 2×基础产能
	 * 3面 = 4×基础产能
	 * 4面 = 8×基础产能
	 * 5面 = 16×基础产能
	 * 6面 = 32×基础产能
	 * </pre>
	 *
	 * <p>最终产能计算公式为:
	 *
	 * <pre>
	 * BASE_PRODUCTION × 2^(接触面数 - 1)
	 * </pre>
	 *
	 * <p>当没有任何有效流体接触时, 发电机停止工作并返回 0
	 *
	 * @return 当前 Tick 产生的 FE 数量
	 */
	private int working() {
		// 流体接触面
		int contactSurface = 0;

		for (Direction direction : Direction.values()) {
			BlockPos pos = worldPosition.relative(direction);
			FluidState fluidState = level.getFluidState(pos);

			if (fluidState.is(CmiFluidTags.GG_WORK_FLUID)) {
				contactSurface++;
			}
		}

		if (contactSurface == 0) {
			return 0;
		}

		return BASE_PRODUCTION * (1 << (contactSurface - 1));
	}

	/**
	 * 通过获取BlockState判断是否在工作
	 *
	 * @return
	 */
	private boolean isWorking() {
		BlockState state = getBlockState();

		return state.getValue(BasicBlock.LIT);
	}

	public void setStoragedEnergy(int energy) {
		storagedEnergy = energy;
		setChanged();
	}

	public void addEnergy(int amount) {
		storagedEnergy = Math.min(
				storagedEnergy + amount,
				500000
		);

		setChanged();
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ENERGY) {
			return storage.get(direction).cast();
		}
		return super.getCapability(capability, direction);
	}
}