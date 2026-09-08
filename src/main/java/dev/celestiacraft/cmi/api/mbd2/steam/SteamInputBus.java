package dev.celestiacraft.cmi.api.mbd2.steam;

import com.lowdragmc.mbd2.MBD2;
import com.lowdragmc.mbd2.api.capability.recipe.IO;
import com.lowdragmc.mbd2.common.machine.definition.MBDMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.config.*;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTraitDefinition;
import dev.celestiacraft.cmi.tags.CmiFluidTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

/**
 * 蒸汽输入总线 (部件): 多方块的蒸汽仓 (GT 模型)。
 * <p>
 * 总线自带蒸汽槽 (name="steam"), 容量 = 等级参数; 多方块工作时从成型结构中
 * 所有总线的蒸汽槽并联抽取 (见 MultiBlockSteamMachine.findSteamStorages)。
 * <p>
 * 用法:
 * SteamInputBus.build(id("cmi:bronze_steam_input_bus"), "cmi:block/machine/io/bronze_steam_input", 4000);
 * SteamInputBus.build(id("cmi:steel_steam_input_bus"),  "cmi:block/machine/io/steel_steam_input",  16000);
 */
public class SteamInputBus {
	public static final String BUFFER_TRAIT_NAME = "steam";

	public static MBDMachineDefinition build(ResourceLocation id, String modelPath, int capacity) {
		MachineState base = MachineState.builder()
				.name("base")
				.modelRenderer(ResourceLocation.tryParse(modelPath))
				.shape(Shapes.block())
				.build();

		// 蒸汽仓: 总线自己的容量 (等级), 多方块消耗靠 trait 名 "steam" 找到它
		FluidTankCapabilityTraitDefinition buffer = new FluidTankCapabilityTraitDefinition();

		buffer.setName(BUFFER_TRAIT_NAME); // = AbstractSteamMachine.STEAM_TRAIT_NAME
		buffer.setPriority(0);
		buffer.setRecipeHandlerIO(IO.NONE);
		buffer.setGuiIO(IO.NONE);
		buffer.setTankSize(1);
		buffer.setCapacity(capacity);
		buffer.setAllowSameFluids(true);
		buffer.getFluidFilterSettings()
				.setFilterTags(List.of(CmiFluidTags.STEAM.location()));
		buffer.getCapabilityIO()
				.setInternal(IO.IN);
		buffer.getCapabilityIO()
				.setFrontIO(IO.IN);
		buffer.getCapabilityIO()
				.setBackIO(IO.IN);
		buffer.getCapabilityIO()
				.setLeftIO(IO.IN);
		buffer.getCapabilityIO()
				.setRightIO(IO.IN);
		buffer.getCapabilityIO()
				.setTopIO(IO.IN);
		buffer.getCapabilityIO()
				.setBottomIO(IO.IN);
		buffer.getAutoInput()
				.setEnable(false);
		buffer.getAutoOutput()
				.setEnable(false);

		ConfigMachineSettings settings = ConfigMachineSettings.builder()
				.hasUI(false)
				.dropMachineItem(true)
				.build();
		settings.addTraitDefinition(buffer);

		MBDMachineDefinition.Builder builder = MBDMachineDefinition.builder();

		builder.id(id);
		builder.rootState(base);
		builder.blockProperties(ConfigBlockProperties.builder()
				.destroyTime(2.0f)
				.build());
		builder.itemProperties(ConfigItemProperties.builder()
				.maxStackSize(64)
				.build());
		builder.machineSettings(() -> settings);
		builder.recipeLogicSettings(ConfigRecipeLogicSettings.builder()
				.enable(false)
				.recipeType(MBD2.id("dummy"))
				.build());
		builder.partSettings(() -> {
			return ConfigPartSettings.builder()
					.enable(true)
					.canShare(true)
					.build();
		});
		return builder.build();
	}
}
