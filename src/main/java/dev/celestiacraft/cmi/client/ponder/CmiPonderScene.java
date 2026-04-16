package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.client.ponder.scene.cmi.AcceleratorScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.CrucibleScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.WaterPumpScene;
import dev.celestiacraft.cmi.client.ponder.scene.mekanism.CardboardBox;
import dev.celestiacraft.cmi.client.ponder.scene.mekanism.SpsScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.*;
import dev.celestiacraft.libs.NebulaLibs;
import mekanism.common.registries.MekanismBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CmiPonderScene {
	public static void register(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		cmi(helper);
		mekanism(helper);
		tconstruct(helper);
	}

	private static void cmi(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItem.Cmi.SEA_WATER)
				.addStoryBoard("cmi/sea_water", WaterPumpScene::seaWater, CmiPonderTags.CMI);

		helper.forComponents(CmiPonderItem.Cmi.CRUCIBLE)
				.addStoryBoard("cmi/crucible_usage", CrucibleScene::usage, CmiPonderTags.CMI);

		helper.forComponents(CmiPonderItem.Cmi.ACCELERATOR)
				.addStoryBoard("cmi/accelerator", AcceleratorScene::usage, CmiPonderTags.CMI);
	}

	private static void mekanism(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(MekanismBlocks.CARDBOARD_BOX.asItem())
				.addStoryBoard(NebulaLibs.loadResource("blank/5x5"), CardboardBox::usage, CmiPonderTags.MEKANISM);

		helper.forComponents(CmiPonderItem.Mekanism.SPS)
				.addStoryBoard("mekanism/sps_building", SpsScene::building, CmiPonderTags.MEKANISM);
	}

	private static void tconstruct(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItem.TConstruct.MELTER)
				.addStoryBoard("tconstruct/melter_building", MelterScene::building, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard("tconstruct/melter_using", MelterScene::using, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.ALLOYER)
				.addStoryBoard("tconstruct/alloyer_building", AlloyerScene::building, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.HEATER)
				.addStoryBoard("tconstruct/heater_using", HeaterScene::using, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.CASTING)
				.addStoryBoard("tconstruct/casting", CastingScene::cast, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.SAND)
				.addStoryBoard("tconstruct/sand_casting", CastingScene::sand, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.SMELTERY)
				.addStoryBoard("tconstruct/smeltery_building", SmelteryScene::building, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard("tconstruct/smeltery_using", SmelteryScene::using, CmiPonderTags.TCONSTRUCT)
				.addStoryBoard("tconstruct/smeltery_mini", SmelteryScene::mini, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.FOUNDRY)
				.addStoryBoard("tconstruct/foundry_building", FoundryScene::building, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.TANK)
				.addStoryBoard("tconstruct/tank", TankScene::tank, CmiPonderTags.TCONSTRUCT);

		helper.forComponents(CmiPonderItem.TConstruct.CANNONS)
				.addStoryBoard("tconstruct/fluid_cannon", CannonScene::using, CmiPonderTags.TCONSTRUCT);
	}
}