package dev.celestiacraft.cmi.common.block.coil;

import com.lowdragmc.mbd2.api.blockentity.ProxyPartBlockEntity;
import com.lowdragmc.mbd2.api.pattern.MultiblockState;
import com.lowdragmc.mbd2.api.recipe.RecipeLogic;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.machine.MBDMultiblockMachine;
import com.lowdragmc.mbd2.common.machine.definition.config.event.*;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoilBlock extends BasicBlock {
	public static final BooleanProperty FEVER = BooleanProperty.create("fever");
	public static final BooleanProperty FORMED = BooleanProperty.create("formed");
	private static final int COOLDOWN_DELAY = 5;
	private static final Map<MBDMachine, Set<BlockPos>> MACHINE_COILS = new WeakHashMap<>();
	private static final Map<Level, Map<BlockPos, Long>> PENDING_COOLDOWN = new WeakHashMap<>();

	private static boolean updating;

	public CoilBlock(Properties properties) {
		super(properties.strength(3, 3)
				.requiresCorrectToolForDrops()
				.mapColor(MapColor.METAL)
				.sound(SoundType.METAL));
		registerDefaultState(defaultBlockState()
				.setValue(FORMED, false)
				.setValue(FEVER, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FEVER);
		builder.add(FORMED);
	}

	@SubscribeEvent
	public static void onStructureFormed(MachineStructureFormedEvent event) {
		MBDMachine machine = event.getMachine();

		if (updating || !(machine instanceof MBDMultiblockMachine multiblock)) {
			return;
		}

		Level level = machine.getLevel();

		if (level == null || level.isClientSide()) {
			return;
		}

		boolean fever = machine.getRecipeLogic().getStatus().equals(RecipeLogic.Status.WORKING);
		Set<BlockPos> positions = markFormed(level, multiblock, fever);

		if (!positions.isEmpty()) {
			MACHINE_COILS.put(machine, positions);
		}
	}

	@SubscribeEvent
	public static void onRecipeWorking(MachineOnRecipeWorkingEvent event) {
		setCoils(event.getMachine(), true);
	}

	@SubscribeEvent
	public static void onRecipeFinish(MachineOnRecipeFinishEvent event) {
		setCoils(event.getMachine(), false);
	}

	@SubscribeEvent
	public static void onRecipeStatusChanged(MachineRecipeStatusChangedEvent event) {
		if (!event.getNewStatus().equals(RecipeLogic.Status.WORKING)) {
			setCoils(event.getMachine(), false);
		}
	}

	@SubscribeEvent
	public static void onStructureInvalid(MachineStructureInvalidEvent event) {
		clearCoils(event.getMachine());
	}

	@SubscribeEvent
	public static void onMachineRemoved(MachineRemovedEvent event) {
		clearCoils(event.getMachine());
	}

	/**
	 * 处理延迟生效的降温请求
	 * <p>
	 * formed && fever 的线圈收到的降温请求会被推迟 {@link #COOLDOWN_DELAY} tick, 在这里统一结算
	 */
	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.level.isClientSide) {
			return;
		}

		Map<BlockPos, Long> pending = PENDING_COOLDOWN.get(event.level);

		if (pending == null || pending.isEmpty()) {
			return;
		}

		long time = event.level.getGameTime();
		updating = true;

		try {
			Iterator<Map.Entry<BlockPos, Long>> iterator = pending.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<BlockPos, Long> entry = iterator.next();

				if (entry.getValue() > time) {
					continue;
				}

				iterator.remove();
				applyCooldown(event.level, entry.getKey());
			}
		} finally {
			updating = false;
		}

		if (pending.isEmpty()) {
			PENDING_COOLDOWN.remove(event.level);
		}
	}

	/**
	 * 配方状态变化: 只改 fever, formed 保持 true
	 */
	private static void setCoils(MBDMachine machine, boolean fever) {
		if (updating || !(machine instanceof MBDMultiblockMachine multiblock)) {
			return;
		}

		Level level = machine.getLevel();

		if (level == null || level.isClientSide) {
			return;
		}

		Set<BlockPos> positions = MACHINE_COILS.get(machine);

		if (positions == null) {
			// 没有记录: 只有确实成型了才值得回头扫一遍结构缓存
			if (!multiblock.isFormed()) {
				return;
			}

			Set<BlockPos> collected = markFormed(level, multiblock, fever);

			if (!collected.isEmpty()) {
				MACHINE_COILS.put(machine, collected);
			}

			return;
		}

		updating = true;

		try {
			for (BlockPos pos : positions) {
				setCoil(level, pos, true, fever);
			}
		} finally {
			updating = false;
		}
	}

	/**
	 * 结构失效 / 机器被移除: 线圈立刻回到未成型状态, 并取消挂起的降温
	 */
	private static void clearCoils(MBDMachine machine) {
		Set<BlockPos> positions = MACHINE_COILS.remove(machine);

		if (updating || !(machine instanceof MBDMultiblockMachine multiblock)) {
			return;
		}

		Level level = machine.getLevel();

		if (level == null || level.isClientSide) {
			return;
		}

		// 没有记录时退回到结构缓存兜底
		Collection<BlockPos> targets = positions == null ? getCachedPositions(multiblock) : positions;

		updating = true;

		try {
			for (BlockPos pos : targets) {
				cancelCooldown(level, pos);
				setCoil(level, pos, false, false);
			}
		} finally {
			updating = false;
		}
	}

	/**
	 * 把结构缓存里的线圈全部标记为成型, 并按参数设置 fever
	 *
	 * @return 实际被处理的线圈位置
	 */
	private static Set<BlockPos> markFormed(Level level, MBDMultiblockMachine multiblock, boolean fever) {
		updating = true;

		try {
			return collectCoils(level, multiblock, fever);
		} finally {
			updating = false;
		}
	}

	private static Set<BlockPos> collectCoils(Level level, MBDMultiblockMachine multiblock, boolean fever) {
		Set<BlockPos> positions = new HashSet<>();

		for (BlockPos pos : getCachedPositions(multiblock)) {
			if (setCoil(level, pos, true, fever)) {
				positions.add(pos);
			}
		}

		return positions;
	}

	private static Collection<BlockPos> getCachedPositions(MBDMultiblockMachine machine) {
		MultiblockState state = machine.getMultiblockState();

		if (state == null || state.cache == null) {
			return List.of();
		}

		return state.getCache();
	}

	/**
	 * 设置某个位置线圈的 formed / fever
	 *
	 * @return 该位置确实是一个线圈方块时返回 true
	 */
	private static boolean setCoil(Level level, BlockPos pos, boolean formed, boolean fever) {
		BlockState state = getCoilState(level, pos);

		if (state == null) {
			return false;
		}

		// formed 先落地: 结构失效时 formed 立刻变 false, 紧随其后的降温就不会再走延迟
		if (state.getValue(FORMED) != formed) {
			state = state.setValue(FORMED, formed);
			applyCoilState(level, pos, state);
		}

		setFever(level, pos, state, fever);

		return true;
	}

	/**
	 * 设置 fever
	 * <p>
	 * 当线圈当前为 formed && fever 时, 降温会延后 {@link #COOLDOWN_DELAY} tick 生效, 期间重新升温则取消
	 */
	private static void setFever(Level level, BlockPos pos, BlockState state, boolean fever) {
		if (fever) {
			cancelCooldown(level, pos);

			if (!state.getValue(FEVER)) {
				applyCoilState(level, pos, state.setValue(FEVER, true));
			}

			return;
		}

		if (!state.getValue(FEVER)) {
			cancelCooldown(level, pos);
			return;
		}

		// 成型且正在发热: 先把降温挂起来, 避免连续配方之间来回抖
		if (state.getValue(FORMED)) {
			scheduleCooldown(level, pos);
			return;
		}

		applyCoilState(level, pos, state.setValue(FEVER, false));
	}

	/**
	 * 读取该位置线圈当前的状态, 不是线圈时返回 {@code null}
	 * <p>
	 * 被 MBD2 换成代理部件的线圈, 状态存在 BlockEntity 里
	 */
	private static BlockState getCoilState(Level level, BlockPos pos) {
		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof ProxyPartBlockEntity proxy) {
			BlockState original = proxy.getOriginalState();

			return original != null && original.getBlock() instanceof CoilBlock ? original : null;
		}

		BlockState state = level.getBlockState(pos);

		return state.getBlock() instanceof CoilBlock ? state : null;
	}

	/**
	 * 把状态写回该位置
	 */
	private static void applyCoilState(Level level, BlockPos pos, BlockState state) {
		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof ProxyPartBlockEntity proxy) {
			proxy.setOriginalData(state, proxy.getOriginalData(), proxy.getControllerPos());
			return;
		}

		level.setBlockAndUpdate(pos, state);
	}

	private static void scheduleCooldown(Level level, BlockPos pos) {
		PENDING_COOLDOWN.computeIfAbsent(level, (key) -> {
			return new HashMap<>();
		}).put(pos.immutable(), level.getGameTime() + COOLDOWN_DELAY);
	}

	private static void cancelCooldown(Level level, BlockPos pos) {
		Map<BlockPos, Long> pending = PENDING_COOLDOWN.get(level);

		if (pending == null) {
			return;
		}

		pending.remove(pos);

		if (pending.isEmpty()) {
			PENDING_COOLDOWN.remove(level);
		}
	}

	private static void applyCooldown(Level level, BlockPos pos) {
		BlockState state = getCoilState(level, pos);

		if (state == null || !state.getValue(FORMED) || !state.getValue(FEVER)) {
			return;
		}

		applyCoilState(level, pos, state.setValue(FEVER, false));
	}

	public static <T extends Block, P> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String material) {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();

			BlockModelBuilder off = models.withExistingParent("block/coil/%s/off".formatted(material), "block/cube_column")
					.texture("end", models.modLoc("block/coil/%s/top_off".formatted(material)))
					.texture("side", models.modLoc("block/coil/%s/side_off".formatted(material)));

			BlockModelBuilder on = models.withExistingParent("block/coil/%s/on".formatted(material), "block/cube_column")
					.texture("end", models.modLoc("block/coil/%s/top_on".formatted(material)))
					.texture("side", models.modLoc("block/coil/%s/side_on".formatted(material)));

			BlockModelBuilder idle = models.withExistingParent("block/coil/%s/idle".formatted(material), "block/cube_column")
					.texture("end", models.modLoc("block/coil/%s/top_idle".formatted(material)))
					.texture("side", models.modLoc("block/coil/%s/side_idle".formatted(material)));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						BlockModelBuilder model;

						if (state.getValue(FORMED)) {
							model = state.getValue(FEVER) ? on : off;
						} else {
							model = idle;
						}

						return ConfiguredModel.builder()
								.modelFile(model)
								.build();
					});
		};
	}
}