package dev.celestiacraft.cmi.compat.jade.provider.mbd;

import com.lowdragmc.mbd2.api.machine.IMachine;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import dev.celestiacraft.cmi.compat.jade.CmiType;
import dev.celestiacraft.cmi.compat.mbd2.MBDHelpers;
import dev.celestiacraft.cmi.event.mbd2.coke_oven.CokeOvenRunning;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

/**
 * 高级焦炉的 Jade tooltip
 * <p>
 * 显示炉温、目标并行、已完成的配方次数, 以及正在运行的配方还要多久.
 * <p>
 * 这些数据存在机器的 customData 里, 客户端拿不到 MBDMachine, 所以按 Jade 的常规做法
 * 由 IServerDataProvider 在服务端读出来塞进 ServerData, 客户端只负责画.
 */
public enum CokeOvenProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	INSTANCE;

	// 塞进 ServerData 的键名, 全部带前缀, 免得和别的 provider 撞
	private static final String DATA_MATCHED = "cmi_coke_oven";
	private static final String DATA_HEAT = "cmi_coke_oven_heat";
	private static final String DATA_RUNS = "cmi_coke_oven_runs";
	private static final String DATA_PARALLEL = "cmi_coke_oven_parallel";
	private static final String DATA_DURATION = "cmi_coke_oven_duration";
	private static final String DATA_PROGRESS = "cmi_coke_oven_progress";

	@Override
	public ResourceLocation getUid() {
		return CmiType.COMMON;
	}

	/**
	 * 服务端把炉温一类的运行时数据写进 ServerData
	 *
	 * @param data     要发给客户端的 ServerData
	 * @param accessor 方块访问器
	 */
	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		MBDMachine machine = getMachine(accessor);

		// 不是焦炉就别往里写, 客户端那边靠这个键判断要不要显示
		if (machine == null) {
			return;
		}

		data.putBoolean(DATA_MATCHED, true);
		data.putInt(DATA_HEAT, CokeOvenRunning.getHeat(machine));
		data.putInt(DATA_RUNS, CokeOvenRunning.getRuns(machine));
		data.putInt(DATA_PARALLEL, CokeOvenRunning.getCurrentParallel(machine));

		RecipeLogic logic = machine.getRecipeLogic();

		// 没在跑配方就不带进度, 免得 tooltip 上多出一行 0 秒
		if (logic.isWorking()) {
			data.putInt(DATA_DURATION, logic.getDuration());
			data.putInt(DATA_PROGRESS, logic.getProgress());
		}
	}

	/**
	 * 客户端读 ServerData 画 tooltip
	 *
	 * @param tooltip  tooltip
	 * @param accessor 方块访问器
	 * @param config   插件配置
	 */
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		CompoundTag data = accessor.getServerData();

		// 服务端没标过, 说明看的不是焦炉
		if (!data.getBoolean(DATA_MATCHED)) {
			return;
		}

		int heat = data.getInt(DATA_HEAT);
		int runs = data.getInt(DATA_RUNS);
		int parallel = data.getInt(DATA_PARALLEL);

		tooltip.add(line("jade.tip.cmi.coke_oven.heat", heatText(heat), number(CokeOvenRunning.MAX_HEAT)));
		tooltip.add(line("jade.tip.cmi.coke_oven.parallel", number(parallel), number(CokeOvenRunning.MAX_PARALLEL)));
		tooltip.add(line("jade.tip.cmi.coke_oven.runs", number(runs)));

		if (data.contains(DATA_DURATION)) {
			int remain = Math.max(0, data.getInt(DATA_DURATION) - data.getInt(DATA_PROGRESS));

			// 20 tick 算一秒
			tooltip.add(line("jade.tip.cmi.coke_oven.time", number(String.format("%.1f", remain / 20.0d))));
		}
	}

	/**
	 * 从方块实体拿到对应的机器
	 *
	 * @param accessor 方块访问器
	 * @return 焦炉的机器实例, 不是焦炉则为 null
	 */
	private static MBDMachine getMachine(BlockAccessor accessor) {
		Optional<IMachine> optional = IMachine.ofMachine(accessor.getBlockEntity());

		if (optional.isEmpty()) {
			return null;
		}

		if (!(optional.get() instanceof MBDMachine machine)) {
			return null;
		}

		if (!MBDHelpers.isMachine(machine, CokeOvenRunning.MACHINE_ID)) {
			return null;
		}

		return machine;
	}

	private static MutableComponent line(String key, Object... args) {
		return Component.translatable(key, args)
				.withStyle(ChatFormatting.GRAY);
	}

	private static MutableComponent number(int value) {
		return number(String.valueOf(value));
	}

	private static MutableComponent number(String value) {
		return Component.literal(value)
				.withStyle(ChatFormatting.GOLD);
	}

	/**
	 * 炉温按高低换色, 冷的时候偏黄, 满温偏红
	 *
	 * @param heat 当前炉温
	 * @return 带颜色的炉温文本
	 */
	private static MutableComponent heatText(int heat) {
		float ratio = Mth.clamp((float) heat / CokeOvenRunning.MAX_HEAT, 0.0f, 1.0f);
		int color = Mth.hsvToRgb(0.12f * (1.0f - ratio), 0.85f, 1.0f);

		return Component.literal(String.valueOf(heat))
				.withStyle((style) -> {
					return style.withColor(color);
				});
	}
}