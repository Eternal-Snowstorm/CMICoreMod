package dev.celestiacraft.cmi.compat.mbd2;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.mbd2.api.registry.MBDRegistries;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 机器界面的唯一入口。JS / Java 都能用, 但**只写在 startup_scripts**。
 *
 * <h2>JS 怎么写</h2>
 * <pre>{@code
 * MBDUI.register("cmi:xxx", (machine) => UISpec.create(machine, 176, 166, (ui) => {
 *     ui.background("ldlib:textures/gui/background.png")
 *       .title(8, 6)
 *       .slot("xxx_input_item_slot", 40, 42)
 *       .progressBar(79, 42)
 * }))
 * }</pre>
 *
 * <h2>为什么只能是 startup</h2>
 * LDLib 的机器界面是两侧各自建树的:
 * 服务端 {@code MBDMachine.createUI()} -> writeInitialData 发包;
 * 客户端 {@code UIFactory.initClientUI()} 用同一个 uiCreator 再建一次树 -> readInitialData 回填。
 * 两边结构必须一致, 所以客户端也必须拿得到这个工厂函数。
 * startup 脚本两侧都跑, server_scripts 只有服务端跑 -> 写在 server 里, 联机时客户端那棵树是空的(黑屏)。
 *
 * <h2>热重载</h2>
 * uiCreator 只挂一次, 挂的是「每次开界面都重新查表」的 Java lambda,
 * 所以 {@code /kubejs reload startup_scripts} 后 register 覆盖掉工厂, 下次开界面就是新的。
 * 机器定义本身不享受这个待遇: 它只在 construct 阶段注册一次, 而且 registry 已经 freeze, 改完要重启。
 */
public class MBDUI {
	/** 工厂没写 / 返回 null 时的兜底尺寸 */
	private static final int FALLBACK_WIDTH = 176;
	private static final int FALLBACK_HEIGHT = 166;

	/** machineId -> 界面工厂。每次开界面现查, 所以 reload 覆盖即可生效 */
	private static final Map<ResourceLocation, Function<MBDMachine, WidgetGroup>> FACTORIES = new ConcurrentHashMap<>();

	private static volatile boolean registrationClosed = false;

	public static void register(ResourceLocation machineId, Function<MBDMachine, WidgetGroup> factory) {
		if (machineId == null) {
			throw new IllegalArgumentException("MBDUI.register: 机器 id 不合法");
		}

		if (factory == null) {
			FACTORIES.remove(machineId);
			return;
		}

		// 注册期之后才第一次注册 -> 大概率是写在 server_scripts / client_scripts 里了
		// (/kubejs reload 导致的重复注册不算, 那种是覆盖)
		if (registrationClosed && !FACTORIES.containsKey(machineId)) {
			Cmi.LOGGER.warn(
					"MBDUI.register({}) 在注册期之后才被调用。如果这行写在 server_scripts / client_scripts 里: "
							+ "单人/局域网能跑, 但专用服务器 + 独立客户端时客户端没有这份工厂 -> 界面是空的。"
							+ "请把它放到 startup_scripts。",
					machineId
			);
		}

		FACTORIES.put(machineId, factory);
		attach(machineId);
	}

	public static boolean hasUI(String machineId) {
		ResourceLocation id = ResourceLocation.tryParse(machineId);

		return id != null && FACTORIES.containsKey(id);
	}

	/**
	 * 注册期结束后统一挂载, 由 {@code Cmi#onCommonSetup} 调用。
	 * 首次启动时 JS 的 register 跑在机器定义注册之前, 所以必须在这里补挂一次。
	 */
	public static void attachAll() {
		registrationClosed = true;

		for (ResourceLocation machineId : FACTORIES.keySet()) {
			if (!attach(machineId)) {
				Cmi.LOGGER.warn("MBDUI: 找不到机器 {} 的定义, 界面没挂上 (id 写错了? 还是机器压根没注册?)", machineId);
			}
		}
	}

	/**
	 * 把 definition 的 uiCreator 换成「查表 + 兜底占位」的 Java lambda (uiCreator 没有 setter, 只能反射)。
	 *
	 * @return 定义不存在时返回 false
	 */
	public static boolean attach(ResourceLocation machineId) {
		MBDMachineDefinition definition = MBDRegistries.MACHINE_DEFINITIONS.get(machineId);

		if (definition == null) {
			return false;
		}

		MBDHelpers.setPrivateField(definition, "uiCreator", (Function<MBDMachine, WidgetGroup>) (machine) -> {
			Function<MBDMachine, WidgetGroup> factory = FACTORIES.get(machineId);

			if (factory == null) {
				return new WidgetGroup(0, 0, FALLBACK_WIDTH, FALLBACK_HEIGHT);
			}

			WidgetGroup group = factory.apply(machine);
			return group != null ? group : new WidgetGroup(0, 0, FALLBACK_WIDTH, FALLBACK_HEIGHT);
		});

		return true;
	}
}