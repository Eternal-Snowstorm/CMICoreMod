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
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
	/** 流体 / 气体总线单罐容量 (mB) */
	public static final int TANK_CAPACITY = 16000;
	/** 能量总线缓存 (FE) */
	public static final int ENERGY_CAPACITY = 1_000_000;

	private static final Set<ResourceLocation> CONVERTED = new HashSet<>();

	/** 这台机器算不算总线 */
	public static boolean isBus(ResourceLocation id) {
		return id != null && id.getPath().endsWith(BUS_SUFFIX);
	}

	/**
	 * 给总线补储物槽 (幂等, 按 id 记账)。
	 *
	 * @return 是否真的加了东西
	 */
	public static boolean addStorage(ResourceLocation id, MBDMachineDefinition definition) {
		if (!isBus(id) || definition == null || CONVERTED.contains(id)) {
			return false;
		}

		ConfigMachineSettings settings = definition.machineSettings();

		if (settings == null) {
			return false;
		}

		IO io = directionOf(definition);

		boolean item = false;
		boolean fluid = false;
		boolean gas = false;
		boolean energy = false;

		for (TraitDefinition source : sourcesOf(filtersOf(definition))) {
			if (source instanceof ItemSlotCapabilityTraitDefinition) {
				item = true;
			} else if (source instanceof FluidTankCapabilityTraitDefinition) {
				fluid = true;
			} else if (isGasTank(source)) {
				gas = true;
			} else if (source instanceof ForgeEnergyCapabilityTraitDefinition) {
				energy = true;
			}
		}

		// 认不出角色 (没有代理 / 代理没写过滤器) 就当普通物品总线
		if (!item && !fluid && !gas && !energy) {
			item = true;
		}

		if (item) {
			settings.addTraitDefinition(itemTrait(io));
		}

		if (fluid) {
			settings.addTraitDefinition(fluidTrait(io));
		}

		if (gas) {
			settings.addTraitDefinition(gasTrait(io));
		}

		if (energy) {
			settings.addTraitDefinition(energyTrait(io));
		}

		CONVERTED.add(id);
		return true;
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
		trait.setTankSize(1);
		trait.setCapacity(TANK_CAPACITY);
		trait.setAllowSameFluids(true);
		applyIO(trait, io);

		return trait;
	}

	private static TraitDefinition gasTrait(IO io) {
		ChemicalTankCapabilityTraitDefinition.Gas trait = new ChemicalTankCapabilityTraitDefinition.Gas();

		trait.setName(GAS_TRAIT);
		trait.setTankSize(1);
		trait.setCapacity(TANK_CAPACITY);
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

	/** 配方 IO / 界面 IO / 六面 capability 全部对齐 (总线方向由总管决定) */
	private static void applyIO(SimpleCapabilityTraitDefinition trait, IO io) {
		trait.setRecipeHandlerIO(io);
		trait.setGuiIO(io);

		CapabilityIO capabilityIO = trait.getCapabilityIO();

		capabilityIO.setInternal(io);
		capabilityIO.setFrontIO(io);
		capabilityIO.setBackIO(io);
		capabilityIO.setLeftIO(io);
		capabilityIO.setRightIO(io);
		capabilityIO.setTopIO(io);
		capabilityIO.setBottomIO(io);
	}
}
