package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.mbd2.api.block.RotationState;
import com.lowdragmc.mbd2.api.machine.IMultiPart;
import com.lowdragmc.mbd2.api.pattern.BlockPattern;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.MBDMultiblockMachine;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.MultiblockMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.config.*;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTrait;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 多方块蒸汽机器 (固定大小结构)。
 * <p>
 * 用法:
 * new MultiBlockSteamMachine(id("cmi:steam_oven"))
 * .recipeType(id("cmi:steam_oven_rt"))
 * .steamPerTick(4)
 * .model(off, on)
 * .pattern(() -> FactoryBlockPattern.start().aisle(...)...build())
 * .build()
 * <p>
 * 注意: 多方块没有自己的蒸汽槽 —— 蒸汽仓 = 成型结构里的蒸汽输入总线 (GT 模型),
 * 容量由总线等级决定; steamCapacity 链式只存在于 SingleSteamMachine。
 */
public class MultiBlockSteamMachine extends AbstractSteamMachine<MultiBlockSteamMachine> {
	@Nullable
	private Supplier<BlockPattern> patternFactory;
	@Nullable
	private BlockPattern cachedPattern;

	public MultiBlockSteamMachine(ResourceLocation id) {
		super(id);
	}

	/**
	 * 固定结构 (每层一个 aisle, y=0 底 → 顶)
	 */
	public MultiBlockSteamMachine pattern(BlockPattern pattern) {
		return pattern(() -> pattern);
	}

	/**
	 * 固定结构, 惰性构建 (推荐)。
	 * <p>
	 * 机器注册发生在 mod 构造阶段 (早于 RegisterEvent), 结构里引用其它 MB2 机器方块
	 * (如输入总线) 时定义期取方块会得到 null; 惰性构建在首次结构匹配时执行 (运行时方块已就绪)。
	 */
	public MultiBlockSteamMachine pattern(Supplier<BlockPattern> patternFactory) {
		this.patternFactory = patternFactory;
		cachedPattern = null;
		return this;
	}

	@Nullable
	private BlockPattern pattern() {
		if (cachedPattern == null && patternFactory != null) {
			cachedPattern = patternFactory.get();
		}
		return cachedPattern;
	}

	/**
	 * 多方块没有自己的蒸汽槽 (蒸汽仓 = 输入总线), 机器设置不含 steam trait。
	 * 机器作者可覆盖追加物品/流体槽。
	 */
	@Override
	protected ConfigMachineSettings createSettings() {
		return ConfigMachineSettings.builder()
				.hasUI(true)
				.dropMachineItem(true)
				.build();
	}

	/**
	 * 蒸汽源: 成型结构中所有输入总线的蒸汽仓 (并联)。
	 */
	@Override
	protected List<FluidStorage> findSteamStorages(MBDMachine machine) {
		List<FluidStorage> result = new ArrayList<>();
		if (machine instanceof MBDMultiblockMachine controller) {
			for (IMultiPart part : controller.getParts()) {
				if (part instanceof MBDMachine partMachine) {
					FluidTankCapabilityTrait tank = partMachine.getTraitByName(
							FluidTankCapabilityTrait.class, STEAM_TRAIT_NAME);
					if (tank != null && tank.storages.length > 0) {
						result.add(tank.storages[0]);
					}
				}
			}
		}
		return result;
	}

	@Override
	protected MBDMachineDefinition buildDefinition(MachineState rootState, ConfigMachineSettings settings, ConfigRecipeLogicSettings logic) {
		MultiblockMachineDefinition.Builder builder = MultiblockMachineDefinition.builder();

		builder.id(id);
		builder.rootState(rootState);
		builder.blockProperties(ConfigBlockProperties.builder()
				.destroyTime(4.0f)
				.explosionResistance(8.0f)
				.rotationState(RotationState.NONE)
				.build());
		builder.itemProperties(ConfigItemProperties.builder()
				.maxStackSize(64)
				.isGui3d(true)
				.build());
		builder.machineSettings(() -> settings);
		builder.recipeLogicSettings(logic);
		builder.multiblockSettings(() -> ConfigMultiblockSettings.builder()
				.showUIOnlyFormed(true)
				.showUIWhenClickStructure(true)
				.build());

		MultiblockMachineDefinition definition = builder.build();
		if (patternFactory != null) {
			definition.blockPatternFactory((machine) -> pattern());
		}
		return definition;
	}
}
