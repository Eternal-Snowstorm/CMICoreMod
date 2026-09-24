package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.mbd2.api.capability.recipe.IO;
import com.lowdragmc.mbd2.api.registry.MBDRegistries;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.config.ConfigMachineSettings;
import com.lowdragmc.mbd2.common.machine.definition.config.ConfigPartSettings;
import com.lowdragmc.mbd2.common.trait.CapabilityIO;
import com.lowdragmc.mbd2.common.trait.SimpleCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.TraitDefinition;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.forgeenergy.ForgeEnergyCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.item.ItemSlotCapabilityTraitDefinition;
import com.lowdragmc.mbd2.integration.mekanism.trait.chemical.ChemicalTankCapabilityTraitDefinition;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 总线(接口)的储物槽工厂: 给一条总线补上"自己的"槽位, 让它变成箱子。
 *
 * <p>角色识别: 从机器自己的 {@code partSettings.proxyControllerCapabilities.traitNameFilter}
 * 反查控制器身上被代理的 trait (精确名或前缀), 于是
 * "chemical_reactor_input" -> 物品 + 流体 + 能量, "electrolyzer_input_item" -> 物品 …
 * 认不出来就按纯物品总线处理。
 *
 * <p>界面不在这里 —— 见 {@link MachineUIKit} (统一给所有机器挂界面)。
 * 数值都在这几个常量里, 想改格数/容量改这里。
 */
public class BusKit {
	/** 总线的 id 后缀 */
	public static final String BUS_SUFFIX = "_bus";

	/** 自动生成的 trait 名 (同一台机器上唯一即可) */
	public static final String ITEM_TRAIT = "bus_item";
	public static final String FLUID_TRAIT = "bus_fluid";
	public static final String GAS_TRAIT = "bus_gas";
	public static final String ENERGY_TRAIT = "bus_energy";

	/** 物品总线格数 (3 行 x 9 列) */
	public static final int ITEM_SLOTS = 27;
	/** 单格上限 */
	public static final int SLOT_LIMIT = 64;
	/** 流体 / 气体总线的格数 (和物品一样多) */
	public static final int TANK_COUNT = 27;
	/** 流体总线每格容量 (mB) */
	public static final int FLUID_SLOT_CAPACITY = 16000;
	/** 气体总线每格容量 (mB) */
	public static final int GAS_SLOT_CAPACITY = 16000;
	/** 总线自动 IO 的间隔 (tick): 输入总线拉料 / 输出总线推产物 */
	public static final int AUTO_IO_INTERVAL = 5;

	/** 能量总线缓存 (FE) */
	public static final int ENERGY_CAPACITY = 1_000_000;

	/** 总线能装的东西 */
	public enum Kind {
		ITEM,
		FLUID,
		GAS,
		ENERGY
	}

	/** 已经处理过的总线: id -> 这台总线实际有哪些储物 */
	private static final Map<ResourceLocation, Set<Kind>> CONVERTED = new HashMap<>();

	/** 这台机器算不算总线 */
	public static boolean isBus(ResourceLocation id) {
		return id != null && id.getPath().endsWith(BUS_SUFFIX);
	}

