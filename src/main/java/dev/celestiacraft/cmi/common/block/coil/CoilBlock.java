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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoilBlock extends BasicBlock {
	public static final BooleanProperty FEVER = BooleanProperty.create("fever");
	private static final Map<MBDMachine, Set<BlockPos>> FEVER_COILS = new WeakHashMap<>();

	private static boolean updating;

	public CoilBlock(Properties properties) {
		super(properties.strength(3, 3)
				.requiresCorrectToolForDrops()
				.mapColor(MapColor.METAL)
				.sound(SoundType.METAL)
				.lightLevel(litBlockEmission(FEVER, 15)));
		registerDefaultState(defaultBlockState()
				.setValue(FEVER, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FEVER);
	}

	@SubscribeEvent
	public static void onStructureFormed(MachineStructureFormedEvent event) {
		MBDMachine machine = event.getMachine();

		if (updating || !(machine instanceof MBDMultiblockMachine multiblock)) {
			return;
		}

		Level level = machine.getLevel();

		if (level == null || level.isClientSide) {
			return;
		}

		// 成型时 MultiblockState 的 cache 一定有效, 趁这时候把线圈位置记下来并设好状态
		boolean fever = machine.getRecipeLogic().getStatus().equals(RecipeLogic.Status.WORKING);
		Set<BlockPos> positions = write(level, multiblock, fever);

		if (!positions.isEmpty()) {
			FEVER_COILS.put(machine, positions);
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
		setCoils(event.getMachine(), false);
		FEVER_COILS.remove(event.getMachine());
	}

	@SubscribeEvent
	public static void onMachineRemoved(MachineRemovedEvent event) {
		setCoils(event.getMachine(), false);
		FEVER_COILS.remove(event.getMachine());
	}

	private static void setCoils(MBDMachine machine, boolean fever) {
		if (updating || !(machine instanceof MBDMultiblockMachine multiblock)) {
			return;
		}

		Level level = machine.getLevel();

		if (level == null || level.isClientSide) {
			return;
		}

		Set<BlockPos> positions = FEVER_COILS.get(machine);

		if (positions == null) {
			// 还没有记录 (没成型过 / 记录被清了): 扫一遍结构缓存
			Set<BlockPos> collected = write(level, multiblock, fever);

			if (fever && !collected.isEmpty()) {
				FEVER_COILS.put(machine, collected);
			}

			return;
		}

		updating = true;

		try {
			for (BlockPos pos : positions) {
				setFever(level, pos, fever);
			}
		} finally {
			updating = false;
		}
	}

	private static Set<BlockPos> write(Level level, MBDMultiblockMachine multiblock, boolean fever) {
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
			if (setFever(level, pos, fever)) {
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

	private static boolean setFever(Level level, BlockPos pos, boolean fever) {
		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof ProxyPartBlockEntity proxy) {
			BlockState original = proxy.getOriginalState();

			if (original == null || !(original.getBlock() instanceof CoilBlock)) {
				return false;
			}

			if (!original.getValue(FEVER).equals(fever)) {
				proxy.setOriginalData(original.setValue(FEVER, fever), proxy.getOriginalData(), proxy.getControllerPos());
			}

			return true;
		}

		BlockState state = level.getBlockState(pos);

		if (!(state.getBlock() instanceof CoilBlock)) {
			return false;
		}

		if (!state.getValue(FEVER).equals(fever)) {
			level.setBlockAndUpdate(pos, state.setValue(FEVER, fever));
		}

		return true;
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

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						return ConfiguredModel.builder()
								.modelFile(state.getValue(FEVER) ? on : off)
								.build();
					});
		};
	}
}