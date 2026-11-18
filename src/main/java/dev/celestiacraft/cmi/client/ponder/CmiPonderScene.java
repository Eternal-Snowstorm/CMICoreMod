package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.client.ponder.scene.ae2.Ae2GuiScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.AcceleratorScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.CrucibleScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.WaterPumpScene;
import dev.celestiacraft.cmi.client.ponder.scene.mekanism.CardboardBox;
import dev.celestiacraft.cmi.client.ponder.scene.mekanism.SpsScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.*;
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
				.addStoryBoard(cmi("sea_water"), WaterPumpScene::getSeaWater, CmiPonderTags.CMI);

		helper.forComponents(CmiPonderItem.Cmi.CRUCIBLE)
				.addStoryBoard(cmi("crucible_usage"), CrucibleScene::usage, CmiPonderTags.CMI);

		helper.forComponents(CmiPonderItem.Cmi.ACCELERATOR)
				.addStoryBoard(cmi("accelerator"), AcceleratorScene::usage, CmiPonderTags.CMI);
	}

	private static String cmi(String path) {
		return String.format("cmi/%s", path);
	}

	private static void mekanism(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(MekanismBlocks.CARDBOARD_BOX.asItem())
				.addStoryBoard(NebulaLibs.loadResource("blank/5x5"), CardboardBox::usage, CmiPonderTags.MEKANISM);

		helper.forComponents(CmiPonderItem.Mekanism.SPS)
				.addStoryBoard(mekanism("sps_building"), SpsScene::building, CmiPonderTags.MEKANISM);
	}

	private static String mekanism(String path) {
		return String.format("mekanism/%s", path);
	}

	private static void tconstruct(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItem.TConstruct.MELTER)
				.addStoryBoard(tconstruct("melter_building"), MelterScene::building, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard(tconstruct("melter_using"), MelterScene::using, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.ALLOYER)
				.addStoryBoard(tconstruct("alloyer_building"), AlloyerScene::building, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.HEATER)
				.addStoryBoard(tconstruct("heater_using"), HeaterScene::using, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.CASTING)
				.addStoryBoard(tconstruct("casting"), CastingScene::cast, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.SAND)
				.addStoryBoard(tconstruct("sand_casting"), CastingScene::sand, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.SMELTERY)
				.addStoryBoard(tconstruct("smeltery_building"), SmelteryScene::building, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard(tconstruct("smeltery_using"), SmelteryScene::using, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard(tconstruct("smeltery_mini"), SmelteryScene::mini, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.FOUNDRY)
				.addStoryBoard(tconstruct("foundry_building"), FoundryScene::building, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.TANK)
				.addStoryBoard(tconstruct("tank"), TankScene::tank, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.CANNONS)
				.addStoryBoard(tconstruct("fluid_cannon"), CannonScene::using, CmiPonderTags.TCONSTRUCT);
	}

	private static String tconstruct(String path) {
		return String.format("tconstruct/%s", path);
	}

	private static void vanilla(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItem.Vanilla.INFINITE_LAVA)
				.addStoryBoard(vanilla("infinite_lava"), InfiniteLavaScene::usage, CmiPonderTags.CMI);
	}

	private static String vanilla(String path) {
		return String.format("vanilla/%s", path);
	}

	/** AE2 相关方块：不引入编译期依赖，直接用 ResourceLocation 作为场景键。 */
	public static final ResourceLocation[] AE2_COMPONENTS = {
			ResourceLocation.fromNamespaceAndPath("ae2", "pattern_provider"),
			ResourceLocation.fromNamespaceAndPath("ae2", "pattern_access_terminal"),
			ResourceLocation.fromNamespaceAndPath("ae2", "pattern_encoding_terminal"),
			ResourceLocation.fromNamespaceAndPath("ae2", "blank_pattern")
	};

	/** 热力系列机器：同样用 ResourceLocation 作为场景键。 */
	public static final ResourceLocation[] THERMAL_COMPONENTS = {
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_furnace"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_sawmill"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_pulverizer"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_smelter"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_insolator"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_centrifuge"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_press"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_crucible"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_chiller"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_refinery"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_pyrolyzer"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_bottler"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_brewer"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_crystallizer"),
			ResourceLocation.fromNamespaceAndPath("thermal", "machine_crafter"),
			ResourceLocation.fromNamespaceAndPath("thermal_extra", "advanced_refinery"),
			ResourceLocation.fromNamespaceAndPath("thermal_extra", "nitratic_igniter"),
			ResourceLocation.fromNamespaceAndPath("thermal_extra", "fluid_mixer"),
			ResourceLocation.fromNamespaceAndPath("thermal_extra", "component_assembly"),
			ResourceLocation.fromNamespaceAndPath("thermal_extra", "endothermic_dehydrator")
	};

	/*
	 * 注意这里不要用 cmi(...) 包一层：cmi(String) 会额外加上 "cmi/" 前缀，
	 * 而 addStoryBoard(String) 内部是 asLocation(path) -> new ResourceLocation("cmi", path)，
	 * 于是 cmi("ae2/ae2_gui1") 会被解析成 cmi:cmi/ae2/ae2_gui1，
	 * 对应文件 assets/cmi/ponder/cmi/ae2/ae2_gui1.nbt —— 这个文件并不存在。
	 * 结果 Ponder 只打一条 "Ponder schematic missing" 并塞一个空结构，
	 * 场景里就是一片空白。直接用 "ae2/ae2_gui1" 才能命中 ponder/ae2/ae2_gui1.nbt。
	 */
	private static void ae2(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		helper.forComponents(AE2_COMPONENTS)
				.addStoryBoard("ae2/ae2_gui1", Ae2GuiScene::scene1, CmiPonderTags.CMI)
				.addStoryBoard("ae2/ae2_gui2", Ae2GuiScene::scene2, CmiPonderTags.CMI);
	}

	private static void thermal(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		helper.forComponents(THERMAL_COMPONENTS)
				.addStoryBoard("thermal/thermal_gui", ThermalGuiScene::scene1, CmiPonderTags.CMI)
				.addStoryBoard("thermal/thermal_gui2", ThermalGuiScene::scene2, CmiPonderTags.CMI);
	}

	/**
	 * 迁移自 KubeJS 的 AE2 / 热力界面思索场景。
	 * <p>
	 * 这些场景挂在第三方 mod 的方块上，但本模组并不编译期依赖它们，
	 * 因此直接用 {@link ResourceLocation} 作为场景键注册：Ponder 的场景注册表
	 * 本身就是以 id 为键的，方块不存在时对应条目不会被索引，也不会报错。
	 */
	public static void registerByResourceLocation(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		ae2(helper);
		thermal(helper);
	}
}