	/**
	 * 给总线补储物槽 (幂等, 按 id 记账)。
	 *
	 * @return 这台总线实际有哪些储物 (空集 = 不是总线 / 没有定义)
	 */
	public static Set<Kind> addStorage(ResourceLocation id, MBDMachineDefinition definition) {
		if (!isBus(id) || definition == null) {
			return Set.of();
		}

		Set<Kind> cached = CONVERTED.get(id);

		if (cached != null) {
			return cached;
		}

		ConfigMachineSettings settings = definition.machineSettings();

		if (settings == null) {
			return Set.of();
		}

		IO io = directionOf(definition);
		Set<Kind> kinds = EnumSet.noneOf(Kind.class);

		for (TraitDefinition source : sourcesOf(filtersOf(definition))) {
			if (source instanceof ItemSlotCapabilityTraitDefinition) {
				kinds.add(Kind.ITEM);
			} else if (source instanceof FluidTankCapabilityTraitDefinition) {
				kinds.add(Kind.FLUID);
			} else if (isGasTank(source)) {
				kinds.add(Kind.GAS);
			} else if (source instanceof ForgeEnergyCapabilityTraitDefinition) {
				kinds.add(Kind.ENERGY);
			}
		}

		// 认不出角色 (没有代理 / 代理没写过滤器) 就当普通物品总线
		if (kinds.isEmpty()) {
			kinds.add(Kind.ITEM);
		}

		if (kinds.contains(Kind.ITEM)) {
			settings.addTraitDefinition(itemTrait(io));
		}

		if (kinds.contains(Kind.FLUID)) {
			settings.addTraitDefinition(fluidTrait(io));
		}

		if (kinds.contains(Kind.GAS)) {
			settings.addTraitDefinition(gasTrait(io));
		}

		if (kinds.contains(Kind.ENERGY)) {
			settings.addTraitDefinition(energyTrait(io));
		}

		CONVERTED.put(id, kinds);
		return kinds;
	}

	// ------------------------------------------------------------------

	/** 这台总线代理了哪些控制器 trait (过滤器字符串) */
	private static Set<String> filtersOf(MBDMachineDefinition definition) {
		Set<String> filters = new LinkedHashSet<>();
		ConfigPartSettings partSettings = definition.partSettings();

		if (partSettings == null) {
			return filters;
		}

		for (ConfigPartSettings.ProxyCapability proxy : partSettings.proxyControllerCapabilities()) {
			String filter = proxy.traitNameFilter();

			if (filter != null && !filter.isEmpty()) {
				filters.add(filter);
			}
		}

		return filters;
	}

	/** 总线的方向: 取代理的 internal IO; 没有或冲突就是 BOTH */
	private static IO directionOf(MBDMachineDefinition definition) {
		IO direction = null;
		ConfigPartSettings partSettings = definition.partSettings();

		if (partSettings == null) {
			return IO.BOTH;
		}

		for (ConfigPartSettings.ProxyCapability proxy : partSettings.proxyControllerCapabilities()) {
			IO io = proxy.capabilityIO().getInternal();

			if (io != IO.IN && io != IO.OUT) {
				continue;
			}

			if (direction == null) {
				direction = io;
			} else if (direction != io) {
				return IO.BOTH;
			}
		}

		return direction == null ? IO.BOTH : direction;
	}

	/** 按过滤器在全注册表里找被代理的 trait (精确名或 "过滤器_xxx" 前缀) */
	private static List<TraitDefinition> sourcesOf(Set<String> filters) {
		List<TraitDefinition> sources = new ArrayList<>();

		if (filters.isEmpty()) {
			return sources;
		}

		for (MBDMachineDefinition definition : MBDRegistries.MACHINE_DEFINITIONS) {
			ConfigMachineSettings settings = definition.machineSettings();

			if (settings == null) {
				continue;
			}

			for (TraitDefinition trait : settings.traitDefinitions()) {
				String name = trait.getName();

				if (name == null) {
					continue;
				}

				for (String filter : filters) {
					if (name.equals(filter) || name.startsWith(filter + "_")) {
						sources.add(trait);
						break;
					}
				}
			}
		}

		return sources;
	}

	private static boolean isGasTank(TraitDefinition trait) {
		try {
			return trait instanceof ChemicalTankCapabilityTraitDefinition<?, ?>;
		} catch (NoClassDefFoundError error) {
			// 没装 Mekanism
			return false;
		}
	}

	private static TraitDefinition itemTrait(IO io) {
		ItemSlotCapabilityTraitDefinition trait = new ItemSlotCapabilityTraitDefinition();

		trait.setName(ITEM_TRAIT);
		trait.setSlotSize(ITEM_SLOTS);
		trait.setSlotLimit(SLOT_LIMIT);
		applyIO(trait, io);

		return trait;
	}

	private static TraitDefinition fluidTrait(IO io) {
		FluidTankCapabilityTraitDefinition trait = new FluidTankCapabilityTraitDefinition();

		trait.setName(FLUID_TRAIT);
		trait.setTankSize(TANK_COUNT);
		trait.setCapacity(FLUID_SLOT_CAPACITY);
		trait.setAllowSameFluids(true);
		applyIO(trait, io);

		return trait;
	}

