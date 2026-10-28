package dev.celestiacraft.cmi.event.mbd2.coke_oven;

import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.definition.config.event.MachineOnRecipeFinishEvent;
import com.lowdragmc.mbd2.common.machine.definition.config.event.MachineOnRecipeWorkingEvent;
import com.lowdragmc.mbd2.common.machine.definition.config.event.MachineRecipeModifyEvent;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.mbd2.MBDHelpers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 高级焦炉的运行时规则
 * <p>
 * 规则:
 * 1. 首次运行时, 所需时间是沉浸工程焦炉原配方时间的 4 倍, 且只有 1 并行
 * 2. 每完成一次配方, 所需时间 -20%, 并行 +1, 直到 12 并行
 * 3. 每完成一次配方, 炉温 +250, 上限 2500
 * 4. 炉温每提升 250, 所需时间 -25%(乘算), 2500 度时约为原来的 5.6%
 * 5. 以上累积只在机器持续运行时有效, 中途断开(连续 3 秒没有配方在工作)就全部清零
 * <p>
 * 状态存在机器的 customData 里, 跟着方块实体一起保存
 */
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CokeOvenRunning {
	// 机器 id, Jade 的 tooltip 也拿它判断是不是本机器
	public static final ResourceLocation MACHINE_ID = Cmi.loadResource("reinforced_coke_oven");

	/**
	 * 代理配方的所需时间系数
	 * <p>
	 * 那边的 duration 是沉浸工程焦炉原配方 time 的 0.5 倍
	 */
	private static final double PROXY_DURATION_SCALE = 0.5d;
	// 首次运行的所需时间倍率, 相对沉浸工程焦炉原配方 time
	private static final int BASE_MULTIPLIER = 4;
	// 每完成一次配方, 所需时间乘算的倍率
	private static final double TIME_STEP = 0.8;
	// 并行上限, Jade 的 tooltip 要拿它显示上限
	public static final int MAX_PARALLEL = 16;
	// 每完成一次配方提升的炉温
	private static final int HEAT_STEP = 250;
	// 炉温上限, Jade 的 tooltip 要拿它显示上限
	public static final int MAX_HEAT = 2500;
	// 炉温每提升这么多, 所需时间乘算一次
	private static final int HEAT_STAGE = 250;
	// 炉温每提升一档, 所需时间乘算的倍率
	private static final double HEAT_TIME_STEP = 0.75;
	// 所需时间下限
	private static final int MIN_DURATION = 100;

	/**
	 * 连续未工作的时间达到该值即视为断开, 累积状态全部清零
	 * <p>
	 * 这里量的是系统时间(毫秒), 不是机器被 tick 的次数.
	 * <p>
	 * 加速火把一类的方块会在同一个游戏 tick 里对机器重复调用 tick(),
	 * 若按机器被调用的次数计数, 一次配方收尾那几帧空档就会被放大成几十上百次,
	 * 于是每跑完一份都被判定成断开, 运行次数与炉温永远涨不上去.
	 * <p>
	 * 判定只在配方开工那一 tick 做一次(见 durationModify), 空档期间不去盯着看,
	 * 结果一样, 但省掉一个每 tick 都要跑一遍的监听.
	 */
	private static final long RESET_MILLIS = 3000L;

	// 自定义数据的键名
	private static final String DATA_RUNS = "runs";
	private static final String DATA_HEAT = "heat";
	private static final String DATA_LAST_WORK = "last_work";
	private static final String DATA_BASE = "base";

	/**
	 * 搜索配方时记下原始所需时间, 并按运行次数改写并行
	 *
	 * @param event 配方修改事件
	 */
	@SubscribeEvent
	public static void onRecipeModify(MachineRecipeModifyEvent.Before event) {
		MBDMachine machine = event.getMachine();

		if (!MBDHelpers.isMachine(machine, MACHINE_ID)) {
			return;
		}

		MBDRecipe recipe = event.getRecipe();

		if (recipe == null) {
			return;
		}

		setBaseDuration(machine, recipe.duration);
		event.setRecipe(parallelModify(machine, recipe));
	}

	/**
	 * 配方运行期间改写所需时间, 同时维护空档计时
	 *
	 * @param event 配方运行事件
	 */
	@SubscribeEvent
	public static void onRecipeWorking(MachineOnRecipeWorkingEvent event) {
		MBDMachine machine = event.getMachine();

		if (!MBDHelpers.isMachine(machine, MACHINE_ID)) {
			return;
		}

		durationModify(machine);
	}

	/**
	 * 完成一次配方, 累加运行次数与炉温
	 *
	 * @param event 配方完成事件
	 */
	@SubscribeEvent
	public static void onRecipeFinish(MachineOnRecipeFinishEvent event) {
		MBDMachine machine = event.getMachine();

		if (!MBDHelpers.isMachine(machine, MACHINE_ID)) {
			return;
		}

		// 完成一次配方, 运行次数 +1, 炉温跟着涨
		int runs = getRuns(machine) + 1;

		setRuns(machine, runs);
		setHeat(machine, Math.min(MAX_HEAT, runs * HEAT_STEP));

		// 配方刚跑完, 让空档从这一刻开始算, 免得下一次搜索配方的耗时被算成断开
		setLastWork(machine, System.currentTimeMillis());

		/*
		 * 让配方逻辑重新搜索一次配方.
		 *
		 * MBDMachine.doModifyRecipe 只在搜索配方时走一遍修改事件,
		 * 而 RecipeLogic.onRecipeFinish 之后只要 lastRecipe 还匹配就直接 setupRecipe(lastRecipe)
		 * 复用, 不重新修改配方. 不标记 dirty 的话并行数会一直停在开工时的那一档,
		 * 要等机器自己停下来才会更新.
		 */
		machine.getRecipeLogic().markLastRecipeDirty();
	}

	/**
	 * 按运行次数与炉温改写配方所需时间
	 *
	 * @param machine 目标机器
	 */
	private static void durationModify(MBDMachine machine) {
		RecipeLogic logic = machine.getRecipeLogic();

		/*
		 * 空档计时挂在配方运行事件上, 不单独开每 tick 的监听.
		 *
		 * 拿系统时间做差, 不受加速火把在同一个 gameTick 里重复调用 tick() 的影响:
		 * 重复调用的那几次, 毫秒数基本是同一个值, 不会被放大成几十上百个空档.
		 *
		 * 而配方跑起来之后本方法每 tick 都会被调到, 拿它当时钟用就够了.
		 */
		long now = System.currentTimeMillis();

		// 开工首 tick 先看这一次是不是接在上一次后面
		if (logic.getProgress() == 0) {
			resetIfBroken(machine, now);
		}

		if (getLastWork(machine) != now) {
			setLastWork(machine, now);
		}

		/*
		 * 若已经开始运行则不再修改
		 * 不然会出现每 Tick 都做一次修改的现象
		 * 甚至能看到配方时间越来越多甚至突破 int 上限
		 */
		if (logic.getProgress() > 0) {
			return;
		}

		/*
		 * 所需时间的原始值从搜索配方时记下的基准上取, 不用 logic.getDuration().
		 *
		 * setupRecipe 会把 duration 设成 recipe.duration, 但这个值被本方法改过之后
		 * 就不再是原始值了: 机器等材料不足时 RecipeLogic.doDamping 会把 progress
		 * 扣回 0, 材料补上后本方法会被再调用一次. 若拿被改过的值当基准再算一遍,
		 * 时间会一层层翻倍. 原始配方永远不含本类的修改, 用它当基准才算得幂等.
		 */
		int base = getBaseDuration(machine);

		// 基准为 0 时说明没拿到原始配方时间, 直接跳过, 配方按原时长走
		if (base <= 0) {
			return;
		}

		int target = calculateDuration(base, getRuns(machine), getHeat(machine));

		if (target == logic.getDuration()) {
			return;
		}

		logic.setDuration(target);
	}

	/**
	 * 按运行次数改写并行数量
	 *
	 * @param machine 目标机器
	 * @param recipe  原始配方
	 * @return 放大内容之后的配方
	 */
	private static MBDRecipe parallelModify(MBDMachine machine, MBDRecipe recipe) {
		// 首次运行为 1 并行, 每完成一次配方 +1
		int parallel = getCurrentParallel(machine);

		if (parallel <= 1) {
			return recipe;
		}

		/*
		 * applyParallel 内部走 MBDRecipe.accurateParallel, 按材料实际能满足的最大并行
		 * 降级, 材料不足时不会返回 null, 顶多是并行 1.
		 */
		return machine.applyParallel(recipe, parallel);
	}

	/**
	 * 计算当前所需时间
	 *
	 * @param duration 代理配方的原始所需时间
	 * @param runs     已完成的配方次数
	 * @param heat     当前炉温
	 * @return 本次配方所需时间
	 */
	private static int calculateDuration(int duration, int runs, int heat) {
		// 代理配方是原配方时间的一半, 先还原成原配方时间, 再乘上首次运行的倍率
		double time = duration / PROXY_DURATION_SCALE * BASE_MULTIPLIER;

		// 每完成一次配方乘算一次
		time *= StrictMath.pow(TIME_STEP, runs);

		// 炉温每提升一档再乘算一次
		time *= StrictMath.pow(HEAT_TIME_STEP, (double) heat / HEAT_STAGE);

		return Math.max(MIN_DURATION, (int) Math.round(time));
	}

	/**
	 * 空档太久就把累积状态清零
	 * <p>
	 * 机器的累积状态只在连续运行时有效, 中间停下超过 RESET_MILLIS 就该从头再来.
	 *
	 * @param machine 目标机器
	 * @param now     当前毫秒时间戳
	 */
	private static void resetIfBroken(MBDMachine machine, long now) {
		// 空档还没到判定值, 累积状态先保留
		if (now - getLastWork(machine) < RESET_MILLIS) {
			return;
		}

		// 已经是空状态, 不用再写一次
		if (getRuns(machine) == 0 && getHeat(machine) == 0) {
			return;
		}

		// 中途断开过, 运行次数与炉温一起清零
		setRuns(machine, 0);
		setHeat(machine, 0);
	}

	/**
	 * 取机器的自定义数据
	 *
	 * @param machine 目标机器
	 * @return 自定义数据
	 */
	private static CompoundTag getMachineData(MBDMachine machine) {
		return machine.getCustomData();
	}

	/**
	 * 读取已完成的配方次数
	 *
	 * @param machine 目标机器
	 * @return 运行次数
	 */
	public static int getRuns(MBDMachine machine) {
		return getMachineData(machine).getInt(DATA_RUNS);
	}

	/**
	 * 写入已完成的配方次数
	 *
	 * @param machine 目标机器
	 * @param runs    运行次数
	 */
	private static void setRuns(MBDMachine machine, int runs) {
		getMachineData(machine).putInt(DATA_RUNS, runs);
	}

	/**
	 * 读取当前炉温
	 *
	 * @param machine 目标机器
	 * @return 炉温
	 */
	public static int getHeat(MBDMachine machine) {
		return getMachineData(machine).getInt(DATA_HEAT);
	}

	/**
	 * 写入当前炉温
	 *
	 * @param machine 目标机器
	 * @param heat    炉温
	 */
	private static void setHeat(MBDMachine machine, int heat) {
		getMachineData(machine).putInt(DATA_HEAT, heat);
	}

	/**
	 * 取当前的并行数量
	 * <p>
	 * 首次运行为 1 并行, 每完成一次配方 +1, 到 MAX_PARALLEL 封顶.
	 * <p>
	 * 实际开出的并行还会被材料数量再压一次(见 parallelModify), 这里给的是目标值.
	 *
	 * @param machine 目标机器
	 * @return 目标并行数量
	 */
	public static int getCurrentParallel(MBDMachine machine) {
		return Math.min(MAX_PARALLEL, getRuns(machine) + 1);
	}

	/**
	 * 读取最后一次处于工作状态的系统时间(毫秒)
	 *
	 * @param machine 目标机器
	 * @return 毫秒时间戳
	 */
	private static long getLastWork(MBDMachine machine) {
		return getMachineData(machine).getLong(DATA_LAST_WORK);
	}

	/**
	 * 写入最后一次处于工作状态的系统时间
	 *
	 * @param machine 目标机器
	 * @param time    毫秒时间戳
	 */
	private static void setLastWork(MBDMachine machine, long time) {
		getMachineData(machine).putLong(DATA_LAST_WORK, time);
	}

	/**
	 * 读取代理配方的原始所需时间
	 *
	 * @param machine 目标机器
	 * @return 原始所需时间
	 */
	private static int getBaseDuration(MBDMachine machine) {
		return getMachineData(machine).getInt(DATA_BASE);
	}

	/**
	 * 写入代理配方的原始所需时间
	 *
	 * @param machine  目标机器
	 * @param duration 原始所需时间
	 */
	private static void setBaseDuration(MBDMachine machine, int duration) {
		getMachineData(machine).putInt(DATA_BASE, duration);
	}
}