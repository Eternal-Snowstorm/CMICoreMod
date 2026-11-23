package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.client.ponder.scene.ae2.Ae2GuiScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.AcceleratorScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.CrucibleScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.WaterPumpScene;
import dev.celestiacraft.cmi.client.ponder.scene.mekanism.CardboardBox;
import dev.celestiacraft.cmi.client.ponder.scene.mekanism.SpsScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.AlloyerScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.CannonScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.CastingScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.FoundryScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.HeaterScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.MelterScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.SmelteryScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.TankScene;
import dev.celestiacraft.cmi.client.ponder.scene.thermal.ThermalGuiScene;
import dev.celestiacraft.cmi.client.ponder.scene.vanilla.InfiniteLavaScene;
import dev.celestiacraft.libs.NebulaLibs;
import mekanism.common.registries.MekanismBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CmiPonderScene {
	public static void register(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		cmi(helper);
		mekanism(helper);
		tconstruct(helper);
		vanilla(helper);
	}

	private static void cmi(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItem.Cmi.SEA_WATER)
				.addStoryBoard(storyboard("cmi/water_well_sea_water"), WaterPumpScene::getSeaWater, CmiPonderTags.CMI);

		helper.forComponents(CmiPonderItem.Cmi.CRUCIBLE)
				.addStoryBoard(storyboard("cmi/crucible_usage"), CrucibleScene::usage, CmiPonderTags.CMI);

		helper.forComponents(CmiPonderItem.Cmi.ACCELERATOR)
				.addStoryBoard(storyboard("cmi/accelerator_usage"), AcceleratorScene::usage, CmiPonderTags.CMI);
	}

	private static void mekanism(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(MekanismBlocks.CARDBOARD_BOX.asItem())
				.addStoryBoard(NebulaLibs.loadResource("blank/5x5"), CardboardBox::usage, CmiPonderTags.MEKANISM);

		helper.forComponents(CmiPonderItem.Mekanism.SPS)
				.addStoryBoard(storyboard("mekanism/sps_building"), SpsScene::building, CmiPonderTags.MEKANISM);
	}

	private static void tconstruct(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItem.TConstruct.MELTER)
				.addStoryBoard(storyboard("tconstruct/melter_building"), MelterScene::building, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard(storyboard("tconstruct/melter_usage"), MelterScene::using, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.ALLOYER)
				.addStoryBoard(storyboard("tconstruct/alloyer_building"), AlloyerScene::building, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.HEATER)
				.addStoryBoard(storyboard("tconstruct/heater_usage"), HeaterScene::using, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.CASTING)
				.addStoryBoard(storyboard("tconstruct/casting"), CastingScene::cast, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.SAND)
				.addStoryBoard(storyboard("tconstruct/sand_casting"), CastingScene::sand, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.SMELTERY)
				.addStoryBoard(storyboard("tconstruct/smeltery_building"), SmelteryScene::building, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard(storyboard("tconstruct/smeltery_usage"), SmelteryScene::using, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard(storyboard("tconstruct/smeltery_mini"), SmelteryScene::mini, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.FOUNDRY)
				.addStoryBoard(storyboard("tconstruct/foundry_building"), FoundryScene::building, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.TANK)
				.addStoryBoard(storyboard("tconstruct/tank_usage"), TankScene::tank, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.CANNONS)
				.addStoryBoard(storyboard("tconstruct/fluid_cannon_usage"), CannonScene::using, CmiPonderTags.TCONSTRUCT);
	}

	private static void vanilla(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItem.Vanilla.INFINITE_LAVA)
				.addStoryBoard(storyboard("vanilla/infinite_lava"), InfiniteLavaScene::usage, CmiPonderTags.CMI);
	}

	/**
	 * 构造 CMI 命名空间下的 storyboard 路径
	 */
	private static ResourceLocation storyboard(String path) {
		return ResourceLocation.fromNamespaceAndPath(Cmi.MODID, path);
	}

	/**
	 * 构造 AE2 方块 ID
	 */
	private static ResourceLocation ae2Id(String path) {
		return ResourceLocation.fromNamespaceAndPath("ae2", path);
	}

	/**
	 * 构造热力方块 ID
	 */
	private static ResourceLocation thermalId(String path) {
		return ResourceLocation.fromNamespaceAndPath("thermal", path);
	}

	/**
	 * 构造热力扩展方块 ID
	 */
	private static ResourceLocation thermalExtraId(String path) {
		return ResourceLocation.fromNamespaceAndPath("thermal_extra", path);
	}

	/**
	 * AE2 场景挂载的方块
	 */
	private static final ResourceLocation[] AE2_COMPONENTS = {
			ae2Id("pattern_provider"),
			ae2Id("pattern_access_terminal"),
			ae2Id("pattern_encoding_terminal"),
			ae2Id("blank_pattern")
	};

	/**
	 * 热力场景挂载的机器
	 */
	private static final ResourceLocation[] THERMAL_COMPONENTS = {
			thermalId("machine_furnace"),
			thermalId("machine_sawmill"),
			thermalId("machine_pulverizer"),
			thermalId("machine_smelter"),
			thermalId("machine_insolator"),
			thermalId("machine_centrifuge"),
			thermalId("machine_press"),
			thermalId("machine_crucible"),
			thermalId("machine_chiller"),
			thermalId("machine_refinery"),
			thermalId("machine_pyrolyzer"),
			thermalId("machine_bottler"),
			thermalId("machine_brewer"),
			thermalId("machine_crystallizer"),
			thermalId("machine_crafter"),
			thermalExtraId("advanced_refinery"),
			thermalExtraId("nitratic_igniter"),
			thermalExtraId("fluid_mixer"),
			thermalExtraId("component_assembly"),
			thermalExtraId("endothermic_dehydrator")
	};

	/*
	 * AE2/热力场景不能经 cmi() 包装, 
	 * 否则会多出一层 cmi/ 前缀
	 */
	private static void ae2(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		helper.forComponents(AE2_COMPONENTS)
				.addStoryBoard(storyboard("ae2/me_pattern_manager"), Ae2GuiScene::patternManager, CmiPonderTags.CMI)
				.addStoryBoard(storyboard("ae2/me_interface_automation"), Ae2GuiScene::interfaceAutomation, CmiPonderTags.CMI);
	}

	private static void thermal(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		helper.forComponents(THERMAL_COMPONENTS)
				.addStoryBoard(storyboard("thermal/basic_machine_guidance"), ThermalGuiScene::basicMachineGuidance, CmiPonderTags.CMI)
				.addStoryBoard(storyboard("thermal/machine_automation"), ThermalGuiScene::machineAutomation, CmiPonderTags.CMI);
	}

	/**
	 * 注册 AE2 与热力的外部场景
	 * <p>
	 * 这些场景挂在第三方模组的方块上, 本模组不编译期依赖它们, 因此直接用方块 ID 注册. 
	 * 对应模组缺失时不会产生索引项。
	 */
	public static void registerByResourceLocation(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		ae2(helper);
		thermal(helper);
	}
}
