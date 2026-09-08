package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.config.*;
import net.minecraft.resources.ResourceLocation;

/**
 * 单方块蒸汽机器。
 * <p>
 * 用法:
 * new SingleSteamMachine(id("cmi:steam_press"))
 * .recipeType(id("cmi:steam_press_rt"))
 * .steamCapacity(16000)
 * .steamPerTick(2)
 * .model(off, on)
 * .build()
 */
public class SingleSteamMachine extends AbstractSteamMachine<SingleSteamMachine> {
	public SingleSteamMachine(ResourceLocation id) {
		super(id);
	}

	@Override
	protected MBDMachineDefinition buildDefinition(MachineState rootState, ConfigMachineSettings settings, ConfigRecipeLogicSettings logic) {
		MBDMachineDefinition.Builder builder = MBDMachineDefinition.builder();

		builder.id(id);
		builder.rootState(rootState);
		builder.blockProperties(ConfigBlockProperties.builder()
				.destroyTime(3.0f)
				.explosionResistance(6.0f)
				.rotationState(com.lowdragmc.mbd2.api.block.RotationState.NON_Y_AXIS)
				.build());
		builder.itemProperties(ConfigItemProperties.builder()
				.maxStackSize(64)
				.isGui3d(true)
				.build());
		builder.machineSettings(() -> settings);
		builder.recipeLogicSettings(logic);
		builder.partSettings(() -> {
			return ConfigPartSettings.builder()
					.enable(false)
					.build();
		});
		return builder.build();
	}
}