	private static TraitDefinition gasTrait(IO io) {
		ChemicalTankCapabilityTraitDefinition.Gas trait = new ChemicalTankCapabilityTraitDefinition.Gas();

		trait.setName(GAS_TRAIT);
		trait.setTankSize(TANK_COUNT);
		trait.setCapacity(GAS_SLOT_CAPACITY);
		applyIO(trait, io);

		return trait;
	}

	private static TraitDefinition energyTrait(IO io) {
		ForgeEnergyCapabilityTraitDefinition trait = new ForgeEnergyCapabilityTraitDefinition();

		trait.setName(ENERGY_TRAIT);
		trait.setCapacity(ENERGY_CAPACITY);
		trait.setMaxReceive(ENERGY_CAPACITY);
		trait.setMaxExtract(ENERGY_CAPACITY);
		applyIO(trait, io);

		return trait;
	}

	/**
	 * 配方 IO 按总线方向走, 但**界面 IO 一律 BOTH**。
	 * <p>
	 * 原来把 guiIO 也设成 IN 时, MBD2 会 {@code setCanTakeItems(guiIO.support(OUT))} = false ——
	 * 东西放得进总线却拿不出来。
	 */
	private static void applyIO(SimpleCapabilityTraitDefinition trait, IO io) {
		trait.setRecipeHandlerIO(io);
		trait.setGuiIO(IO.BOTH);

		// 对外的 capability 一律 BOTH: 管道要能双向, 机器 (自动输出) 也要能把产物推进来。
		// 只按总线方向设 IO 的话, 输出总线对外是"只出不进", 产物就永远进不来。
		CapabilityIO capabilityIO = trait.getCapabilityIO();

		capabilityIO.setInternal(IO.BOTH);
		capabilityIO.setFrontIO(IO.BOTH);
		capabilityIO.setBackIO(IO.BOTH);
		capabilityIO.setLeftIO(IO.BOTH);
		capabilityIO.setRightIO(IO.BOTH);
		capabilityIO.setTopIO(IO.BOTH);
		capabilityIO.setBottomIO(IO.BOTH);

		enableAutoIO(trait, io);
	}

	/**
	 * 打开 trait 定义上的"自动 IO" (ToggleAutoIO)。
	 * <p>
	 * MBD2 的机制 (IAutoIOTrait.serverTick): 每隔 interval tick, 按六面 IO 与相邻容器搬运 ——
	 * 物品/流体/气体/能量 trait 都实现了这个接口。输入总线六面 IN = 从旁边拉料进来,
	 * 输出总线六面 OUT = 把产物推出去。
	 * <p>
	 * 注意别跟 AutoWorldIO (autoInput / autoOutput) 混: 那个是把流体倒进世界、把物品丢出去。
	 * <p>
	 * 基类 SimpleCapabilityTraitDefinition 没有 getAutoIO, 所以走反射 (各个子类都有这个 getter)。
	 */
	private static void enableAutoIO(SimpleCapabilityTraitDefinition trait, IO io) {
		try {
			Object autoIO = trait.getClass().getMethod("getAutoIO").invoke(trait);

			if (autoIO == null) {
				return;
			}

			autoIO.getClass().getMethod("setEnable", boolean.class).invoke(autoIO, true);

			try {
				autoIO.getClass().getMethod("setInterval", int.class).invoke(autoIO, AUTO_IO_INTERVAL);
			} catch (ReflectiveOperationException ignored) {
				// 没有 interval 就用默认值
			}

			for (String face : new String[]{"setFrontIO", "setBackIO", "setLeftIO", "setRightIO", "setTopIO", "setBottomIO"}) {
				try {
					autoIO.getClass().getMethod(face, IO.class).invoke(autoIO, io);
				} catch (ReflectiveOperationException ignored) {
					// 少一个面不影响
				}
			}
		} catch (ReflectiveOperationException ignored) {
			// 这个 trait 没有自动 IO
		}
	}
}
