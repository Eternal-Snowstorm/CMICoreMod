package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.mbd2.api.registry.MBDRegistries;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.MultiblockMachineDefinition;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.mbd2.UISpec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * 统一界面: 给所有机器套上 UISpec 版式, 不再一台一台写。
 *
 * <ul>
 *     <li>多方块控制器 -> {@link UISpec#controller} (上半大屏幕 + 下半玩家物品栏)</li>
 *     <li>单方块 / 总线 -> {@link UISpec#bus} (一堆格子 + 下半玩家物品栏)</li>
 * </ul>
 *
 * <h2>为什么要在两个时机各跑一次</h2>
 * MBD2 的机器注册发生在 {@code FMLConstructModEvent} 的 enqueueWork 里, KubeJS 的机器还要更晚
 * (在 MBD2 post 出来的 MBDRegistryEvents.machine 事件里)。这两个时机都可能晚于 common setup,
 * 所以 {@link #applyAll()} 在 common setup 与 {@code FMLLoadCompleteEvent} 各跑一次 —— 幂等,
 * 第二次只补第一次没赶上的。
 * <p>
 * 漏掉的后果不是"少个界面"而是**右键直接 NPE**: {@code MBDMachine.createUI} 会直接
 * {@code getDefinition().uiCreator().apply(machine)}, uiCreator 为 null 就炸在服务端。
 *
 * <p>优先级: JS / Java 里显式 {@code MBDUI.register} 过的机器保持自己的版式, 这里不碰。
 * 只处理 {@code cmi:} 命名空间的机器。
 */
public class MachineUIKit {
	/** 只管这个命名空间 */
	public static final String NAMESPACE = Cmi.MODID;

	/**
	 * 把所有机器套上统一界面。幂等, 可重复调用 (common setup / load complete 各一次)。
	 *
	 * @return 本次新套上的机器数
	 */
	public static int applyAll() {
		// JS 的 register 可能跑在机器注册之前, 那次 attach 是空转 —— 先补挂一遍
		MBDUI.reattachAll();

		int applied = 0;
		int skipped = 0;
		int failed = 0;

		for (Map.Entry<ResourceLocation, MBDMachineDefinition> entry : MBDRegistries.MACHINE_DEFINITIONS.entries()) {
			ResourceLocation id = entry.getKey();

			if (id == null || !NAMESPACE.equals(id.getNamespace())) {
				continue;
			}

			try {
				if (apply(id, entry.getValue())) {
					applied++;
				} else {
					skipped++;
				}
			} catch (Throwable error) {
				failed++;
				Cmi.LOGGER.error("[MachineUIKit] {} 套界面失败 (这台机器点开会 NPE)", id, error);
			}
		}

		Cmi.LOGGER.info(
				"[MachineUIKit] cmi 机器 {} 台: 本次套上 {} 台, 已有自己的界面 {} 台, 失败 {} 台",
				applied + skipped + failed,
				applied,
				skipped,
				failed
		);

		verify();

		return applied;
	}

	/**
	 * 自检: 凡是 hasUI 的 cmi 机器, uiCreator 都不能是 null (null 的话右键 = 服务端 NPE)。
	 * <p>
	 * 这条日志就是用来抓"时机没赶上"的: 下次点机器报 NPE, 直接看这行有没有点名。
	 */
	private static void verify() {
		int missing = 0;

		for (Map.Entry<ResourceLocation, MBDMachineDefinition> entry : MBDRegistries.MACHINE_DEFINITIONS.entries()) {
			ResourceLocation id = entry.getKey();
			MBDMachineDefinition definition = entry.getValue();

			if (id == null || !NAMESPACE.equals(id.getNamespace()) || definition == null || definition.machineSettings() == null) {
				continue;
			}

			if (!definition.machineSettings().hasUI()) {
				continue;
			}

			if (MBDHelpers.getPrivateField(definition, "uiCreator") == null) {
				missing++;
				Cmi.LOGGER.error("[MachineUIKit] {} 开了界面但 uiCreator 是 null —— 点它会 NPE", id);
			}
		}

		if (missing == 0) {
			Cmi.LOGGER.info("[MachineUIKit] 自检通过: 所有开界面的机器都挂上 uiCreator 了");
		}
	}

	/**
	 * 给一台机器套统一界面。
	 *
	 * @return 是否本次真的套上了 (已经有自己的界面 / 已套过 -> false)
	 */
	public static boolean apply(ResourceLocation id, MBDMachineDefinition definition) {
		if (id == null || definition == null || definition.machineSettings() == null) {
			return false;
		}

		if (MBDUI.hasUI(id.toString())) {
			// JS / Java 自己登记过界面 (比如蒸汽机要显示聚合水位条), 尊重它
			return false;
		}

		boolean multiblock = definition instanceof MultiblockMachineDefinition;

		// 总线: 先补自己的储物槽 (箱子)
		BusKit.addStorage(id, definition);

		MBDHelpers.setPrivateField(definition.machineSettings(), "hasUI", true);

		MBDUI.registerInternal(id, multiblock ? UISpec::controller : UISpec::bus);

		Cmi.LOGGER.info("[MachineUIKit] {} -> {}", id, multiblock ? "控制器版式 (屏幕 + 物品栏)" : "总线版式 (格子 + 物品栏)");
		return true;
	}
}
