package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.mbd2.api.capability.recipe.IO;
import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.config.*;
import com.lowdragmc.mbd2.common.trait.TraitDefinition;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.item.ItemSlotCapabilityTraitDefinition;
import dev.celestiacraft.cmi.tags.CmiFluidTags;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * 蒸汽机器抽象父类: 组装层 + 运行时蒸汽逻辑 (连续抽取)。
 * <p>
 * 消耗语义: 工作期间每个 tick 从蒸汽槽抽取蒸汽; 默认消耗由 {@link #steamPerTick(int)} 定义,
 * 配方可以在 data 里写 "steam_per_tick" 键覆盖 (没写就沿用默认)。
 * <p>
 * 状态树: base -> (working, suspend)。蒸汽不足 -> 配方暂停 + suspend 状态; 蒸汽恢复 -> 继续工作。
 */
public abstract class AbstractSteamMachine<M extends AbstractSteamMachine<M>> {
	/**
	 * 蒸汽槽 trait 名 (输入总线靠它代理, UI 靠它定位)
	 */
	public static final String STEAM_TRAIT_NAME = "steam";
	/**
	 * 配方 data 键: 覆盖本配方的每 tick 蒸汽消耗 (mB), 不写则沿用机器默认
	 */
	public static final String STEAM_PER_TICK_KEY = "steam_per_tick";

	@Getter
	protected final ResourceLocation id;
	@Getter
	protected ResourceLocation recipeTypeId;
	@Getter
	protected int steamPerTick = 2;
	@Nullable
	protected Function<String, ResourceLocation> stateModels;

	protected AbstractSteamMachine(ResourceLocation id) {
		this.id = id;
	}

	/**
	 * 本机专属配方类型 (专业对口, 注册见 {@link SteamRegistry#registerRecipeTypes})
	 *
	 * @param recipeTypeId
	 * @return
	 */
	public M recipeType(ResourceLocation recipeTypeId) {
		this.recipeTypeId = recipeTypeId;
		return self();
	}

	/**
	 * 默认每 tick 蒸汽消耗 (mB), 默认 2; 配方可用 data.steam_per_tick 覆盖
	 *
	 * @param mBPerTick
	 * @return
	 */
	public M steamPerTick(int mBPerTick) {
		steamPerTick = mBPerTick;
		return self();
	}

	/**
	 * 只有 base 模型 (working/suspend 继承)
	 *
	 * @param off
	 * @return
	 */
	public M model(ResourceLocation off) {
		return model((state) -> {
			return "working".equals(state) ? null : off;
		});
	}

	/**
	 * base + working 模型 (suspend 继承 base)
	 *
	 * @param off
	 * @param on
	 * @return
	 */
	public M model(ResourceLocation off, ResourceLocation on) {
		return model((state) -> {
			return "working".equals(state) ? on : off;
		});
	}

	/**
	 * 动态模型: 按状态名 ("base"/"working"/"suspend") 返回该状态的模型,
	 * 返回 null 表示该状态不设渲染器 (继承父状态)
	 *
	 * @param stateModels
	 * @return
	 */
	public M model(Function<String, ResourceLocation> stateModels) {
		this.stateModels = stateModels;
		return self();
	}

	protected M self() {
		return (M) this;
	}

	/**
	 * 状态树: base -> (working, suspend)
	 */
	protected MachineState createRootState() {
		return MachineState.builder()
				.name("base")
				.modelRenderer(modelOf("base"))
				.shape(Shapes.block())
				.children(List.of(state("working"), state("suspend")))
				.build();
	}

	private MachineState state(String name) {
		MachineState.Builder<?> builder = MachineState.builder()
				.name(name)
				.shape(Shapes.block());
		ResourceLocation model = modelOf(name);
		if (model != null) {
			builder.modelRenderer(model);
		}
		return builder.build();
	}

	@Nullable
	private ResourceLocation modelOf(String state) {
		return stateModels == null ? null : stateModels.apply(state);
	}

	/**
	 * 蒸汽槽 trait: 只收 forge:steam, 全方向 IN; 容量由调用方给定 (单方块形态用)
	 */
	protected TraitDefinition createSteamTank(int capacity) {
		FluidTankCapabilityTraitDefinition tank = new FluidTankCapabilityTraitDefinition();

		tank.setName(STEAM_TRAIT_NAME);
		tank.setPriority(0);
		tank.setRecipeHandlerIO(IO.IN);
		tank.setGuiIO(IO.IN);
		tank.setTankSize(1);
		tank.setCapacity(capacity);
		tank.setAllowSameFluids(true);
		tank.getFluidFilterSettings()
				.setFilterTags(List.of(CmiFluidTags.STEAM.location()));
		tank.getCapabilityIO()
				.setInternal(IO.IN);
		tank.getCapabilityIO()
				.setFrontIO(IO.IN);
		tank.getCapabilityIO()
				.setBackIO(IO.IN);
		tank.getCapabilityIO()
				.setLeftIO(IO.IN);
		tank.getCapabilityIO()
				.setRightIO(IO.IN);
		tank.getCapabilityIO()
				.setTopIO(IO.IN);
		tank.getCapabilityIO()
				.setBottomIO(IO.IN);
		tank.getAutoInput()
				.setEnable(false);
		tank.getAutoOutput()
				.setEnable(false);
		return tank;
	}

	/**
	 * 机器设置: 基础开关。蒸汽槽是单方块形态的特征, 由 SingleSteamMachine 添加;
	 * 多方块没有蒸汽槽 (蒸汽仓 = 输入总线)。机器作者可覆盖追加物品/流体槽。
	 *
	 * @return
	 */
	protected ConfigMachineSettings createSettings() {
		return ConfigMachineSettings.builder()
				.hasUI(true)
				.dropMachineItem(true)
				.build();
	}

	/**
	 * 配方逻辑: 指向本机配方类型
	 */
	protected ConfigRecipeLogicSettings createRecipeLogic() {
		return ConfigRecipeLogicSettings.builder()
				.enable(true)
				.recipeType(recipeTypeId)
				.build();
	}

	/**
	 * 子类实现形态差异: 单方块 / 多方块装配
	 *
	 * @param rootState
	 * @param settings
	 * @param logic
	 * @return
	 */
	protected abstract MBDMachineDefinition buildDefinition(
			MachineState rootState,
			ConfigMachineSettings settings,
			ConfigRecipeLogicSettings logic
	);

	/**
	 * 构建机器定义: 组装 + 注入 UI + 登记注册表
	 *
	 * @return
	 */
	public MBDMachineDefinition build() {
		MachineState rootState = createRootState();
		ConfigMachineSettings settings = createSettings();
		ConfigRecipeLogicSettings logic = createRecipeLogic();
		MBDMachineDefinition definition = buildDefinition(rootState, settings, logic);
		injectUI(definition);
		SteamRegistry.register(this);
		return definition;
	}

	/**
	 * 标准蒸汽机 UI: 标题 + 蒸汽条 + 进度条 + 蒸汽槽 + 物品槽自动排布
	 *
	 * @param machine
	 * @return
	 */
	protected WidgetGroup createStandardUI(MBDMachine machine) {
		return UISpec.create(machine, 176, 166, (builder) -> {
			builder.background("ldlib:textures/gui/background.png")
					.title(70, 5)
					.steamBar(() -> steamFillRatio(machine), 60, 20, 18, 52) // 聚合水位 (多方块 = 所有蒸汽仓)
					.progressBar(79, 42);

			// 物品槽按配方 IO 自动两列排布
			int inX = 34;
			int outX = 108;
			for (TraitDefinition trait : machine.getDefinition().machineSettings().traitDefinitions()) {
				if (trait instanceof ItemSlotCapabilityTraitDefinition slot) {
					if (slot.getRecipeHandlerIO() == IO.OUT) {
						builder.slot(slot.getName(), outX, 42);
						outX += 22;
					} else {
						builder.slot(slot.getName(), inX, 42);
						inX += 22;
					}
				}
			}

			// 蒸汽槽固定在右侧 (多方块无控制器蒸汽槽, 蒸汽在输入总线里, 只显示聚合条)
			FluidTankCapabilityTrait steamTank = machine.getTraitByName(FluidTankCapabilityTrait.class, STEAM_TRAIT_NAME);
			if (steamTank != null) {
				builder.tank(STEAM_TRAIT_NAME, 141, 22, 18, 58);
			}
		});
	}

	/**
	 * 把 {@link #createStandardUI} 注入机器的 uiCreator (private 字段无 setter, 反射)。
	 * build() 自动调用; 子类覆盖 createStandardUI 即自动生效 (虚方法分发)。
	 * 不需要 UI 的子类可覆盖本方法留空。
	 */
	protected void injectUI(MBDMachineDefinition definition) {
		setPrivateField(
				definition,
				"uiCreator",
				(Function<MBDMachine, WidgetGroup>) this::createStandardUI
		);
	}

	/**
	 * 工作 tick: 抽蒸汽; 不足则暂停配方 + 切 suspend 状态
	 *
	 * @param machine
	 * @param recipe
	 */
	protected void consumeSteamWhileWorking(MBDMachine machine, MBDRecipe recipe) {
		List<FluidStorage> storages = findSteamStorages(machine);
		int perTick = getSteamPerTickFor(recipe);
		long total = storages.stream().mapToLong(FluidStorage::getFluidAmount).sum();
		if (total < perTick) {
			machine.getRecipeLogic().setStatus(RecipeLogic.Status.SUSPEND);
			machine.setMachineState("suspend");
			return;
		}
		// 按顺序从蒸汽仓抽取 (多方块 = 并联的输入总线)
		int remain = perTick;
		for (FluidStorage storage : storages) {
			if (remain <= 0) {
				break;
			}
			FluidStack drained = storage.drain(remain, storage.getFluid(), true, true);
			if (drained != null && !drained.isEmpty()) {
				remain = (int) (remain - drained.getAmount());
			}
		}
	}

	/**
	 * 机器 tick: suspend 状态下蒸汽恢复足够则继续工作
	 *
	 * @param machine
	 */
	protected void tryResume(MBDMachine machine) {
		if (!machine.getRecipeLogic().isSuspend()) {
			return;
		}
		long total = findSteamStorages(machine).stream()
				.mapToLong(FluidStorage::getFluidAmount).sum();
		if (total >= steamPerTick) {
			machine.getRecipeLogic().setStatus(RecipeLogic.Status.WORKING);
			machine.setMachineState("working");
		}
	}

	/**
	 * 蒸汽源: 单方块 = 自身蒸汽槽; 多方块 = 成型结构中所有输入总线的蒸汽仓 (子类覆盖)。
	 */
	protected List<FluidStorage> findSteamStorages(MBDMachine machine) {
		FluidTankCapabilityTrait tank = machine.getTraitByName(FluidTankCapabilityTrait.class, STEAM_TRAIT_NAME);
		if (tank == null || tank.storages.length == 0) {
			return List.of();
		}
		return List.of(tank.storages[0]);
	}

	/** 蒸汽水位 (聚合量 / 聚合容量), UI 用 */
	protected double steamFillRatio(MBDMachine machine) {
		List<FluidStorage> storages = findSteamStorages(machine);
		long total = storages.stream().mapToLong(FluidStorage::getFluidAmount).sum();
		long capacity = storages.stream().mapToLong(FluidStorage::getCapacity).sum();
		return capacity > 0 ? (double) total / capacity : 0;
	}

	/**
	 * 本 tick 蒸汽消耗: 配方 data 写了 steam_per_tick 则覆盖, 否则用机器默认
	 *
	 * @param recipe
	 * @return
	 */
	protected int getSteamPerTickFor(MBDRecipe recipe) {
		if (recipe != null && recipe.data != null && recipe.data.contains(STEAM_PER_TICK_KEY)) {
			return recipe.data.getInt(STEAM_PER_TICK_KEY);
		}
		return steamPerTick;
	}

	/**
	 * 设置 Java 对象的 private 字段 (绕开 MB2 缺失的 setter)
	 *
	 * @param target
	 * @param fieldName
	 * @param value
	 */
	protected static void setPrivateField(Object target, String fieldName, Object value) {
		try {
			var field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to set private field " + fieldName + " on " + target.getClass(), e);
		}
	}
}