package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.client.ponder.scene.cmi.CrucibleScene;
import dev.celestiacraft.cmi.client.ponder.scene.cmi.WaterPumpScene;
import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.*;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CmiPonderScenes {
	public static void register(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		cmi(helper);
		tconstruct(helper);
	}

	private static void cmi(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItems.Cmi.SEA_WATER)
				.addStoryBoard("cmi/sea_water", WaterPumpScene::seaWater);

		helper.forComponents(CmiPonderItems.Cmi.CRUCIBLE)
				.addStoryBoard("cmi/crucible_usage", CrucibleScene::usage);
	}

	private static void tconstruct(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(CmiPonderItems.TConstruct.MELTER)
				.addStoryBoard("tconstruct/melter_building", MelterScene::building, CmiPonderTags.SMELTERY)
				.addStoryBoard("tconstruct/melter_using", MelterScene::using, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.ALLOYER)
				.addStoryBoard("tconstruct/alloyer_building", AlloyerScene::building, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.HEATER)
				.addStoryBoard("tconstruct/heater_using", HeaterScene::using, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.CASTING)
				.addStoryBoard("tconstruct/casting", CastingScene::cast, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.SAND)
				.addStoryBoard("tconstruct/sand_casting", CastingScene::sand, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.SMELTERY)
				.addStoryBoard("tconstruct/smeltery_building", SmelteryScene::building, CmiPonderTags.SMELTERY)
				.addStoryBoard("tconstruct/smeltery_using", SmelteryScene::using, CmiPonderTags.SMELTERY)
				.addStoryBoard("tconstruct/smeltery_mini", SmelteryScene::mini, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.FOUNDRY)
				.addStoryBoard("tconstruct/foundry_building", FoundryScene::building, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.TANK)
				.addStoryBoard("tconstruct/tank", TankScene::tank, CmiPonderTags.SMELTERY);

		helper.forComponents(CmiPonderItems.TConstruct.CANNONS)
				.addStoryBoard("tconstruct/fluid_cannon", CannonScene::using, CmiPonderTags.SMELTERY);
	}
}