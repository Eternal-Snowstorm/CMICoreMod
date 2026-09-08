package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.mbd2.common.machine.definition.config.event.MachineOnRecipeWorkingEvent;
import com.lowdragmc.mbd2.common.machine.definition.config.event.MachineTickEvent;
import com.lowdragmc.mbd2.integration.kubejs.events.MBDMachineEvents;
import com.lowdragmc.mbd2.integration.kubejs.events.MBDServerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;

/**
 * 蒸汽机器全局事件: 工作 tick 抽蒸汽 / 蒸汽不足暂停 / 蒸汽恢复继续。
 * 由 SteamRegistry.init() 一次性注册。
 */
public class SteamMachineEvents {
	private static boolean initialized;

	static void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		MBDServerEvents.ON_RECIPE_WORKING.listenJava(ScriptType.SERVER, SteamMachineEvents.class, (event) -> {
			MachineOnRecipeWorkingEvent e = ((MBDMachineEvents.MachineEventJS<MachineOnRecipeWorkingEvent>) event).getEvent();
			handleWorking(e);
			return null;
		});

		MBDServerEvents.TICK.listenJava(ScriptType.SERVER, SteamMachineEvents.class, (event) -> {
			MachineTickEvent e = ((MBDMachineEvents.MachineEventJS<MachineTickEvent>) event).getEvent();
			handleTick(e);
			return null;
		});
	}

	private static void handleWorking(MachineOnRecipeWorkingEvent event) {
		AbstractSteamMachine<?> steam = SteamRegistry.byMachine(event.getMachine());
		if (steam == null) {
			return;
		}
		steam.consumeSteamWhileWorking(event.getMachine(), event.getRecipe());
	}

	private static void handleTick(MachineTickEvent event) {
		AbstractSteamMachine<?> steam = SteamRegistry.byMachine(event.getMachine());
		if (steam == null) {
			return;
		}
		steam.tryResume(event.getMachine());
	}
}