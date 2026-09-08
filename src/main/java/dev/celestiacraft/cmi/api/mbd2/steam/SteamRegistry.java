package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.mbd2.api.recipe.MBDRecipeType;
import com.lowdragmc.mbd2.api.registry.MBDRegistries;
import com.lowdragmc.mbd2.common.event.MBDRegistryEvent;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * 蒸汽机器注册表: 机器定义 id -> 蒸汽机器配置实例 (逻辑单例, 无持久化状态)。
 * 蒸汽数据都保存在机器的蒸汽槽 trait 里 (MB2 自己持久化), 这里只存配置与逻辑。
 */
public class SteamRegistry {
	private static final Map<ResourceLocation, AbstractSteamMachine<?>> MACHINES = new HashMap<>();

	static void register(AbstractSteamMachine<?> machine) {
		MACHINES.put(machine.getId(), machine);
	}

	public static AbstractSteamMachine<?> get(ResourceLocation id) {
		return MACHINES.get(id);
	}

	static AbstractSteamMachine<?> byMachine(MBDMachine machine) {
		return MACHINES.get(machine.getDefinition().id());
	}

	/**
	 * 在 MBDRegistryEvent.MBDRecipeType 里调用一次: 为所有蒸汽机器注册各自的配方类型
	 * 每台机器专业对口 (自己的 recipeTypeId), 不使用 MB2 燃料配方 (连续抽取方案)
	 *
	 * @param event
	 */
	public static void registerRecipeTypes(MBDRegistryEvent.MBDRecipeType event) {
		for (AbstractSteamMachine<?> machine : MACHINES.values()) {
			if (machine.getRecipeTypeId() == null) {
				continue;
			}
			MBDRecipeType type = new MBDRecipeType(machine.getRecipeTypeId());
			type.setRequireFuelForWorking(false);
			type.setXEIVisible(true);
			MBDRegistries.RECIPE_TYPES.register(machine.getRecipeTypeId(), type);
		}
	}

	/**
	 * 在 mod 构造器调用一次: 注册全局机器事件 (蒸汽抽取 / 恢复)。
	 */
	public static void init() {
		SteamMachineEvents.init();
	}
}