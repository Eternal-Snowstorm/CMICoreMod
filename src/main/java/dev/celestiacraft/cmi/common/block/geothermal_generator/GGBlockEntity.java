package dev.celestiacraft.cmi.common.block.geothermal_generator;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.celestiacraft.cmi.common.block.geothermal_generator.capability.GGEnergyStorage;
import dev.celestiacraft.cmi.config.common.GeothermalGeneratorConfig;
import dev.celestiacraft.cmi.tags.CmiFluidTags;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GGBlockEntity extends SmartBlockEntity {
	@Getter
	private int selfIncreasingEnergy = 0;
	private final GGEnergyStorage storage;
	@Getter
	private int storagedEnergy;
	@Getter
	private static final int BASE_PRODUCTION = GeothermalGeneratorConfig.PRODUCTION_EFFICIENCY.get();

	public GGBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		storage = new GGEnergyStorage(this);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
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
			BlockState stateValue = state.setValue(BasicBlock.LIT, working);
			level.setBlockAndUpdate(worldPosition, stateValue);
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
	 * <p>
	 * 并且只能在下界工作
	 * </p>
	 *
	 * <p>每检测到一个有效接触面, 发电机的产能倍率都会多一倍:
	 *
	 * <p>最终产能计算公式为:
	 *
	 * <pre>
	 * BASE_PRODUCTION × 接触面数
	 * </pre>
	 *
	 * <p>
	 * 因此理论上产量最高可以达到 ({@link #BASE_PRODUCTION} * 6) / Tick
	 * <p>
	 * 当没有任何有效流体接触时, 发电机停止工作并返回 0
	 *
	 * @return 当前 Tick 产生的 FE 数量
	 */
	private int working() {
		// 流体接触面
		int contactSurface = 0;

		for (Direction direction : Direction.values()) {
			BlockPos pos = worldPosition.relative(direction);
			FluidState fluid = level.getFluidState(pos);

			if (!level.dimension().equals(Level.NETHER)) {
				return 0;
			}
			if (fluid.is(CmiFluidTags.GG_WORK_FLUID)) {
				contactSurface++;
			}
		}

		if (contactSurface == 0) {
			return 0;
		}

		return BASE_PRODUCTION * contactSurface;
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