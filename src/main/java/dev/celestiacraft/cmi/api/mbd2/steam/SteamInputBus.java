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
 * 蒸汽输入总线 (部件): 外部蒸汽经它泵入控制器蒸汽槽。
 * <p>
 * GT 风格多等级: 容量分级 = 总线自带蒸汽缓冲槽容量 (缓冲外部输入峰值),
 * 同时代理控制器蒸汽槽 (traitNameFilter="steam")。
 * <p>
 * 用法:
 * SteamInputBus.build(id("cmi:bronze_steam_input_bus"), "cmi:block/machine/io/bronze_steam_input", 4000);
 * SteamInputBus.build(id("cmi:steel_steam_input_bus"),  "cmi:block/machine/io/steel_steam_input",  16000);
 */
public class SteamInputBus {
	public static final String BUFFER_TRAIT_NAME = "steam_buffer";

	public static MBDMachineDefinition build(ResourceLocation id, String modelPath, int capacity) {
		MachineState base = MachineState.builder()
				.name("base")
				.modelRenderer(ResourceLocation.tryParse(modelPath))
				.shape(Shapes.block())
				.build();

		// 蒸汽缓冲槽: 总线自己的容量 (分级)
		FluidTankCapabilityTraitDefinition buffer = new FluidTankCapabilityTraitDefinition();

		buffer.setName(BUFFER_TRAIT_NAME);
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

		// 代理控制器蒸汽槽 (traitNameFilter 是 private 字段, 反射设置)
		ConfigPartSettings.ProxyCapability proxy = new ConfigPartSettings.ProxyCapability();

		AbstractSteamMachine.setPrivateField(proxy, "traitNameFilter", AbstractSteamMachine.STEAM_TRAIT_NAME);
		proxy.capabilityIO()
				.setInternal(IO.IN);
		proxy.capabilityIO()
				.setFrontIO(IO.IN);
		proxy.capabilityIO()
				.setBackIO(IO.NONE);
		proxy.capabilityIO()
				.setLeftIO(IO.NONE);
		proxy.capabilityIO()
				.setRightIO(IO.NONE);
		proxy.capabilityIO()
				.setTopIO(IO.NONE);
		proxy.capabilityIO()
				.setBottomIO(IO.NONE);
		proxy.autoIO()
				.setEnable(false);
		proxy.autoIO()
				.setInterval(20);

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
					.proxyControllerCapabilities(List.of(proxy))
					.build();
		});
		return builder.build();
	}
}
