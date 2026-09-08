package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.mbd2.api.block.RotationState;
import com.lowdragmc.mbd2.api.pattern.BlockPattern;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.MultiblockMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.config.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 多方块蒸汽机器 (固定大小结构)。
 * <p>
 * 用法:
 * new MultiBlockSteamMachine(id("cmi:steam_oven"))
 * .recipeType(id("cmi:steam_oven_rt"))
 * .steamCapacity(64000)
 * .steamPerTick(4)
 * .model(off, on)
 * .pattern(FactoryBlockPattern.start().aisle(...)...build())
 * .build()
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
		this.cachedPattern = null;
		return this;
	}

	@Nullable
	private BlockPattern pattern() {
		if (cachedPattern == null && patternFactory != null) {
			cachedPattern = patternFactory.get();
		}
		return cachedPattern;
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
